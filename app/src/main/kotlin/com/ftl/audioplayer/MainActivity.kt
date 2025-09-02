package com.ftl.audioplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ftl.audioplayer.ui.theme.FTLAudioPlayerTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║                    MAIN ACTIVITY                             ║
 * ║           FTL Hi-Res Audio Player Entry Point               ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FTLAudioPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("FTL Audio Player")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Welcome to $name",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FTLAudioPlayerTheme {
        Greeting("FTL Audio Player")
    }
}