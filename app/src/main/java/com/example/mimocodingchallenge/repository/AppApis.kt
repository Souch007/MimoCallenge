package com.example.mimocodingchallenge.repository

import com.example.mimocodingchallenge.helpers.AppConstants
import com.example.mimocodingchallenge.helpers.LessonsDataClass
import retrofit2.http.GET

interface AppApis {
    @GET(AppConstants.LESSONS)
    suspend fun getAllLessons() : LessonsDataClass
}