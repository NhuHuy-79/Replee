package com.nhuhuy.replee.core.database.converter

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CoreConverter {
    private val json = Json

    @TypeConverter
    fun fromListToString(list: List<String>): String {
        return json.encodeToString(list)
    }

    @TypeConverter
    fun fromStringToList(data: String): List<String> {
        return json.decodeFromString(data)
    }
}