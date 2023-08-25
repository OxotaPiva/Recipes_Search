package com.example.recipessearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.recipessearch.data.api.Recipe
import com.example.recipessearch.databinding.ActivityMainBinding
import com.example.recipessearch.databinding.ActivityRecipeViewBinding
import com.squareup.picasso.Picasso

class RecipeViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeViewBinding

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
    }
}