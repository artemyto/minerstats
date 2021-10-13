package misha.miner.screens.pc

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Monitor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import kotlinx.coroutines.Job
import misha.miner.models.common.ErrorState
import misha.miner.common.ui.widgets.BaseScreen
import misha.miner.common.ui.widgets.ErrorAlert

@Composable
fun PCStats(viewModel: PCStatsViewModel, openDrawer: () -> Job) {

    viewModel.initialize()

    val status by viewModel.status.collectAsState()
    val error by viewModel.error.collectAsState()

    val outputList: MutableList<String> by viewModel.outputList.observeAsState(mutableListOf())

    val selectedPC by viewModel.selectedPC.collectAsState()
    val pcLabelList: MutableList<String> by viewModel.pcLabelList.observeAsState(mutableListOf())

    val isRefreshing: Boolean by viewModel.isRefreshing.observeAsState(false)

    Scaffold(
        bottomBar = {
            BottomNavigation {
                pcLabelList.forEachIndexed { index, label ->
                    BottomNavigationItem(
                        icon = { Icon(Icons.Outlined.Monitor, null) },
                        label = { Text(label) },
                        selected = index == selectedPC,
                        onClick = {
                            viewModel.selectPC(index)
                        }
                    )
                }
            }
        }
    ) {
        BaseScreen(
            swipeEnabled = true,
            isRefreshing = isRefreshing,
            openDrawer = openDrawer,
            refreshClicked = { viewModel.runClicked() },
            list = mutableListOf(status).apply { addAll(outputList) }
        )
    }

    if (error is ErrorState.Error) {
        val message = (error as ErrorState.Error).message

        ErrorAlert(message = message) {
            viewModel.error.value = ErrorState.None
        }
    }
}