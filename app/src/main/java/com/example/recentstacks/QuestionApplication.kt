package com.example.recentstacks

import android.app.Application
import com.example.recentstacks.api.QuestionService
import com.example.recentstacks.api.RetrofitHelper
import com.example.recentstacks.db.QuestionDatabase
import com.example.recentstacks.repository.QuestionRepository

class QuestionApplication : Application() {

    lateinit var questionRepository: QuestionRepository

    override fun onCreate() {
        super.onCreate()
        initialise()
    }

    private fun initialise() {
        val questionService = RetrofitHelper.getInstance().create(QuestionService::class.java)
        val database = QuestionDatabase.getDatabase(applicationContext)
        questionRepository = QuestionRepository(questionService, database, applicationContext)
    }
}