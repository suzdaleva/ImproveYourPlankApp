package com.myapp.annoteapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
interface NoteDatabaseDao {
    @Insert
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM notes_table WHERE noteId= :key")
    suspend fun get(key: Long) : Note

    @Query("SELECT * FROM notes_table WHERE date= :date")
    suspend fun getNoteWithDate(date: Calendar) : Note?

    @Query("SELECT * FROM notes_table ORDER BY noteId DESC")
    suspend fun getAllNotes() : List<Note>

    @Query("SELECT EXISTS (SELECT 1 FROM notes_table WHERE date = :date)")
    suspend fun exists(date: Calendar): Boolean


    @Query("SELECT * FROM notes_table ORDER BY noteId DESC LIMIT 1")
    suspend fun getThisNote(): Note?

    @Query("DELETE FROM notes_table WHERE noteId = (SELECT Max(noteId) FROM notes_table)")
    suspend fun deleteThisNote()

    @Query("DELETE FROM notes_table WHERE date = :date")
    suspend fun deleteEmptyNote(date: Calendar)

    @Query("DELETE FROM notes_table")
    suspend fun clear()

    @Query("SELECT * from notes_table WHERE noteId = :key")
    fun getNoteWithId(key: Long): LiveData<Note>

    @Query("SELECT * from notes_table ORDER BY modify_date DESC LIMIT 1")
    suspend fun getLastUpdatedNote(): Note?
}