package com.example.recentstacks.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class Item(
    @PrimaryKey
    val question_id: Int = 0,
    val item_type: Int = 0,
    val accepted_answer_id: Int = 0,
    val answer_count: Int = 0,
    val bounty_amount: Int = 0,
    val bounty_closes_date: Int = 0,
    val closed_date: Int = 0,
    val closed_reason: String = "",
    val content_license: String = "",
    val creation_date: Int = 0,
    val is_answered: Boolean = false,
    val last_activity_date: Int = 0,
    val last_edit_date: Int = 0,
    val link: String = "",
    val owner: Owner = Owner(),
    val score: Int = 0,
    val tags: List<String> = listOf(),
    val title: String = "",
    val view_count: Int = 0
)