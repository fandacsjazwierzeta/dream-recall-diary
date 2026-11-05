package com.dreamrecall.diary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dreamrecall.diary.core.di.ServiceLocator
import com.dreamrecall.diary.core.theme.AppTheme
import com.dreamrecall.diary.presentation.app.AppRoot

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        ServiceLocator.start(applicationContext)
        setContent {
            AppTheme {
                AppRoot()
            }
        }
    }
}
