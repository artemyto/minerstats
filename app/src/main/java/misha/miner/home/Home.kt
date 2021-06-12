package misha.miner.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import misha.miner.R

@Composable
fun Home(homeViewModel: HomeViewModel, openDrawer: () -> Job) {

    val title = homeViewModel.title.collectAsState(initial = "")

    Column(modifier = Modifier
        .fillMaxHeight()
        .background(Color.LightGray)) {

        Button(onClick = {
            openDrawer()
        }) {
            Icon(painter = painterResource(id = R.drawable.img_burger_menu), contentDescription = null)
        }
        Text(text = title.value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
            textAlign = TextAlign.Center)
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