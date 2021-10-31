package com.example.recentstacks.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recentstacks.models.Item

@Dao
interface QuestionsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<Item>)

    @Query("DELETE FROM questions")
    suspend fun deleteAllQuestions()

    @Query("SELECT * FROM questions")
    suspend fun getQuotes(): List<Item>
}