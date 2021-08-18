package com.example.mimocodingchallenge

import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.mimocodingchallenge.databinding.ActivityParentBinding
import com.example.mimocodingchallenge.factory.ViewModelFactory
import com.example.mimocodingchallenge.repository.AppApis
import com.example.mimocodingchallenge.repository.AppRepository
import com.example.mimocodingchallenge.repository.RemoteDataSource
import com.example.mimocodingchallenge.viewmodel.LessonsViewModel
import com.example.mimocodingchallenge.db.AppDatabase


class MimoParentActivity : AppCompatActivity() {
    private lateinit var activityParentMimoBinding: ActivityParentBinding
    private lateinit var lessonsViewModel: LessonsViewModel
    private lateinit var db: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityParentMimoBinding = DataBindingUtil.setContentView(this, R.layout.activity_parent)
        db = AppDatabase(this)
        initializeViewModel()
        initObservers()
    }

    private fun initObservers() {
        lessonsViewModel.lessonsDataObserver.observe(this, {
            if (it != null) {
                lessonsViewModel.lessonsLength =
                    lessonsViewModel.lessonsDataObserver.value!!.lessons.size
                updateLessonsView(lessonsViewModel.currentLessonIndex)
            }
        })

        lessonsViewModel.lessonCompletionEvent.observe(this, {
            when {
                it != null -> {
                    for (lessons in it) {
                        val lessonsDetail = TextView(this)
                        lessonsDetail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30F)
                        lessonsDetail.setPadding(15, 15, 15, 15)
                        lessonsDetail.text =
                            "Start Time : " + lessons.lessonStartTime + " - \n End Time : " + lessons.lessonStartTime
                        lessonsDetail.setTextColor( ContextCompat.getColor(
                            applicationContext,
                            R.color.purple_500
                        ))
                        activityParentMimoBinding.parentLl.addView(lessonsDetail)
                    }
                }
            }
        })
    }

    private fun updateLessonsView(index: Int) {
        val lessonStartTime = lessonsViewModel.getDateTime()
        val lesson = lessonsViewModel.lessonsDataObserver.value!!.lessons[index]
        val codingView: LinearLayout = activityParentMimoBinding.lessonsView
        val parentView: LinearLayout = activityParentMimoBinding.parent
        var correctAnswer = ""
        codingView.removeAllViews()
        parentView.removeAllViews()
        val inputState = lesson.input
        var userInputAnswer: EditText? = null
        var startIndex = -1
        val totalAnswerLength: Int
        val submitLessonButton = Button(this)
        submitLessonButton.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        parentView.addView(
            submitLessonButton,
        )
        submitLessonButton.setBackgroundColor(Color.TRANSPARENT)
        submitLessonButton.text = this.resources.getString(R.string.done)
        submitLessonButton.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.purple_200
            )
        )

        submitLessonButton.setTextColor(Color.WHITE)
        submitLessonButton.isEnabled = false
        if (inputState != null) {
            userInputAnswer = EditText(this)
            userInputAnswer.setTextColor(Color.parseColor("#FF000000"))
            userInputAnswer.setPadding(15, 15, 15, 15)
            userInputAnswer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)
            userInputAnswer.width = 200
            userInputAnswer.background = (
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ed_background
                )
            )
            startIndex = lesson.input.startIndex
            totalAnswerLength = lesson.input.endIndex - lesson.input.startIndex
            userInputAnswer.filters =
                arrayOf<InputFilter>(InputFilter.LengthFilter(totalAnswerLength))
            userInputAnswer.doOnTextChanged { text, _, _, _ ->
                if (text.toString().matches(correctAnswer.toRegex())) {
                    submitLessonButton.isEnabled = true
                    submitLessonButton.setBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.purple_500
                        )
                    )
                } else {
                    submitLessonButton.isEnabled = false
                    submitLessonButton.setBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.purple_200
                        )
                    )
                }
            }
        } else {
            submitLessonButton.isEnabled = true
            submitLessonButton.setBackgroundColor( ContextCompat.getColor(
                applicationContext,
                R.color.purple_500
            ))
        }
        submitLessonButton.setOnClickListener {
            lessonsViewModel.updateLessonInDB(db, lesson, lessonStartTime)
            lessonsViewModel.currentLessonIndex += 1
            if (lessonsViewModel.currentLessonIndex < lessonsViewModel.lessonsLength) {
                updateLessonsView(lessonsViewModel.currentLessonIndex)
            } else {
                codingView.removeAllViews()
                parentView.removeAllViews()
                val message= TextView(this)
                message.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30F)
                message.setPadding(15, 15, 15, 15)
                message.text = this.resources.getString(R.string.all_done)
                message.setTextColor( ContextCompat.getColor(
                    applicationContext,
                    R.color.purple_500
                ))
                message.id = View.generateViewId()
                activityParentMimoBinding.parentLl.addView(message)
                lessonsViewModel.fetchAllCompletedLessons(db)
            }
        }
        var lastStringIndex = 0
        lesson.content.forEach { i ->
            val contentTv = TextView(this)
            contentTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)
            contentTv.setPadding(15, 15, 15, 15)
            when {
                startIndex > -1 && lastStringIndex == startIndex -> {
                    codingView.addView(userInputAnswer)
                    contentTv.text = i.text.substring(6)
                    correctAnswer = i.text.substring(0, 6)
                    contentTv.setTextColor(Color.parseColor(i.color))
                    codingView.addView(contentTv)
                }
                else -> {
                    contentTv.text = i.text
                    contentTv.setTextColor(Color.parseColor(i.color))
                    codingView.addView(contentTv)
                }
            }
            lastStringIndex = i.text.length
        }

    }

    private fun initializeViewModel() {
        val factory = getViewModelFactory()
        lessonsViewModel = ViewModelProvider(this, factory).get(LessonsViewModel::class.java)
        activityParentMimoBinding.lifecycleOwner = this
        lessonsViewModel.fetchLessons()
    }

    private fun getViewModelFactory(): ViewModelFactory {
        val remoteDataSource = RemoteDataSource()
        return ViewModelFactory(AppRepository(remoteDataSource.buildApi(AppApis::class.java)))
    }
}