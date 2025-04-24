package com.coda.situlearner.core.model.data

@androidx.annotation.Keep
enum class Language {
    Unknown,
    Chinese,
    English,
    Japanese;

    companion object {
        val validLanguages by lazy {
            Language.entries.filter { it != Unknown }.also {
                // at least two known languages: target and source languages
                check(it.size >= 2)
            }
        }
    }
}