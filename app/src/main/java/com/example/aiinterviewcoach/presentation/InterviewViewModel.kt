package com.example.aiinterviewcoach.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiinterviewcoach.data.local.GeminiService
import com.example.aiinterviewcoach.domain.model.Answer
import com.example.aiinterviewcoach.domain.model.Question
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class InterviewState(
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val userAnswer: String = "",
    val answers: List<Answer> = emptyList(),
    val isLoading: Boolean = false,
    val isGeneratingQuestions: Boolean = false,
    val isFinished: Boolean = false,
    val totalScore: Int = 0,
    val selectedRole: String = "",
    val selectedDifficulty: String = ""
)

class InterviewViewModel : ViewModel() {

    private val geminiService = GeminiService()

    private val _state = MutableStateFlow(InterviewState())
    val state: StateFlow<InterviewState> = _state

    fun startInterview(role: String, difficulty: String) {
        _state.value = InterviewState(
            isGeneratingQuestions = true,
            selectedRole = role,
            selectedDifficulty = difficulty
        )

        viewModelScope.launch {
            try {
                val questionTexts = geminiService.generateQuestions(
                    role = role,
                    difficulty = difficulty,
                    count = 5
                )

                val questions = questionTexts.mapIndexed { index, text ->
                    Question(
                        id = index,
                        text = text,
                        category = role,
                        difficulty = difficulty
                    )
                }

                _state.value = _state.value.copy(
                    questions = questions,
                    isGeneratingQuestions = false,
                    currentIndex = 0
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isGeneratingQuestions = false
                )
            }
        }
    }

    fun updateAnswer(answer: String) {
        _state.value = _state.value.copy(userAnswer = answer)
    }

    fun submitAnswerAndNext() {
        val currentState = _state.value
        if (currentState.userAnswer.isBlank()) return

        _state.value = currentState.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val currentQuestion = currentState.questions[currentState.currentIndex]

                val (score, feedback) = geminiService.evaluateAnswer(
                    question = currentQuestion.text,
                    answer = currentState.userAnswer,
                    role = currentState.selectedRole
                )

                val newAnswer = Answer(
                    questionId = currentQuestion.id,
                    questionText = currentQuestion.text,
                    userAnswer = currentState.userAnswer,
                    aiFeedback = feedback,
                    score = score
                )

                val updatedAnswers = currentState.answers + newAnswer
                val isLast = currentState.currentIndex == currentState.questions.size - 1

                if (isLast) {
                    val avgScore = updatedAnswers.map { it.score }.average().toInt()
                    _state.value = currentState.copy(
                        answers = updatedAnswers,
                        isLoading = false,
                        isFinished = true,
                        totalScore = avgScore
                    )
                } else {
                    _state.value = currentState.copy(
                        answers = updatedAnswers,
                        currentIndex = currentState.currentIndex + 1,
                        userAnswer = "",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.value = currentState.copy(isLoading = false)
            }
        }
    }

    fun resetInterview() {
        _state.value = InterviewState()
    }
}