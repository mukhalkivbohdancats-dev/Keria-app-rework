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
// Змінюйте будь-який текст у лапках або параметри тут:
// =======================================================
object AppStrings {
    var appTitle = "KERIA APP"
    
    // Тексти діалогового вікна помилки
    var dialogTitle = "керя лох"
    var dialogMessage = "керя лох"
    var dialogOkButton = "Да"
    var dialogCloseButton = "X"
    
    // Звук та інтервал (500 мс = 0.5 секунди)
    var soundResourceName = "meow"
    var errorIntervalMs = 500L 
    
    // Назва файлу зображення фону в res/drawable (без .png / .jpg)
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
    // Список активних діалогів (зберігся ID кожного вікна)
    val activeDialogs = remember { mutableStateListOf<Int>() }

    // Безперервний спаун помилок кожні 0.5 секунди по всьому екрану
    LaunchedEffect(Unit) {
        var idCounter = 0
        while (isActive) {
            idCounter++
            activeDialogs.add(idCounter)
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
            // Запасний фон (бірюзовий Windows 95), якщо картинку ще не додано
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF008080))
            )
        }

        // 2. ВІДОБРАЖЕННЯ ВІКОН ПОМИЛОК ПО ВСЬОМУ ЕКРАНУ
        for (dialogId in activeDialogs) {
            RetroErrorDialog(
                index = dialogId,
                onDismiss = { activeDialogs.remove(dialogId) }
            )
        }
    }
}

@Composable
fun RetroErrorDialog(index: Int, onDismiss: () -> Unit) {
    // Широкий розмах зсуву, щоб помилки з'являлися по всьому екрану (зверху, знизу, зліва, справа)
    val offsetX = (((index * 37) % 260) - 130).dp
    val offsetY = (((index * 73) % 560) - 280).dp

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RectangleShape,
            color = Color(0xFFC0C0C0), // Ретро сірий фон
            modifier = Modifier
                .offset(x = offsetX, y = offsetY)
                .fillMaxWidth(0.82f)
                .border(2.dp, Color.White, RectangleShape)
                .padding(2.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(Color(0xFFC0C0C0))
                    .padding(4.dp)
            ) {
                // Ретро синій заголовок вікна з кнопкою закриття "X"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF000080))
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${AppStrings.dialogTitle} #$index",
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
                        Text(AppStrings.dialogCloseButton, color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
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
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

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
                            text = AppStrings.dialogOkButton,
                            color = Color.Black,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

// Допоміжна функція відтворення звуку meow.mp3
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
