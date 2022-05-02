package misha.miner.screens.pc

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Monitor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.Job
import misha.miner.common.ui.widgets.BaseScreen
import misha.miner.common.ui.widgets.ErrorAlert
import misha.miner.models.common.ErrorState

@Composable
fun PCStats(openDrawer: () -> Job) {

    val viewModel: PCStatsViewModel = hiltViewModel()

    viewModel.initialize()

    val status by viewModel.status.collectAsState()
    val error by viewModel.error.collectAsState()

    val outputList: List<String> by viewModel.outputList.observeAsState(listOf())

    val selectedPC by viewModel.selectedPC.collectAsState()
    val pcLabelList: List<String> by viewModel.pcLabelList.observeAsState(listOf())

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
    ) { paddingValues ->
        BaseScreen(
            modifier = Modifier.padding(paddingValues),
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
