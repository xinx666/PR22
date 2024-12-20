package com.example.pr22

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.memorygame.MemoryGameApp
import com.example.pr22.ui.theme.PR22_NikolaenkoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PR22_NikolaenkoTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MemoryGameApp()
                }
            }
        }
    }
}

