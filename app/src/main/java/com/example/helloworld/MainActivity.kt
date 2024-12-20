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
            .background(Color(0xFF2196F3)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Welcome to Flag Quiz!",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Button(
                onClick = onStartQuiz,
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Start Quiz")
            }
        }
    }
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

    val gradient = Brush.linearGradient(colors = listOf(Color(0xFF00BCD4), Color(0xFF4CAF50)))

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onBackToMenu,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(8.dp)
            ) {
                Text("Back to Menu")
            }

            Image(
                painter = painterResource(id = flags[currentIndex.value].first),
                contentDescription = "Flag of ${flags[currentIndex.value].second}",
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp)
            )

            Text("Which country's flag is this?", Modifier.padding(10.dp))
            Text("Your score: ${userScore.value}", Modifier.padding(10.dp))

            Spacer(modifier = Modifier.height(16.dp))

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
                            userScore.value += 1
                            correctSound.start()
                            Toast.makeText(context, "Correct!", Toast.LENGTH_SHORT).show()
                        } else {
                            userScore.value -= 1
                            wrongSound.start()
                            Toast.makeText(context, "Incorrect. The correct answer is $correctAnswer.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Text(option)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(
                    onClick = {
                        currentIndex.value = if (currentIndex.value > 0) currentIndex.value - 1 else flags.size - 1
                        selectedAnswer.value = null
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Previous")
                }

                Button(
                    onClick = {
                        currentIndex.value = (currentIndex.value + 1) % flags.size
                        selectedAnswer.value = null
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Next")
                }
            }
        }
    }
}
