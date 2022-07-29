package com.example.socialx.api

import com.example.socialx.models.NewsResponseData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("top-headlines")
    fun getLatestHeadlines(@Query("country") country:String,@Query("apiKey") apiKey:String): Call<NewsResponseData?>
}