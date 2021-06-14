package misha.miner.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import misha.miner.ui.common.ExposedDropDownMenu

@Composable
fun Home(viewModel: HomeViewModel, openDrawer: () -> Job) {

    viewModel.initialize()

    val status by viewModel.status.collectAsState()
    val output by viewModel.output.collectAsState()

    val commandList: MutableList<String> by viewModel.commandList.observeAsState(mutableListOf())

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(Color.LightGray)
    ) {

        Button(onClick = {
            openDrawer()
        }) {
            Icon(
                painter = painterResource(id = R.drawable.img_burger_menu),
                contentDescription = null
            )
        }
        Text(text = status)
        Row(modifier = Modifier.fillMaxWidth()) {
            ExposedDropDownMenu(
                suggestions = commandList,
                onItemSelected = {
                    viewModel.commandSelected(it)
                }
            )
            Button(onClick = { viewModel.runClicked() }) {
                Text(text = "run")
            }
        }
        Button(onClick = { viewModel.runClicked() }) {
            Text(text = "run")
        }
        Text(text = output)
    }
}

//@Preview(showBackground = true)
//@Composable
//fun HomePreview() {
//    MyMinerTheme() {
//        val homeViewModel = HomeViewModel()
//        Home(homeViewModel, openDrawer)
//    }
//}