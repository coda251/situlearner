package com.coda.situlearner.core.model.domain

import com.coda.situlearner.core.model.data.WordCategoryType

data class WordCategoryList(
    val categoryType: WordCategoryType,
    val categories: List<WordCategory>
) : List<WordCategory> by categories {

    inline fun <reified T : WordCategory> asTypedCategoryList(): List<T> =
        categories.filterIsInstance<T>()
}