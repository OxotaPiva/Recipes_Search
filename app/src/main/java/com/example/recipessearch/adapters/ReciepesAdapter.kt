package com.example.recipessearch.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.*
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.recipessearch.RecipeViewActivity
import com.example.recipessearch.data.api.Recipe
import com.example.recipessearch.data.api.RecipeHit
import com.example.recipessearch.databinding.RecipeItemBinding
import com.squareup.picasso.Picasso
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

class RecipesAdapter(
    private val recipeData: List<RecipeHit>,
    private val context: Context
) :
    RecyclerView.Adapter<RecipesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hits = recipeData[position]
        holder.bind(hits.recipe)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, RecipeViewActivity::class.java)
            val recipeDetails = hits.recipe
            intent.putExtra("recipeDetails", recipeDetails)
            context.startActivity(intent)
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

            Picasso.get()
                .load(recipeData.image)
                .into(binding.recipeImage)

        }
    }
}