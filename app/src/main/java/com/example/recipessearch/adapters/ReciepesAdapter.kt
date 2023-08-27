package com.example.recipessearch.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipessearch.MainActivity
import com.example.recipessearch.RecipeViewActivity
import com.example.recipessearch.data.api.ApiService
import com.example.recipessearch.data.api.Recipe
import com.example.recipessearch.data.api.RecipeHit
import com.example.recipessearch.data.api.RecipeSearchResponse
import com.example.recipessearch.data.db.RecipesDao
import com.example.recipessearch.data.db.RecipesDatabase
import com.example.recipessearch.databinding.RecipeItemBinding
import com.example.skytracker.data.api.Instance
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

class RecipesAdapter(
    private val recipeData: List<RecipeHit>,
    private val context: Context,
    private val isSavedRecipes: Boolean = false
) :
    RecyclerView.Adapter<RecipesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    private lateinit var recipesDao: RecipesDao
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hits = recipeData[position]
        holder.bind(hits.recipe)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, RecipeViewActivity::class.java)
            val recipeDetails = hits.recipe
            intent.putExtra("recipeDetails", recipeDetails)
            if(context is MainActivity) {
                context.startChildActivity.launch(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return recipeData.size
    }

    inner class ViewHolder(private val binding: RecipeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(recipeData: Recipe) {
            binding.recipeTitle.text = recipeData.label
            val kcal = recipeData.totalNutrients["ENERC_KCAL"]
            binding.recipeKcal.text = kcal?.quantity?.toInt().toString() + " " + kcal?.unit
            if (isSavedRecipes) {
                binding.deleteFromFollowedButton.visibility = View.VISIBLE
            }
            binding.deleteFromFollowedButton.setOnClickListener {
                if (context is MainActivity) {
                    context.lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            recipesDao = RecipesDatabase
                                .getDatabase(context)
                                .recipesDao()
                            recipesDao.deleteRecipeByUri(recipeData.uri)

                            withContext(Dispatchers.Main) {
                                context.getSavedListRecipes(Instance.api)
                            }
                        }
                    }

                    Toast.makeText(context, "Рецепт удален из сохраненных", Toast.LENGTH_LONG).show()
                }
                Picasso.get()
                    .load(recipeData.image)
                    .into(binding.recipeImage)
                }


        }
    }
}