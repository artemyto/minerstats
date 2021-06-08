package misha.miner.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import misha.miner.ui.theme.MyMinerTheme

@Composable
fun Home(homeViewModel: HomeViewModel) {

    val title = homeViewModel.title.collectAsState(initial = "")

    Column(modifier = Modifier
        .fillMaxHeight()
        .background(Color.LightGray)) {

        Text(text = title.value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
            textAlign = TextAlign.Center)
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    MyMinerTheme() {
        val homeViewModel = HomeViewModel()
        Home(homeViewModel)
    }
}