package com.example.keriaapp

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RectangleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

// =======================================================
// ⚙️ НАЛАШТУВАННЯ ТА ТЕКСТИ ДОДАТКА
// =======================================================
object AppStrings {
    var appTitle = "KERIA APP"
    
    // Повідомлення помилок
    var dialogTitle = "System Error"
    var dialogMessage = "An unexpected meow error occurred in Keria App!"
    
    // Звук та інтервал (500 мс = 0.5 секунди)
    var soundResourceName = "meow"
    var errorIntervalMs = 500L 
    
    // Назва файлу зображення фону в res/drawable (без розширення .png/.jpg)
    var bgImageName = "bg_image"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    KeriaMainScreen()
                }
            }
        }
    }
}

@Composable
fun KeriaMainScreen() {
    val context = LocalContext.current
    var errorCount by remember { mutableIntStateOf(0) }

    // Безперервний спаун помилок кожні 0.5 секунди
    LaunchedEffect(Unit) {
        while (isActive) {
            errorCount++
            playSound(context)
            delay(AppStrings.errorIntervalMs)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. ФОНОВЕ ЗОБРАЖЕННЯ
        val bgId = context.resources.getIdentifier(AppStrings.bgImageName, "drawable", context.packageName)
        if (bgId != 0) {
            Image(
                painter = painterResource(id = bgId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Запасний фон (бірюзовий), якщо зображення ще не додано
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF008080))
            )
        }

        // 2. НАКОПИЧЕНІ ПОМИЛКИ
        for (i in 1..errorCount) {
            RetroErrorDialog(index = i)
        }
    }
}

@Composable
fun RetroErrorDialog(index: Int) {
    // Невеликий хаотичний зсув кожного наступного вікна, щоб вони заповнювали екран
    val offsetX = ((index * 17) % 140 - 70).dp
    val offsetY = ((index * 31) % 240 - 120).dp

    Dialog(onDismissRequest = { /* Неможливо закрити */ }) {
        Surface(
            shape = RectangleShape,
            color = Color(0xFFC0C0C0), // Ретро сірий фон вікна
            modifier = Modifier
                .offset(x = offsetX, y = offsetY)
                .fillMaxWidth(0.85f)
                .border(2.dp, Color.White, RectangleShape)
                .padding(2.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(Color(0xFFC0C0C0))
                    .padding(4.dp)
            ) {
                // Ретро синій заголовок вікна (БЕЗ КНОПКИ ЗАКРИТТЯ "X")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF000080))
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${AppStrings.dialogTitle} #$index",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Вміст повідомлення
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
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
                        text = AppStrings.dialogMessage,
                        color = Color.Black,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// Функція відтворення звуку
private fun playSound(context: android.content.Context) {
    try {
        val rawId = context.resources.getIdentifier(AppStrings.soundResourceName, "raw", context.packageName)
        if (rawId != 0) {
            val mediaPlayer = MediaPlayer.create(context, rawId)
            mediaPlayer?.start()
            mediaPlayer?.setOnCompletionListener { mp -> mp.release() }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
