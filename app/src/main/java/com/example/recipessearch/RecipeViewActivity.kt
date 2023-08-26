package com.example.recipessearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.recipessearch.data.api.Recipe
import com.example.recipessearch.data.db.RecipeEntity
import com.example.recipessearch.data.db.RecipesDao
import com.example.recipessearch.data.db.RecipesDatabase
import com.example.recipessearch.databinding.ActivityRecipeViewBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
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

        binding.addToFollowedButton.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    recipesDao = RecipesDatabase
                        .getDatabase(this@RecipeViewActivity)
                        .recipesDao()
                    if (recipeDetails != null) {
                        recipesDao.insertRecipe(RecipeEntity(uri = recipeDetails.uri))
                    }
                }
            }
            Toast.makeText(this@RecipeViewActivity
                , "Рецепт сохронен и добавлен на главный экран", Toast.LENGTH_SHORT).show()
        }
    }
}