package com.example.aiinterviewcoach.presentation.results

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aiinterviewcoach.domain.model.Answer

@Composable
fun ResultsScreen(
    role: String,
    totalScore: Int,
    answers: List<Answer>,
    onRetry: () -> Unit,
    onHome: () -> Unit
) {
    val scoreColor = when {
        totalScore >= 80 -> Color(0xFF4CAF50)
        totalScore >= 60 -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }

    val scoreEmoji = when {
        totalScore >= 80 -> "🏆"
        totalScore >= 60 -> "👍"
        else -> "💪"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(32.dp))

            // Score Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = scoreEmoji, fontSize = 64.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Interview Complete!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = role,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Score Circle
                Card(
                    modifier = Modifier.size(120.dp),
                    shape = RoundedCornerShape(60.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = scoreColor.copy(alpha = 0.1f)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$totalScore%",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = scoreColor
                            )
                            Text(
                                text = "Score",
                                fontSize = 12.sp,
                                color = scoreColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Detailed Feedback",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Answer Reviews
        itemsIndexed(answers) { index, answer ->
            AnswerReviewCard(
                index = index + 1,
                answer = answer
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Button(
                onClick = onRetry,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Try Again 🔄", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Back to Home 🏠", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun AnswerReviewCard(index: Int, answer: Answer) {
    val scoreColor = when {
        answer.score >= 80 -> Color(0xFF4CAF50)
        answer.score >= 60 -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Q$index",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${answer.score}%",
                    color = scoreColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = answer.questionText,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your answer:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = answer.userAnswer.ifEmpty { "No answer provided" },
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = scoreColor.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "🤖 AI Feedback",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = scoreColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = answer.aiFeedback,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}