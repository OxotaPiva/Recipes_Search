package com.example.recipessearch

import android.app.Activity
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipessearch.data.api.Recipe
import com.example.recipessearch.data.db.RecipeEntity
import com.example.recipessearch.data.db.RecipesDao
import com.example.recipessearch.data.db.RecipesDatabase
import com.example.recipessearch.databinding.ActivityRecipeViewBinding
import com.example.skytracker.data.api.Instance
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeViewBinding
    private lateinit var recipesDao: RecipesDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recipeDetails = intent.getSerializableExtra("recipeDetails") as? Recipe



        binding.recipeTitle.text = recipeDetails?.label
        Picasso.get()
            .load(recipeDetails?.image)
            .resize(300, 300)
            .into(binding.recipeImage)
        var stringOfIngredients = ""
        recipeDetails?.ingredientLines?.forEach {
            stringOfIngredients+= "$it\n"
        }
        binding.recipeIngredients.text = stringOfIngredients
        recipesDao = RecipesDatabase
            .getDatabase(this@RecipeViewActivity)
            .recipesDao()
        GlobalScope.launch {
            if (recipeDetails?.uri?.let { recipesDao.checkRecipeExists(it) }!! > 0) {
                runOnUiThread {
                    binding.addToFollowedButton.visibility = View.GONE
                    binding.deleteFromFollowedButton.visibility = View.VISIBLE
                }
            }
        }
        binding.addToFollowedButton.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    if (recipeDetails != null) {
                        recipesDao.insertRecipe(RecipeEntity(uri = recipeDetails.uri))
                    }
                }
            }
            binding.addToFollowedButton.visibility = View.GONE
            binding.deleteFromFollowedButton.visibility = View.VISIBLE
            Toast.makeText(this@RecipeViewActivity
                , "Рецепт сохронен и добавлен на главный экран", Toast.LENGTH_SHORT).show()
        }

        binding.deleteFromFollowedButton.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    if (recipeDetails != null) {
                        recipesDao.deleteRecipeByUri(recipeDetails.uri)
                    }
                    withContext(Dispatchers.Main) {
                        (parent as? MainActivity)?.getSavedListRecipes(Instance.api)
                        binding.addToFollowedButton.visibility = View.VISIBLE
                        binding.deleteFromFollowedButton.visibility = View.GONE

                        Toast.makeText(this@RecipeViewActivity,
                            "Рецепт удален из сохраненных", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            setResult(Activity.RESULT_OK, null)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK, null)
        finish()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }
}