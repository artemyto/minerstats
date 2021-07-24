package misha.miner.screens.pc

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.Job
import misha.miner.models.common.ErrorState
import misha.miner.ui.common.BaseScreen

@Composable
fun PCStats(viewModel: PCStatsViewModel, openDrawer: () -> Job) {

    viewModel.initialize()

    val status by viewModel.status.collectAsState()
    val error by viewModel.error.collectAsState()

    val outputList: MutableList<String> by viewModel.outputList.observeAsState(mutableListOf())

    BaseScreen(
        openDrawer = openDrawer,
        refreshClicked = { viewModel.runClicked() },
        list = mutableListOf(status).apply { addAll(outputList) }
    )

    if (error is ErrorState.Error) {
        val message = (error as ErrorState.Error).message
        AlertDialog(
            onDismissRequest = {  },
            title = { Text(text = "Error") },
            text = { Text(
                text = message
            ) },

            confirmButton = {

            },

            dismissButton = {
                Button(onClick = {
                    viewModel.error.value = ErrorState.None
                },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Blue,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Okay")
                }
            }
        )
    }
}