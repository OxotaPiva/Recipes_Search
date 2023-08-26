package com.example.recipessearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipessearch.adapters.RecipesAdapter
import com.example.recipessearch.data.api.ApiService
import com.example.recipessearch.data.api.RecipeHit
import com.example.recipessearch.data.api.RecipeSearchResponse
import com.example.recipessearch.data.db.RecipesDao
import com.example.recipessearch.data.db.RecipesDatabase
import com.example.recipessearch.databinding.ActivityMainBinding
import com.example.skytracker.data.api.Instance
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var binding: ActivityMainBinding
    }
    private lateinit var recipesDao: RecipesDao
    private lateinit var recipesAdapter: RecipesAdapter
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val service = Instance.api
        getSavedListRecipes(service)
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                GlobalScope.launch {
                    runOnUiThread {
                        fetchRecipes(service.getRecipesByQuery(query))
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                    getSavedListRecipes(service)
                }
                return true
            }

        })

    }

    private fun fetchRecipes(call: Call<RecipeSearchResponse>) {
        call.enqueue(object : Callback<RecipeSearchResponse> {
            override fun onResponse(call: Call<RecipeSearchResponse>, response: Response<RecipeSearchResponse>) {
                if (response.isSuccessful) {
                    val response = response.body()
                    val hits = response?.hits
                    recipesAdapter = hits?.let { RecipesAdapter(it, this@MainActivity) }!!
                    binding.recipeAdapter.adapter = recipesAdapter
                    binding.recipeAdapter.layoutManager = LinearLayoutManager(this@MainActivity)
                } else {
                    Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RecipeSearchResponse>, t: Throwable) {
                Toast.makeText(applicationContext, t.toString(), Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getSavedListRecipes(service: ApiService) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                recipesDao = RecipesDatabase
                    .getDatabase(this@MainActivity)
                    .recipesDao()
                val recipesDB = recipesDao.getAllRecipes()
                val recipesList = emptyList<RecipeHit>()
                val recipes = recipesList.toMutableList()
                recipesDB.forEach { recipe ->
                    val recipeCall = service.getRecipeByUri(recipe.uri)
                    recipeCall.enqueue(object : Callback<RecipeSearchResponse> {
                        override fun onResponse(call: Call<RecipeSearchResponse>, response: Response<RecipeSearchResponse>) {
                            if (response.isSuccessful) {
                                val response = response.body()
                                val hits = response?.hits
                                hits?.get(0)?.let { recipes.add(it) }
                            } else {
                                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<RecipeSearchResponse>, t: Throwable) {
                            Toast.makeText(applicationContext, t.toString(), Toast.LENGTH_LONG).show()
                        }
                    })
                }
                runOnUiThread {
                    recipesAdapter =
                        RecipesAdapter(recipes, this@MainActivity)
                    binding.recipeAdapter.adapter = recipesAdapter
                    binding.recipeAdapter.layoutManager =
                        LinearLayoutManager(this@MainActivity)
                }
            }

        }

    }
}