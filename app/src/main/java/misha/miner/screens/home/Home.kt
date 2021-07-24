package misha.miner.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import kotlinx.coroutines.Job
import misha.miner.ui.common.BaseScreen

@Composable
fun Home(viewModel: HomeViewModel, openDrawer: () -> Job) {

    viewModel.initialize()

    val eth: String by viewModel.eth.observeAsState("")
    val poolOutputList: MutableList<String> by viewModel.poolOutputList.observeAsState(mutableListOf())
    val balanceOutputList: MutableList<String> by viewModel.balanceOutputList.observeAsState(
        mutableListOf()
    )

    BaseScreen(
        openDrawer = openDrawer,
        refreshClicked = { viewModel.runClicked() },
        list = mutableListOf<String>().apply {
            if (eth.isNotBlank()) add(eth)
            addAll(balanceOutputList)
            addAll(poolOutputList)
        }
    )
}