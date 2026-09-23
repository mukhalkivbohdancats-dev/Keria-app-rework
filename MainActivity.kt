package com.example.keriaapp

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RectangleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF008080) // Ретро бірюзовий фон (Windows 95/98)
                ) {
                    KeriaMainScreen()
                }
            }
        }
    }
}

@Composable
fun KeriaMainScreen() {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var timerSeconds by remember { mutableStateOf(10) }
    var isTimerRunning by remember { mutableStateOf(false) }

    // Логіка відліку таймера
    LaunchedEffect(isTimerRunning, timerSeconds) {
        if (isTimerRunning && timerSeconds > 0) {
            delay(1000L)
            timerSeconds--
        } else if (isTimerRunning && timerSeconds == 0) {
            isTimerRunning = false
            showDialog = true
            
            // Відтворення meow.mp3
            try {
                val rawId = context.resources.getIdentifier("meow", "raw", context.packageName)
                if (rawId != 0) {
                    val mediaPlayer = MediaPlayer.create(context, rawId)
                    mediaPlayer?.start()
                    mediaPlayer?.setOnCompletionListener { mp -> mp.release() }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "KERIA APP",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Відображення таймера
        Text(
            text = "Timer: $timerSeconds s",
            fontSize = 22.sp,
            color = Color.Yellow,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Кнопки управління
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = {
                    if (timerSeconds == 0) timerSeconds = 10
                    isTimerRunning = true
                },
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0C0C0))
            ) {
                Text("Start Timer", color = Color.Black, fontFamily = FontFamily.Monospace)
            }

            Button(
                onClick = {
                    showDialog = true
                    try {
                        val rawId = context.resources.getIdentifier("meow", "raw", context.packageName)
                        if (rawId != 0) {
                            val mediaPlayer = MediaPlayer.create(context, rawId)
                            mediaPlayer?.start()
                            mediaPlayer?.setOnCompletionListener { mp -> mp.release() }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0C0C0))
            ) {
                Text("Trigger Dialog", color = Color.Black, fontFamily = FontFamily.Monospace)
            }
        }
    }

    // Ретро діалог помилки
    if (showDialog) {
        RetroErrorDialog(
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun RetroErrorDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RectangleShape,
            color = Color(0xFFC0C0C0), // Ретро сірий фон
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color.White, RectangleShape)
                .padding(2.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(Color(0xFFC0C0C0))
                    .padding(4.dp)
            ) {
                // Заголовок вікна
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF000080)) // Ретро синій заголовок
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "System Error",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Button(
                        onClick = onDismiss,
                        shape = RectangleShape,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0C0C0))
                    ) {
                        Text("X", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Вміст повідомлення
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.Red, RectangleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "An unexpected meow error occurred in Keria App!",
                        color = Color.Black,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Кнопка OK
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onDismiss,
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0C0C0)),
                        modifier = Modifier.border(1.dp, Color.Black, RectangleShape)
                    ) {
                        Text(
                            text = "OK",
                            color = Color.Black,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
