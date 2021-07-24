package misha.miner.screens.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import kotlinx.coroutines.Job
import misha.miner.R

@Composable
fun Home(viewModel: HomeViewModel, openDrawer: () -> Job) {

    viewModel.initialize()

    val eth: String by viewModel.eth.observeAsState("")
    val poolOutputList: MutableList<String> by viewModel.poolOutputList.observeAsState(mutableListOf())
    val balanceOutputList: MutableList<String> by viewModel.balanceOutputList.observeAsState(
        mutableListOf()
    )

    ConstraintLayout(Modifier.fillMaxWidth()) {

        val (menu, refresh, list) = createRefs()

        Button(
            onClick = {
                openDrawer()
            },
            modifier = Modifier.constrainAs(menu) {
                top.linkTo(parent.top, margin = 16.dp)
                start.linkTo(parent.start, margin = 16.dp)
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.img_burger_menu),
                contentDescription = null
            )
        }

        Button(
            onClick = { viewModel.runClicked() },
            modifier = Modifier.constrainAs(refresh) {
                top.linkTo(parent.top, margin = 16.dp)
                end.linkTo(parent.end, margin = 16.dp)
                linkTo(menu.top, menu.bottom, topMargin = 0.dp, bottomMargin = 0.dp)
                height = Dimension.fillToConstraints
            }
        ) {
            Text(text = "refresh")
        }
        
        LazyColumn(
            modifier = Modifier.constrainAs(list) {
                linkTo(parent.start, parent.end, startMargin = 16.dp, endMargin = 16.dp)
                linkTo(menu.bottom, parent.bottom, topMargin = 16.dp, bias = 0F)
                width = Dimension.fillToConstraints
            }
        ) {

            item {
                Text(eth)
            }

            items(balanceOutputList.size) { index ->
                Text(balanceOutputList[index])
            }

            items(poolOutputList.size) { index ->
                Text(poolOutputList[index])
            }
        }
    }
}