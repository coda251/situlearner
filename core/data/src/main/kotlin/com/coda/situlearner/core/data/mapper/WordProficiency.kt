package com.coda.situlearner.core.data.mapper

import com.coda.situlearner.core.database.model.Beginner
import com.coda.situlearner.core.database.model.Intermediate
import com.coda.situlearner.core.database.model.Proficient
import com.coda.situlearner.core.database.model.Unset
import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.database.model.WordProficiency as WordProficiencyValue

internal fun WordProficiencyValue.asExternalModel() = when (this) {
    Beginner -> WordProficiency.Beginner
    Intermediate -> WordProficiency.Intermediate
    Proficient -> WordProficiency.Proficient
    else -> WordProficiency.Unset
}

internal fun WordProficiency.asValue() = when (this) {
    WordProficiency.Unset -> Unset
    WordProficiency.Beginner -> Beginner
    WordProficiency.Intermediate -> Intermediate
    WordProficiency.Proficient -> Proficient
}