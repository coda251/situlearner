package com.coda.situlearner.core.database.util

import androidx.room.TypeConverter
import com.coda.situlearner.core.database.model.Meanings
import kotlinx.serialization.json.Json

internal class MeaningsConverter {

    @TypeConverter
    fun stringToMeanings(string: String?): Meanings? = string?.let(Json::decodeFromString)

    @TypeConverter
    fun meaningsToString(meanings: Meanings?): String? = meanings?.let(Json::encodeToString)
}