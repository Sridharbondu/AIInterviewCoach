package com.example.aiinterviewcoach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aiinterviewcoach.ui.theme.AIInterviewCoachTheme
import com.example.aiinterviewcoach.presentation.InterviewViewModel
import com.example.aiinterviewcoach.presentation.home.HomeScreen
import com.example.aiinterviewcoach.presentation.interview.InterviewScreen
import com.example.aiinterviewcoach.presentation.results.ResultsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AIInterviewCoachTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AIInterviewCoachApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AIInterviewCoachApp(modifier: Modifier = Modifier) {
    val viewModel: InterviewViewModel = viewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        when {
            // Show loading while generating questions
            state.isGeneratingQuestions -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // Show results when finished
            state.isFinished -> {
                ResultsScreen(
                    role = state.selectedRole,
                    totalScore = state.totalScore,
                    answers = state.answers,
                    onRetry = {
                        viewModel.startInterview(
                            state.selectedRole,
                            state.selectedDifficulty
                        )
                    },
                    onHome = { viewModel.resetInterview() }
                )
            }

            // Show interview when questions loaded
            state.questions.isNotEmpty() -> {
                InterviewScreen(
                    questions = state.questions,
                    currentIndex = state.currentIndex,
                    isLoading = state.isLoading,
                    userAnswer = state.userAnswer,
                    onAnswerChanged = { viewModel.updateAnswer(it) },
                    onNextQuestion = { viewModel.submitAnswerAndNext() },
                    onFinishInterview = { viewModel.submitAnswerAndNext() },
                    onSpeechResult = { viewModel.updateAnswer(it) }
                )
            }

            // Show home screen by default
            else -> {
                HomeScreen(
                    onStartInterview = { role, difficulty ->
                        viewModel.startInterview(role, difficulty)
                    }
                )
            }
        }
    }
}