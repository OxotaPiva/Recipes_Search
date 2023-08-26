package com.example.recipessearch.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipessearch.data.api.Recipe

@Dao
interface RecipesDao {
    // Получить все фильмы из базы данных
    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): List<RecipeEntity>

    // Вставить фильм в базу данных
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecipe(recipe: RecipeEntity)

    // Удалить все фильмы из базы данных
    @Query("DELETE FROM recipes WHERE uri = :recipeUri")
    fun deleteRecipeByUri(recipeUri: String)
}

