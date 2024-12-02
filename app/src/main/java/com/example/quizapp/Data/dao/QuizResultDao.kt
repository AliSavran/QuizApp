package com.ornek.quizapp.Data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ornek.quizapp.Data.entities.QuizResult
import kotlinx.coroutines.flow.Flow


@Dao
interface QuizResultDao {
    @Insert
    suspend fun insertQuizResult(quizResult: QuizResult)

    @Query("SELECT * FROM quiz_results ORDER BY timestamp DESC")
    fun getAllQuizResults(): Flow<List<QuizResult>>

    @Query("SELECT AVG(score * 1.0 / totalQuestions) FROM quiz_results")
    fun getAverageScore(): Flow<Double>
}
