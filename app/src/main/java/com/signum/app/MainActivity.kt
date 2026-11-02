package com.signum.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.signum.app.di.AppContainer
import com.signum.app.presentation.SignumNavGraph
import com.signum.app.presentation.theme.SignumTheme

class MainActivity : ComponentActivity() {

    companion object {
        lateinit var appContainer: AppContainer
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContainer = AppContainer(this)

        setContent {
            SignumTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SignumNavGraph()
                }
            }
        }
    }
}
