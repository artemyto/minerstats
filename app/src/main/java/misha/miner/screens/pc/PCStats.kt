package misha.miner.screens.pc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.Job
import misha.miner.R

@Composable
fun PCStats(viewModel: PCStatsViewModel, openDrawer: () -> Job) {

    viewModel.initialize()

    val status by viewModel.status.collectAsState()

    val outputList: MutableList<String> by viewModel.outputList.observeAsState(mutableListOf())

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
        Text(text = status)
        StringList(items = outputList)
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