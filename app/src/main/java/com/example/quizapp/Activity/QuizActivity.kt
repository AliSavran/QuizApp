package com.ornek.quizapp.Activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import android.text.Html
import android.view.View
import android.widget.Toast
import com.ornek.quizapp.R
import com.ornek.quizapp.TriviaApiService.TriviaQuestion
import com.ornek.quizapp.ViewModel.QuizViewModel

class QuizActivity : AppCompatActivity() {
    private val viewModel: QuizViewModel by viewModels()
    private lateinit var questionTextView: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var nextButton: Button
    private lateinit var timerTextView: TextView
    private lateinit var countDownTimer: CountDownTimer


    private var correctAnswer: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)


        questionTextView = findViewById(R.id.questionTextView)
        radioGroup = findViewById(R.id.answersRadioGroup)
        nextButton = findViewById(R.id.nextButton)
        timerTextView = findViewById(R.id.timerTextView)


        startCountdownTimer()


        viewModel.questions.observe(this) { questions ->
            if (questions.isNotEmpty()) {
                loadQuestion(questions[viewModel.currentQuestionIndex.value ?: 0])
            }
        }

        nextButton.setOnClickListener {
            checkAnswer()
        }


        radioGroup.setOnCheckedChangeListener { _, _ ->
            nextButton.isEnabled = true
        }
    }

    private fun loadQuestion(question: TriviaQuestion) {

        correctAnswer = question.correct_answer


        questionTextView.text = Html.fromHtml(question.question, Html.FROM_HTML_MODE_LEGACY)


        val allAnswers = question.incorrect_answers.toMutableList()
        allAnswers.add(question.correct_answer)
        allAnswers.shuffle()


        radioGroup.removeAllViews()
        radioGroup.clearCheck()
        nextButton.isEnabled = false

        allAnswers.forEachIndexed { index, answer ->
            val radioButton = RadioButton(this)
            radioButton.text = Html.fromHtml(answer, Html.FROM_HTML_MODE_LEGACY)
            radioButton.id = View.generateViewId()
            radioGroup.addView(radioButton)
        }
    }

    private fun checkAnswer() {
        val selectedRadioButtonId = radioGroup.checkedRadioButtonId
        if (selectedRadioButtonId == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedAnswer = findViewById<RadioButton>(selectedRadioButtonId).text.toString()

        val isCorrect = selectedAnswer == correctAnswer


        if (isCorrect) {
            Toast.makeText(this, "Correct Answer!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Wrong Answer", Toast.LENGTH_SHORT).show()
        }


        viewModel.updateScore(isCorrect)


        if (viewModel.isQuizFinished()) {
            finishQuiz()
        } else {

            viewModel.nextQuestion()
            loadQuestion(viewModel.questions.value!![viewModel.currentQuestionIndex.value!!])
        }
    }

    private fun startCountdownTimer() {
        countDownTimer = object : CountDownTimer(300000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutesRemaining = millisUntilFinished / 60000
                val secondsRemaining = (millisUntilFinished % 60000) / 1000
                timerTextView.text = String.format("Remaining Time: %02d:%02d", minutesRemaining, secondsRemaining)
            }

            override fun onFinish() {
                Toast.makeText(this@QuizActivity, "Time is Up!", Toast.LENGTH_SHORT).show()
                finishQuiz()
            }
        }.start()
    }

    private fun finishQuiz() {
        countDownTimer.cancel()
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("SCORE", viewModel.score.value ?: 0)
        }
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }
}