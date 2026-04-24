package com.coda.situlearner.feature.word.search

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.MediaRepository
import com.coda.situlearner.core.data.repository.UserPreferenceRepository
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.MediaCollectionWithFiles
import com.coda.situlearner.feature.word.search.model.SearchResult
import com.coda.situlearner.infra.subkit.processor.Processor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.chunked
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take

internal class SearchViewModel(
    mediaRepository: MediaRepository,
    preferenceRepository: UserPreferenceRepository,
    private val processor: Processor
) : ViewModel() {

    private val _queryWord = MutableStateFlow("")
    val queryWord = _queryWord.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = combine(
        preferenceRepository.userPreference.map { it.wordLibraryLanguage },
        _queryWord.filter { it.isNotBlank() }.distinctUntilChanged()
    ) { l, q -> l to q }
        .flatMapLatest { (language, query) ->
            flow {
                val existed = mutableSetOf<Int>()
                // currently full scan mode, in the future we may consider:
                // 1. add language property to file or collection for quick filtering
                // 2. scan in targeted files
                // 3. index
                val collectionWithFiles = mediaRepository.getAllMediaCollectionWithFiles()
                val limits = collectionWithFiles.flatMap { it.files }.size * 5.coerceAtMost(500)
                var hasResult = false
                performSearch(query, language, collectionWithFiles)
                    .filter {
                        val fingerprint = it.subtitle.sourceText.trim().lowercase().hashCode()
                        existed.add(fingerprint)
                    }
                    .take(limits)
                    .chunked(10)
                    .scan(emptyList()) { acc, chunk -> acc + chunk }
                    .map { list ->
                        if (list.isEmpty()) UiState.Loading
                        else {
                            hasResult = true
                            UiState.Success(list)
                        }
                    }
                    .onStart { emit(UiState.Loading) }
                    .onCompletion {
                        if (it == null && !hasResult) {
                            emit(UiState.Empty)
                        }
                    }
                    .collect {
                        emit(it)
                    }
            }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = UiState.Idle
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun performSearch(
        q: String,
        language: Language,
        collectionWithFiles: List<MediaCollectionWithFiles>
    ) = flow {
        collectionWithFiles.forEach { c ->
            c.files.forEach { file ->
                val path = file.subtitleUrl?.toUri()?.path ?: return@forEach
                val content = processor.load(path) ?: return@forEach
                if (content.sourceLanguage != language) return@forEach

                content.subtitles.forEach { subtitle ->
                    // 1. token match
                    val tokenMatch =
                        subtitle.tokens?.firstOrNull { it.lemma.equals(q, ignoreCase = true) }

                    // 2. subtitle match
                    val startIndex =
                        tokenMatch?.startIndex ?: subtitle.sourceText.indexOf(q, ignoreCase = true)

                    if (startIndex != -1) {
                        val endIndex = tokenMatch?.endIndex ?: (startIndex + q.length)
                        emit(
                            SearchResult(
                                collection = c.collection,
                                file = file,
                                subtitle = subtitle.copy(tokens = null),
                                start = startIndex,
                                end = endIndex
                            )
                        )
                    }
                }
            }
        }
    }

    fun search(word: String) {
        _queryWord.value = word
    }
}

internal sealed interface UiState {
    data object Idle : UiState
    data object Loading : UiState
    data object Empty : UiState
    data class Success(val results: List<SearchResult>) : UiState
}