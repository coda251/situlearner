package com.coda.situlearner.core.data.mapper

import com.coda.situlearner.core.datastore.WordBookSortByProto
import com.coda.situlearner.core.model.data.WordBookSortBy

internal fun WordBookSortBy.asProto(): WordBookSortByProto = when (this) {
    WordBookSortBy.Count -> WordBookSortByProto.WORD_BOOK_SORT_BY_COUNT
    WordBookSortBy.UpdatedDate -> WordBookSortByProto.WORD_BOOK_SORT_BY_UPDATED_DATE
}

internal fun WordBookSortByProto.asExternalModel(): WordBookSortBy = when (this) {
    WordBookSortByProto.UNRECOGNIZED, WordBookSortByProto.WORD_BOOK_SORT_BY_COUNT ->
        WordBookSortBy.Count
    WordBookSortByProto.WORD_BOOK_SORT_BY_UPDATED_DATE -> WordBookSortBy.UpdatedDate
}