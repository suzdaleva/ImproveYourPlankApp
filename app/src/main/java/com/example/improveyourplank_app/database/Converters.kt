package com.example.improveyourplank_app.database

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long): LocalDateTime? {
        return Date(value).toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long {
        return Date.from(date?.atZone(ZoneId.systemDefault())?.toInstant()).time
    }
}