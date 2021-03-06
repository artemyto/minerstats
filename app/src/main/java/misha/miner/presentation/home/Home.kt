package misha.miner.presentation.home

import androidx.compose.material.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import misha.miner.common.ui.widgets.BaseScreen

@Composable
fun Home(drawerState: DrawerState) {

    val viewModel: HomeViewModel = hiltViewModel()

    viewModel.initialize()

    val eth: String by viewModel.eth.observeAsState("")
    val poolOutputList: List<String> by viewModel.poolOutputList.observeAsState(listOf())
    val balanceOutputList: List<String> by viewModel.balanceOutputList.observeAsState(
        listOf()
    )
    val workerOutputList: List<String> by viewModel.workerOutputList.observeAsState(
        listOf()
    )
    val sharesOutputList: List<String> by viewModel.sharesOutputList.observeAsState(
        listOf()
    )
    val isRefreshing: Boolean by viewModel.isRefreshing.observeAsState(false)

    BaseScreen(
        swipeEnabled = true,
        isRefreshing = isRefreshing,
        drawerState = drawerState,
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
