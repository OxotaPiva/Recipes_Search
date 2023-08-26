package com.example.recipessearch.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipessearch.data.api.Recipe

@Dao
interface RecipesDao {

    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): List<RecipeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecipe(recipe: RecipeEntity)

    @Query("DELETE FROM recipes WHERE uri = :recipeUri")
    fun deleteRecipeByUri(recipeUri: String)
}

