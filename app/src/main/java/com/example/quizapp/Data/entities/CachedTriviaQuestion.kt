package com.ornek.quizapp.Data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trivia_questions")
data class CachedTriviaQuestion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val category: String,
    val type: String,
    val difficulty: String,
    val question: String,
    val correct_answer: String,
    val incorrect_answers: String
)