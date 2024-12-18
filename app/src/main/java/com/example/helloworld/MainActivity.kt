package com.example.helloworld

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import android.media.MediaPlayer


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlagQuizApp()
        }
    }
}

@Composable
fun FlagQuizApp() {
    // List of flags and corresponding country names
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

    val currentIndex = remember { mutableStateOf((0 until flags.size).random()) } // Track current flag index
    val context = LocalContext.current

    // Simple Gradient Background
    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF00BCD4), Color(0xFF4CAF50))
    )

    val correctAnswer = flags[currentIndex.value].second
    val userScore = remember{ mutableStateOf(0)} // Track user score
    val buttonColors = remember { mutableStateOf(Color.Gray) }
    val selectedAnswer = remember { mutableStateOf<String?>(null) }
    val correctSound = MediaPlayer.create(context, R.raw.correct_answer)
    val wrongSound = MediaPlayer.create(context, R.raw.wrong_answer)
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
            // Display the current flag
            Image(
                painter = painterResource(id = flags[currentIndex.value].first),
                contentDescription = "Flag of ${flags[currentIndex.value].second}",
                modifier = Modifier
                    .size(200.dp) // Equal size for all flags
                    .padding(16.dp)
            )

            // Display the question
            Text(
                text = "Which country's flag is this?",
                modifier = Modifier.padding(10.dp)
            )
            Text(
                text = "Your score: ${userScore.value}",
                modifier = Modifier.padding(10.dp),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Display the multiple-choice options
            options.forEach { option ->
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor  = when {
                            selectedAnswer.value == option && option == correctAnswer -> Color.Green
                            selectedAnswer.value == option && option != correctAnswer -> Color.Red
                            else -> Color.Gray
                        }
                    ),
                    onClick = {
                        selectedAnswer.value = option
                        if (option == correctAnswer) {
                            buttonColors.value = Color.Green
                            userScore.value += 1
                            correctSound.start()
                            Toast.makeText(context, "Correct!", Toast.LENGTH_SHORT).show()
                        } else {
                            buttonColors.value = Color.Red
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

            // Navigation buttons
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(
                    onClick = {
                        // Navigate to the previous flag
                        currentIndex.value = if (currentIndex.value > 0) currentIndex.value - 1 else flags.size - 1
                        selectedAnswer.value = null
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Previous")
                }

                Button(
                    onClick = {
                        // Navigate to the next flag
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
