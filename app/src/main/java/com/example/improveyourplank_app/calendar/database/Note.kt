package com.myapp.annoteapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName="notes_table")
data class Note (
    @PrimaryKey(autoGenerate = true)
    var noteId : Long= 0L,

    @ColumnInfo(name = "date")
    var date: Calendar = Calendar.getInstance(),

    @ColumnInfo(name = "note")
    var note: String = "",

    @ColumnInfo(name = "modify_date")
    var modify_date: Calendar = Calendar.getInstance()
)
