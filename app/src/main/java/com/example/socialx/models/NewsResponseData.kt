package com.example.socialx.models

data class NewsResponseData(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)