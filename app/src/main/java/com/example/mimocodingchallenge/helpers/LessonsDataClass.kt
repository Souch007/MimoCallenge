package com.example.mimocodingchallenge.helpers

data class LessonsDataClass(
    val lessons: List<Lesson>,
    var lessonState : Boolean? = false,
)