package com.ornek.quizapp.TriviaApiService

data class TriviaResponse(

    val response_code : Int,
    val results : List<TriviaQuestion>
)