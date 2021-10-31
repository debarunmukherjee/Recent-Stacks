package com.example.recentstacks.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recentstacks.models.Item
import com.example.recentstacks.models.Question
import com.example.recentstacks.repository.QuestionRepository
import com.example.recentstacks.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class QuestionViewModel(private val repository: QuestionRepository) : ViewModel() {

    private val chosenFilterMutable = MutableLiveData<String>()

    val chosenFilter: LiveData<String>
        get() = chosenFilterMutable

    private val mutableAllTags = MutableLiveData<List<String>>()

    val allTags: LiveData<List<String>>
        get() = mutableAllTags

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getQuestions()
            chosenFilterMutable.postValue("All")
        }
    }

    val questions: LiveData<Resource<Question>>
        get() = repository.questions

    fun updateChosenFilter(value: String) {
        chosenFilterMutable.postValue(value)
    }

    fun updateFilters() {
        val arrayOfTags = questions.value?.data?.items?.map { it.tags }
        val tagsList = arrayOfTags?.flatten()
        if (tagsList != null) {
            mutableAllTags.postValue(tagsList.distinct())
        }
    }

    fun getItemsListWithAds(itemList: List<Item>): List<Item> {
        val itemListWithAds = mutableListOf<Item>()
        for ((index, item) in itemList.withIndex()) {
            itemListWithAds.add(item)
            if (index != 0 && index % 4 == 0) {
                itemListWithAds.add(Item(item_type = 1))
            }
        }
        return itemListWithAds
    }
}