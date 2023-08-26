package com.example.recipessearch.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//Объявляем базу данных с ее DAO(предоставляет методы для взаимодействия с базой данных) и сущьностью(таблица базы данных)
@Database(entities = [RecipeEntity::class], version = 1)
abstract class RecipesDatabase : RoomDatabase() {
    abstract fun recipesDao(): RecipesDao

    companion object {
        @Volatile
        private var INSTANCE: RecipesDatabase? = null

        //Получить экземпляр базы данных из другой области кода (например из главной активности)

        fun getDatabase(context: Context): RecipesDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecipesDatabase::class.java,
                    "movie_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

