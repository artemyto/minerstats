package misha.miner.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Job
import misha.miner.R

@Composable
fun Home(viewModel: HomeViewModel, openDrawer: () -> Job) {

    viewModel.initialize()

    val eth: String by viewModel.eth.observeAsState("")
    val poolOutputList: MutableList<String> by viewModel.poolOutputList.observeAsState(mutableListOf())
    val balanceOutputList: MutableList<String> by viewModel.balanceOutputList.observeAsState(mutableListOf())

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(Color.LightGray)
    ) {

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                openDrawer()
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.img_burger_menu),
                    contentDescription = null
                )
            }
            Button(onClick = { viewModel.runClicked() }) {
                Text(text = "refresh")
            }
        }
        Text(text = eth)
        StringList(items = balanceOutputList)
        StringList(items = poolOutputList)
    }
}

@Composable
fun StringList(
    modifier: Modifier = Modifier,
    items: List<String>
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(items) { index, item ->
            Text(item)
        }
    }
}