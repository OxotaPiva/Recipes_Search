package com.example.recipessearch.data.api

data class RecipeSearchResponse(
    val count: Int,
    val from: Int,
    val to: Int,
    val hits: List<RecipeHit>
)

data class RecipeHit(
    val recipe: Recipe
)

data class Recipe(
    val uri: String,
    val label: String,
    val image: String,
    val source: String,
    val url: String,
    val yield: Double,
    val dietLabels: List<String>,
    val healthLabels: List<String>,
    val ingredientLines: List<String>,
    val ingredients: List<Ingredient>,
    val totalTime: Int,
    val totalNutrients: Map<String, NutrientInfo>
)

data class Ingredient(
    val text: String,
    val weight: Double
)

data class NutrientInfo(
    val label: String,
    val quantity: Double,
    val unit: String
)

