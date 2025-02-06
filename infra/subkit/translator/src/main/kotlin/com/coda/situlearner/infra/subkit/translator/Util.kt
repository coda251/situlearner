package com.coda.situlearner.infra.subkit.translator

import com.coda.situlearner.core.model.data.WordMeaning

internal fun List<WordMeaning>.simplify(): List<WordMeaning> {
    val posToDefinition = mutableMapOf<String, String>()
    forEach {
        var newDefinition = it.definition
        if (it.partOfSpeechTag in posToDefinition.keys) {
            posToDefinition[it.partOfSpeechTag]?.let { originalDefinition ->
                newDefinition = originalDefinition + "\n" + newDefinition
            }
        }
        posToDefinition[it.partOfSpeechTag] = newDefinition
    }

    return buildList {
        posToDefinition.forEach {
            add(
                WordMeaning(
                    partOfSpeechTag = it.key,
                    definition = it.value,
                )
            )
        }
    }
}