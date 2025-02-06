package com.coda.situlearner

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.UserPreferenceRepository
import com.coda.situlearner.core.model.data.DarkThemeMode
import com.coda.situlearner.core.model.data.PlaylistType
import com.coda.situlearner.core.model.data.ThemeColorMode
import com.coda.situlearner.core.ui.theme.themeColorFromImage
import com.coda.situlearner.infra.player.PlayerState
import com.coda.situlearner.infra.player.PlayerStateProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainActivityViewModel(
    applicationContext: Context,
    userPreferenceRepository: UserPreferenceRepository
) : ViewModel() {
    private val thumbnailColorFlowProvider = ThumbnailColorFlowProvider(applicationContext)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = combine(
        userPreferenceRepository.userPreference.map { it.darkThemeMode }.distinctUntilChanged(),
        userPreferenceRepository.userPreference.map { it.themeColorMode }.distinctUntilChanged()
            .flatMapLatest { colorMode ->
                when (colorMode) {
                    ThemeColorMode.DynamicWithThumbnail ->
                        thumbnailColorFlowProvider.provideColorFlow(PlayerStateProvider.state)
                            .map { Pair(colorMode, it) }

                    else -> flow { emit(Pair(colorMode, Color.Unspecified)) }
                }
            }
    ) { darkMode, modeAndColor ->
        MainActivityUiState.Success(
            darkThemeMode = darkMode,
            themeColorMode = modeAndColor.first,
            themeColor = modeAndColor.second
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = MainActivityUiState.Loading,
    )
}

private class ThumbnailColorFlowProvider(private val context: Context) {
    private val cachedUrlToColor: MutableMap<String, Color> = mutableMapOf()

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun provideColorFlow(
        playerState: Flow<PlayerState>,
    ): Flow<Color> = playerState.flatMapLatest { state ->
        state.playlistType.flatMapLatest {
            when (it) {
                PlaylistType.Persistent -> {
                    state.playlist.map { playlist ->
                        playlist.currentItem?.thumbnailUrl?.let { url ->
                            cachedUrlToColor[url] ?: run {
                                themeColorFromImage(url, context)?.apply {
                                    cachedUrlToColor[url] = this
                                }
                            }
                        } ?: run {
                            // only when no other items with thumbnail have been played,
                            // we emit a Color.Unspecified
                            if (cachedUrlToColor.isEmpty()) {
                                Color.Unspecified
                            } else null
                        }
                    }.filterNotNull()
                }

                // when playlistType goes to temporary, we do not emit value
                // to avoid color change animation
                PlaylistType.Temporary -> emptyFlow()
            }
        }
    }
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(
        val darkThemeMode: DarkThemeMode,
        val themeColorMode: ThemeColorMode,
        val themeColor: Color,
    ) : MainActivityUiState
}