package com.example.recipessearch.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

//сущность базы данных с ее моделью данных
@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val uri: String
)
