package com.ornek.quizapp.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ornek.quizapp.R
import com.ornek.quizapp.ViewModel.QuizViewModel

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val viewModel: QuizViewModel by viewModels()


        val scoreTextView = findViewById<TextView>(R.id.scoreTextView)
        val score = intent.getIntExtra("SCORE", 0)
        scoreTextView.text = "Skorunuz: $score / 10"


        viewModel.saveQuizResult(score)


        val restartButton = findViewById<Button>(R.id.restartButton)
        restartButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}