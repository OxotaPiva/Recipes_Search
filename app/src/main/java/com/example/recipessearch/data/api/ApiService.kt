package com.example.recipessearch.data.api

import com.example.recipessearch.data.api.RecipeSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("recipes/v2?type=public&app_id=c67f7e78&app_key=a26ffe2df29a567871fd37581804f86a")
    fun getRecipesByQuery(@Query("q") query: String): Call<RecipeSearchResponse>
}