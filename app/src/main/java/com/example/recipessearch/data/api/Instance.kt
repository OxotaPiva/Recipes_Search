package com.example.skytracker.data.api

import com.example.recipessearch.data.api.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Instance {
    private val retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl("https://api.edamam.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}