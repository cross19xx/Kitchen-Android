package com.cross19xx.filmnest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cross19xx.filmnest.core.theme.FilmNestTheme
import com.cross19xx.filmnest.ui.home.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            FilmNestTheme {
                HomeScreen()
            }
        }
    }
}
