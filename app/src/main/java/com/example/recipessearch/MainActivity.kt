package com.example.recipessearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipessearch.adapters.RecipesAdapter
import com.example.recipessearch.data.api.RecipeSearchResponse
import com.example.recipessearch.databinding.ActivityMainBinding
import com.example.skytracker.data.api.Instance
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var binding: ActivityMainBinding
    }

    private lateinit var recipesAdapter: RecipesAdapter
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val service = Instance.api
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
//                if (newText.isEmpty()) {
//                    val call = service.getVacancies()
//                    fetchVacancies(call)
//                }
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
}