package com.example.mimocodingchallenge.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mimocodingchallenge.db.AppDatabase
import com.example.mimocodingchallenge.helpers.Lesson
import com.example.mimocodingchallenge.helpers.LessonCompleteModel
import com.example.mimocodingchallenge.helpers.LessonsDataClass
import com.example.mimocodingchallenge.repository.AppRepository
import com.example.mimocodingchallenge.repository.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LessonsViewModel(val appRepository: AppRepository) : ViewModel() {
    private val _lessonsResponse: MutableLiveData<Resource<LessonsDataClass>> =
        MutableLiveData()

    private var _lessonsDataObserver: MutableLiveData<LessonsDataClass> = MutableLiveData()
    val lessonsDataObserver: LiveData<LessonsDataClass> get() = _lessonsDataObserver

    private var _lessonEventObserver: MutableLiveData<List<LessonCompleteModel>> = MutableLiveData()
    val lessonCompletionEvent: LiveData<List<LessonCompleteModel>> get() = _lessonEventObserver

    var lessonsLength = 0;
    var currentLessonIndex = 0;
    fun fetchLessons() {
        viewModelScope.launch {
            _lessonsResponse.value = appRepository.fetchLessons()
            try {
                _lessonsDataObserver.value = (_lessonsResponse.value as Resource.Success).value
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun fetchAllCompletedLessons(db: AppDatabase) {
        CoroutineScope(Dispatchers.IO).launch {
            _lessonEventObserver.postValue(db.lessonDao().getAll())
        }
    }

    fun updateLessonInDB(db: AppDatabase, lesson: Lesson, lessonStartTime: String?) {
        val lessonEndTime = getDateTime()
        GlobalScope.launch {
            db.lessonDao().insert(LessonCompleteModel(lesson.id,lessonStartTime,lessonEndTime))
        }
    }

    fun getDateTime(): String? {
        val c = Calendar.getInstance()
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return df.format(c.time)
    }
}