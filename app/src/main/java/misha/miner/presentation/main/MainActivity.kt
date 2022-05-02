package misha.miner.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import misha.miner.common.ui.theme.MyMinerTheme
import misha.miner.presentation.AppMainScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyMinerTheme {
                AppMainScreen()
            }
        }
    }
}
