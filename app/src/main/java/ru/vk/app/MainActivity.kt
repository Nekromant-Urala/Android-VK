package ru.vk.app

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.vk.app.ui.theme.AndroidVKTheme
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidVKTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        onOpenSecondClick = ::openSecondActivity,
                        onDialClick = ::dialPhoneNumber,
                        onShareClick = ::shareText
                    )
                }
            }
        }
    }

    private fun openSecondActivity(text: String) {
        val intent = Intent(this, SecondActivity::class.java).apply {
            putExtra("EXTRA_TEXT", text)
        }
        startActivity(intent)
    }

    private fun dialPhoneNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = "tel:$phoneNumber".toUri()
        }
        startActivity(intent)
    }

    private fun shareText(text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        val chooser = Intent.createChooser(intent, "Поделиться через...")
        startActivity(chooser)
    }

    @Composable
    fun MainScreen(
        modifier: Modifier = Modifier,
        onOpenSecondClick: (String) -> Unit,
        onDialClick: (String) -> Unit,
        onShareClick: (String) -> Unit
    ) {
        var text by remember { mutableStateOf("") }
        var isPhoneError by remember { mutableStateOf(false) }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                    isPhoneError = false
                },
                label = { Text("Введите текст или номер") },
                isError = isPhoneError,
                supportingText = {
                    if (isPhoneError) {
                        Text(
                            text = "Некорректный номер телефона",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onOpenSecondClick(text) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Открыть вторую Activity")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (text.isNotBlank() && Patterns.PHONE.matcher(text).matches()) {
                        isPhoneError = false
                        onDialClick(text)
                    } else {
                        isPhoneError = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Позвонить другу")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onShareClick(text) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Поделиться текстом")
            }
        }
    }
}
