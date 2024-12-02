package com.ornek.quizapp.Data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ornek.quizapp.Data.dao.QuizResultDao
import com.ornek.quizapp.Data.dao.TriviaQuestionDao
import com.ornek.quizapp.Data.entities.CachedTriviaQuestion
import com.ornek.quizapp.Data.entities.QuizResult


@Database(
    entities = [QuizResult::class, CachedTriviaQuestion::class],
    version = 1,
    exportSchema = false
)
abstract class QuizDatabase : RoomDatabase() {
    abstract fun quizResultDao(): QuizResultDao
    abstract fun triviaQuestionDao(): TriviaQuestionDao

    companion object {
        @Volatile
        private var INSTANCE: QuizDatabase? = null

        fun getDatabase(context: Context): QuizDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuizDatabase::class.java,
                    "quiz_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}