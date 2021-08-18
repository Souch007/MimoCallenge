package com.example.mimocodingchallenge.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mimocodingchallenge.repository.AppRepository
import com.example.mimocodingchallenge.repository.BaseRepository
import com.example.mimocodingchallenge.viewmodel.LessonsViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val repository: BaseRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LessonsViewModel::class.java) -> LessonsViewModel(repository as AppRepository) as T
            else -> throw IllegalArgumentException("ViewModel Class Not Found")
        }
    }
}