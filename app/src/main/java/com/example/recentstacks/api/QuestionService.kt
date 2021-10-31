package com.example.recentstacks.api

import com.example.recentstacks.models.Question
import retrofit2.Response
import retrofit2.http.GET

interface QuestionService {

    @GET("/2.2/questions?key=ZiXCZbWaOwnDgpVT9Hx8IA((&order=desc&sort=activity&site=stackoverflow")
    suspend fun getQuestions() : Response<Question>
}