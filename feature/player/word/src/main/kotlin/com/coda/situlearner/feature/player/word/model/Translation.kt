package com.coda.situlearner.feature.player.word.model

import com.coda.situlearner.infra.subkit.translator.Translator
import com.coda.situlearner.infra.subkit.translator.WordTranslationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

internal data class Translation(
    val translator: Translator,
    val infoState: RemoteWordInfoState,
)

internal fun Translator.toTranslationFlow(word: String): Flow<Translation> =
    query(word).map { result ->
        val infoState = when (result) {
            WordTranslationResult.Empty -> RemoteWordInfoState.Empty
            WordTranslationResult.Loading -> RemoteWordInfoState.Loading
            WordTranslationResult.Error -> RemoteWordInfoState.Error
            is WordTranslationResult.Success -> {
                result.infos.singleOrNull()?.let {
                    RemoteWordInfoState.Single(it)
                } ?: RemoteWordInfoState.Multiple(
                    infos = result.infos,
                )
            }
        }
        Translation(this, infoState)
    }.onStart {
        emit(
            Translation(
                translator = this@toTranslationFlow,
                infoState = RemoteWordInfoState.Loading
            )
        )
    }