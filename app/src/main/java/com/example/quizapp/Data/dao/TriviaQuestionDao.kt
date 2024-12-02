package com.ornek.quizapp.Data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ornek.quizapp.Data.entities.CachedTriviaQuestion

@Dao
interface TriviaQuestionDao {
    @Insert
    suspend fun insertQuestions(questions: List<CachedTriviaQuestion>)

    @Query("SELECT * FROM trivia_questions")
    fun getAllCachedQuestions(): List<CachedTriviaQuestion>

    @Query("DELETE FROM trivia_questions")
    suspend fun clearCachedQuestions()
}