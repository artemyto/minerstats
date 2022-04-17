package misha.miner.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import kotlinx.coroutines.Job
import misha.miner.common.ui.widgets.BaseScreen

@Composable
fun Home(viewModel: HomeViewModel, openDrawer: () -> Job) {

    viewModel.initialize()

    val eth: String by viewModel.eth.observeAsState("")
    val poolOutputList: MutableList<String> by viewModel.poolOutputList.observeAsState(mutableListOf())
    val balanceOutputList: MutableList<String> by viewModel.balanceOutputList.observeAsState(
        mutableListOf()
    )
    val workerOutputList: MutableList<String> by viewModel.workerOutputList.observeAsState(
        mutableListOf()
    )
    val sharesOutputList: MutableList<String> by viewModel.sharesOutputList.observeAsState(
        mutableListOf()
    )
    val isRefreshing: Boolean by viewModel.isRefreshing.observeAsState(false)

    BaseScreen(
        swipeEnabled = true,
        isRefreshing = isRefreshing,
        openDrawer = openDrawer,
        refreshClicked = viewModel::runClicked,
        list = mutableListOf<String>().apply {
            if (eth.isNotBlank()) add(eth)
            addAll(balanceOutputList)
            addAll(poolOutputList)
            addAll(workerOutputList)
            addAll(sharesOutputList)
        }
    )
}
