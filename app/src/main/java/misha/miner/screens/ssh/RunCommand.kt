package misha.miner.screens.ssh

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import misha.miner.R
import misha.miner.ui.common.ExposedDropDownMenu

@Composable
fun RunCommand(viewModel: RunCommandViewModel, openDrawer: () -> Job) {

    viewModel.initialize()

    val status by viewModel.status.collectAsState()
    val output by viewModel.output.collectAsState()
    val pc: String by viewModel.pc.observeAsState("")

    val commandList: MutableList<String> by viewModel.commandList.observeAsState(mutableListOf())
    val pcLabelList: MutableList<String> by viewModel.pcLabelList.observeAsState(mutableListOf())

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

        Box(Modifier.padding(top = 16.dp, bottom = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.fillMaxWidth()) {
                ExposedDropDownMenu(
                    text = pc,
                    suggestions = pcLabelList,
                    onTextChanged = {
                        viewModel.selectedPC(it)
                    }
                )
            }
        }

        if (pc.isNotBlank()) {
            Text(text = status)
            Row(modifier = Modifier.fillMaxWidth()) {
                ExposedDropDownMenu(
                    suggestions = commandList,
                    onTextChanged = {
                        viewModel.commandSelected(it)
                    }
                )
                Button(onClick = { viewModel.runClicked() }) {
                    Text(text = "run")
                }
            }
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                Text(text = output)
            }
        }
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