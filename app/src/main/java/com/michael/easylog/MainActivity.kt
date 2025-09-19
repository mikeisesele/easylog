package com.michael.easylog

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.michael.easylog.ui.theme.EasyLogTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            EasyLogTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EasyLogDashboardPreview(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
