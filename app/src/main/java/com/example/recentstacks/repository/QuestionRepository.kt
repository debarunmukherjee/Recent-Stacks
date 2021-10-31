package com.example.recentstacks.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.recentstacks.api.QuestionService
import com.example.recentstacks.db.QuestionDatabase
import com.example.recentstacks.models.Question
import com.example.recentstacks.utils.NetworkUtils
import com.example.recentstacks.utils.Resource
import retrofit2.Response

class QuestionRepository(
    private val questionService: QuestionService,
    private val questionDatabase: QuestionDatabase,
    private val applicationContext: Context
) {

    private val questionMutableLiveData = MutableLiveData<Resource<Question>>()

    val questions: LiveData<Resource<Question>>
        get() = questionMutableLiveData

    suspend fun getQuestions() {
        if (NetworkUtils.isInternetAvailable(applicationContext)) {
            questionMutableLiveData.postValue(Resource.Loading())
            val result = questionService.getQuestions()
            questionMutableLiveData.postValue(handleQuestionsResponse(result))
        } else {
            val items = questionDatabase.QuestionsDao().getQuotes()
            val offlineResource = Resource.Success(Question(items = items, is_fetched_offline = true))
            questionMutableLiveData.postValue(offlineResource)
        }
    }

    private suspend fun handleQuestionsResponse(response: Response<Question>) : Resource<Question> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                questionDatabase.QuestionsDao().deleteAllQuestions()
                questionDatabase.QuestionsDao().insertQuestions(response.body()!!.items)
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}