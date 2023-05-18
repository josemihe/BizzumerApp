package com.je.bizzumer.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.je.bizzumer.ui.screens.HomeScreen
import com.je.bizzumer.ui.theme.BizzumerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            BizzumerTheme() {
                Surface(modifier = Modifier. fillMaxSize(), color =
                MaterialTheme. colors.background ) {
                    HomeScreen(navController)
                }
            }
        }
    }
}