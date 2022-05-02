package misha.miner.presentation.scanip

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.Job
import misha.miner.common.ui.widgets.BaseScreen
import misha.miner.common.ui.widgets.ErrorAlert
import misha.miner.data.models.common.ErrorState

@Composable
fun ScanIp(openDrawer: () -> Job) {

    val viewModel: ScanIpViewModel = hiltViewModel()

    viewModel.initialize()

    val error by viewModel.error.collectAsState()

    val outputList: List<String> by viewModel.outputList.observeAsState(listOf())

    val isRefreshing: Boolean by viewModel.isRefreshing.observeAsState(false)

    val selectedAddress: String by viewModel.selectedAddress.observeAsState("")

    Scaffold {
        BaseScreen(
            swipeEnabled = !isRefreshing,
            isRefreshing = isRefreshing,
            openDrawer = openDrawer,
            refreshClicked = { viewModel.runClicked() },
            list = outputList,
            customTopComposable = {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = selectedAddress,
                    onValueChange = {
                        viewModel.inputAddress(it)
                    },
                    label = { Text("Первые 3 числа ip-адреса") }
                )
            },
            onRunClicked = viewModel::runClicked,
        )
    }

    if (error is ErrorState.Error) {
        val message = (error as ErrorState.Error).message

        ErrorAlert(message = message) {
            viewModel.error.value = ErrorState.None
        }
    }
}
