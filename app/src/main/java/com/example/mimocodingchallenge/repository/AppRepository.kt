package com.example.mimocodingchallenge.repository

class AppRepository(private val apis: AppApis?) : BaseRepository() {
    suspend fun fetchLessons() = safeApiCall {
        apis!!.getAllLessons()
    }
}