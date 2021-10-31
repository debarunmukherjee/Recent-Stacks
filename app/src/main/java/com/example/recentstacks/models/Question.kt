package com.example.recentstacks.models

data class Question(
    val has_more: Boolean = false,
    val is_fetched_offline:Boolean = false,
    val items: List<Item> = listOf(),
    val quota_max: Int = 0,
    val quota_remaining: Int = 0
)