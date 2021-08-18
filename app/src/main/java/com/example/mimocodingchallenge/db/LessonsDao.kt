package com.example.mimocodingchallenge.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.mimocodingchallenge.helpers.LessonCompleteModel

@Dao
interface LessonsDao {
    @Query("Select * from lessonsRecord")
    fun getAll(): List<LessonCompleteModel>

    @Insert(onConflict = REPLACE)
    fun insert(vararg lesson: LessonCompleteModel)
}