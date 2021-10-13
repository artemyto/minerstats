package misha.miner.screens.wallet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import kotlinx.coroutines.Job
import misha.miner.models.common.ErrorState
import misha.miner.ui.common.BaseScreen
import misha.miner.ui.common.ErrorAlert

@Composable
fun Wallet(viewModel: WalletViewModel, openDrawer: () -> Job) {

    viewModel.initialize()

    val error by viewModel.error.collectAsState()

    val outputList: List<String> by viewModel.outputList.observeAsState(listOf())
    val isRefreshing: Boolean by viewModel.isRefreshing.observeAsState(false)

    BaseScreen(
        swipeEnabled = true,
        isRefreshing = isRefreshing,
        openDrawer = openDrawer,
        refreshClicked = viewModel::runClicked,
        list = mutableListOf<String>().apply {
            addAll(outputList)
        }
    )

    if (error is ErrorState.Error) {
        val message = (error as ErrorState.Error).message

        ErrorAlert(message = message) {
            viewModel.error.value = ErrorState.None
        }
    }
}