package com.example.mimocodingchallenge.helpers

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "lessonsRecord")
data class LessonCompleteModel(
    @PrimaryKey(autoGenerate = false)
    val lessonId: Int? = null,

    @ColumnInfo(name = "lessonStartTime")
    val lessonStartTime: String? = null,

    @ColumnInfo(name = "lessonCompleteTime")
    val lessonCompleteTime: String? = null
) : Serializable {


}