package com.example.recentstacks

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recentstacks.models.Item
import com.example.recentstacks.utils.NetworkUtils
import com.example.recentstacks.utils.Resource
import com.example.recentstacks.viewmodels.QuestionViewModel
import com.example.recentstacks.viewmodels.QuestionViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), QuestionListAdapter.OnItemClickListener {
    lateinit var questionViewModel: QuestionViewModel
    private lateinit var questionAdapter: QuestionListAdapter
    lateinit var filterBottomSheet: FilterBottomSheet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val questionList = mutableListOf<Item>()
        val repository = (application as QuestionApplication).questionRepository
        questionViewModel = ViewModelProvider(
            this,
            QuestionViewModelFactory(repository)
        ).get(QuestionViewModel::class.java)
        questionViewModel.questions.observe(this, {
            when (it) {
                is Resource.Loading -> {
                    showProgressBar()
                }
                is Resource.Success -> {
                    hideProgressBar()
                    questionViewModel.updateFilters()
                    it.data?.let { response -> questionList.addAll(response.items) }
                    if (it.data?.is_fetched_offline == true) {
                        Toast.makeText(this@MainActivity, "You are offline.", Toast.LENGTH_LONG)
                            .show()
                    }
                    setupRecyclerView(questionList)
                    setUpFilter(questionViewModel)
                }
                is Resource.Error -> {
                    hideProgressBar()
                    it.message?.let { message ->
                        Log.e("RECENT_STACKS_ERR", "An error occurred: $message")
                    }
                    Toast.makeText(this@MainActivity, "Some error occurred", Toast.LENGTH_LONG)
                        .show()
                }
            }
        })
    }

    private fun hideProgressBar() {
        findViewById<ProgressBar>(R.id.pbLoadingBar).visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        findViewById<ProgressBar>(R.id.pbLoadingBar).visibility = View.VISIBLE
    }

    private fun setupRecyclerView(questionList: List<Item>) {
        val questionContainer = findViewById<RecyclerView>(R.id.rvQuestionContainer)
        questionAdapter = QuestionListAdapter(this@MainActivity, this)
        questionContainer.adapter = questionAdapter
        questionAdapter.differ.submitList(questionViewModel.getItemsListWithAds(questionList))
        if (questionList.isEmpty()) {
            findViewById<TextView>(R.id.tvNoResults).visibility = View.VISIBLE
        } else {
            findViewById<TextView>(R.id.tvNoResults).visibility = View.INVISIBLE
        }
        questionContainer.layoutManager = LinearLayoutManager(this)
    }

    override fun onItemClick(position: Int) {
        val clickedItem = questionAdapter.differ.currentList[position]
        if (NetworkUtils.isInternetAvailable(this)) {
            clickedItem.link.asUri().openInBrowser(this)
        } else {
            Toast.makeText(this@MainActivity, "You are offline", Toast.LENGTH_SHORT).show()
        }
    }

    private fun String?.asUri(): Uri? {
        try {
            return Uri.parse(this)
        } catch (e: Exception) {}
        return null
    }

    private fun Uri?.openInBrowser(context: Context) {
        this ?: return // Do nothing if uri is null

        val browserIntent = Intent(Intent.ACTION_VIEW, this)
        ContextCompat.startActivity(context, browserIntent, null)
    }

    private fun showAverageStatsView(averageStatsContainer: ConstraintLayout, items: List<Item>) {
        lifecycleScope.launch(Dispatchers.Main) {
            val averageViewCount = async { questionViewModel.getAverageViewCount(items) }
            val averageAnswerCount = async { questionViewModel.getAverageAnswerCount(items) }
            findViewById<TextView>(R.id.tvAverageViewCount).text = "Average View Count: ${averageViewCount.await()}"
            findViewById<TextView>(R.id.tvAverageAnswerCount).text = "Average Answer Count: ${averageAnswerCount.await()}"
            averageStatsContainer.visibility = View.VISIBLE
        }
    }

    private fun getAllItemsForCurrentFilter(questionViewModel: QuestionViewModel): List<Item>? {
        val allItems = questionViewModel.questions.value?.data?.items
        val filter = questionViewModel.chosenFilter.value
        return if (filter == "All") {
            allItems
        } else {
            allItems?.filter { item -> item.tags.contains(filter) }
        }
    }

    private fun setUpFilter(questionViewModel: QuestionViewModel) {
        filterBottomSheet = FilterBottomSheet(this, questionViewModel)
        findViewById<ImageButton>(R.id.ibFilterButton).setOnClickListener {
            filterBottomSheet.apply {
                show(supportFragmentManager, FilterBottomSheet.TAG)
            }
        }
        questionViewModel.chosenFilter.observe(this, {
            val searchBar = findViewById<SearchView>(R.id.svQuestionSearch)
            searchBar.setQuery("", false)
            val items = getAllItemsForCurrentFilter(questionViewModel)
            val averageStatsContainer = findViewById<ConstraintLayout>(R.id.clAverageStatsContainer)
            if (it != "All" && items != null) {
                showAverageStatsView(averageStatsContainer, items)
            } else {
                averageStatsContainer.visibility = View.GONE
            }
            questionAdapter.differ.submitList(items?.let { it1 ->
                questionViewModel.getItemsListWithAds(
                    it1
                )
            })
        })
    }
}