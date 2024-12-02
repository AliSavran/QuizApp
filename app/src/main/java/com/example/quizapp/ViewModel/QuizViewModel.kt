package com.ornek.quizapp.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ornek.quizapp.Data.database.QuizDatabase
import com.ornek.quizapp.Data.entities.CachedTriviaQuestion
import com.ornek.quizapp.Data.entities.QuizResult
import com.ornek.quizapp.TriviaApiService.TriviaApiService
import com.ornek.quizapp.TriviaApiService.TriviaQuestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private val database = QuizDatabase.getDatabase(application)
    private val quizResultDao = database.quizResultDao()
    private val triviaQuestionDao = database.triviaQuestionDao()

    private val _questions = MutableLiveData<List<TriviaQuestion>>()
    val questions: LiveData<List<TriviaQuestion>> = _questions

    private val _currentQuestionIndex = MutableLiveData(0)
    val currentQuestionIndex: LiveData<Int> = _currentQuestionIndex

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score

    private val triviaApiService: TriviaApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://opentdb.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        triviaApiService = retrofit.create(TriviaApiService::class.java)
        fetchQuestions()
    }

    private fun fetchQuestions() {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val cachedQuestions = triviaQuestionDao.getAllCachedQuestions()

                if (cachedQuestions.isNotEmpty()) {

                    val triviaQuestions = cachedQuestions.map { cachedQuestion ->
                        TriviaQuestion(
                            category = cachedQuestion.category,
                            type = cachedQuestion.type,
                            difficulty = cachedQuestion.difficulty,
                            question = cachedQuestion.question,
                            correct_answer = cachedQuestion.correct_answer,
                            incorrect_answers = cachedQuestion.incorrect_answers.split(",")
                        )
                    }
                    _questions.postValue(triviaQuestions)
                } else {

                    val response = triviaApiService.getQuestions()
                    _questions.postValue(response.results)


                    val cachedQuestions = response.results.map { question ->
                        CachedTriviaQuestion(
                            category = question.category,
                            type = question.type,
                            difficulty = question.difficulty,
                            question = question.question,
                            correct_answer = question.correct_answer,
                            incorrect_answers = question.incorrect_answers.joinToString(",")
                        )
                    }
                    triviaQuestionDao.clearCachedQuestions() // Clear previous cache
                    triviaQuestionDao.insertQuestions(cachedQuestions)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveQuizResult(score: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val quizResult = QuizResult(
                score = score,
                totalQuestions = 10
            )
            quizResultDao.insertQuizResult(quizResult)
        }
    }


    fun nextQuestion() {
        _currentQuestionIndex.value = _currentQuestionIndex.value?.plus(1)
    }

    fun updateScore(isCorrect: Boolean) {
        val currentScore = _score.value ?: 0
        _score.value = if (isCorrect) currentScore + 1 else maxOf(0, currentScore - 1)
    }

    fun isQuizFinished(): Boolean {
        return (currentQuestionIndex.value ?: 0) >= 9
    }
}