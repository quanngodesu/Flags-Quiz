package com.example.helloworld

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import android.media.MediaPlayer
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.tooling.preview.Preview


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen = remember { mutableStateOf("menu") }

            when (currentScreen.value) {
                "menu" -> MenuScreen { currentScreen.value = "quiz" }
                "quiz" -> FlagQuizApp { currentScreen.value = "menu" }
            }
        }
    }
}
@Composable
fun MenuScreen(onStartQuiz: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Welcome to Flag Quiz!",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 28.dp)
            )
            Button(
                onClick = onStartQuiz,
                modifier = Modifier.padding(20.dp)
            ) {
                Text("Start Quiz")
            }
        }
    }
}

@Preview
@Composable
fun MenuScreenPreview() {
    MenuScreen {}
}

@Composable
fun FlagQuizApp(onBackToMenu: () -> Unit) {
    val flags = listOf(
        R.drawable.uk to "UK",
        R.drawable.usa to "USA",
        R.drawable.southkorea to "South Korea",
        R.drawable.italy to "Italy",
        R.drawable.india to "India",
        R.drawable.australia to "Australia",
        R.drawable.brazil to "Brazil",
        R.drawable.canada to "Canada",
        R.drawable.france to "France",
        R.drawable.germany to "Germany",
        R.drawable.vietnam to "Vietnam",
        R.drawable.japan to "Japan"
    )

    val currentIndex = remember { mutableStateOf((0 until flags.size).random()) }
    val context = LocalContext.current
    val correctAnswer = flags[currentIndex.value].second
    val userScore = remember { mutableStateOf(0) }
    val selectedAnswer = remember { mutableStateOf<String?>(null) }
    val correctSound = MediaPlayer.create(context, R.raw.correct_answer)
    val wrongSound = MediaPlayer.create(context, R.raw.wrong_answer)

    val totalQuestions = flags.size
    val answeredQuestions = remember { mutableStateOf(0) }
    val gameOver = remember { mutableStateOf(false) }
    val feedbackMessage = remember { mutableStateOf<String?>(null) }
    LaunchedEffect(feedbackMessage.value) {
        feedbackMessage.value?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            feedbackMessage.value = null
        }
    }


    val gradient = Brush.linearGradient(colors = listOf(Color(0xFF00BCD4), Color(0xFF4CAF50)))

    if (gameOver.value) {
        // Game Over Screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Game Over!",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 28.dp)
                )
                Text(
                    text = "Your Score: ${userScore.value}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 28.dp)
                )
                Button(
                    onClick = {
                        // Restart the game
                        userScore.value = 0
                        answeredQuestions.value = 0
                        gameOver.value = false
                        currentIndex.value = (0 until flags.size).random()
                        selectedAnswer.value = null
                    },
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text("Restart Quiz")
                }
                Button(
                    onClick = onBackToMenu,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text("Back to Menu")
                }
            }
        }
    } else {
        // Quiz Screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.align(Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = flags[currentIndex.value].first),
                    contentDescription = "Flag of ${flags[currentIndex.value].second}",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(16.dp)
                )

                Text("Which country's flag is this?", Modifier.padding(14.dp))
                Text("Your score: ${userScore.value}", Modifier.padding(14.dp))

                Spacer(modifier = Modifier.height(16.dp))

                // Display answer options
                val options = remember(currentIndex.value) {
                    val shuffledFlags = flags.shuffled()
                    val uniqueOptions = mutableSetOf(correctAnswer)
                    for (flag in shuffledFlags) {
                        if (uniqueOptions.size < 4) {
                            uniqueOptions.add(flag.second)
                        }
                    }
                    uniqueOptions.shuffled().toList()
                }

                options.forEach { option ->
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when {
                                selectedAnswer.value == option && option == correctAnswer -> Color.Green
                                selectedAnswer.value == option && option != correctAnswer -> Color.Red
                                else -> Color.Gray
                            }
                        ),
                        onClick = {
                            selectedAnswer.value = option
                            if (option == correctAnswer) {
                                userScore.value++
                                correctSound.start()
                                Toast.makeText(context, "Correct!", Toast.LENGTH_SHORT).show()
                            } else {
                                wrongSound.start()
                                Toast.makeText(context, "Incorrect. The correct answer is $correctAnswer.", Toast.LENGTH_SHORT).show()
                                gameOver.value = true // Set game over state
                            }
                            answeredQuestions.value++

                            // Load the next question
                            if (!gameOver.value) {
                                currentIndex.value = (currentIndex.value + 1) % flags.size
                                selectedAnswer.value = null
                            }
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(option)
                    }
                }
            }
        }
    }
}




