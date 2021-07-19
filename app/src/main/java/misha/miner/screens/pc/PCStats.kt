package misha.miner.screens.pc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Job
import misha.miner.R
import misha.miner.models.common.ErrorState

@Composable
fun PCStats(viewModel: PCStatsViewModel, openDrawer: () -> Job) {

    viewModel.initialize()

    val status by viewModel.status.collectAsState()
    val error by viewModel.error.collectAsState()

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

    if (error is ErrorState.Error) {
        val message = (error as ErrorState.Error).message
        AlertDialog(
            onDismissRequest = {  },
            title = { Text(text = "Error") },
            text = { Text(
                text = message
            ) },

            confirmButton = {

            },

            dismissButton = {
                Button(onClick = {
                    viewModel.error.value = ErrorState.None
                },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Blue,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Okay")
                }
            }
        )
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