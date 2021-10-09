package misha.miner.screens.currencies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.Job
import misha.miner.R
import misha.miner.models.coinmarketcap.data.Listing

@Composable
fun Currencies(viewModel: CurrenciesViewModel, openDrawer: () -> Job) {

    viewModel.initialize()

    val outputList: List<Listing> by viewModel.currencies.observeAsState(mutableListOf())
    val isRefreshing: Boolean by viewModel.isRefreshing.observeAsState(false)

    ListingsScreen(
        openDrawer = openDrawer,
        isRefreshing = isRefreshing,
        refreshClicked = viewModel::runClicked,
        list = outputList
    )
}

@Composable
fun ListingsScreen(
    openDrawer: () -> Job,
    isRefreshing: Boolean,
    refreshClicked: () -> Unit,
    list: List<Listing>
) {
    SwipeRefresh(
        swipeEnabled = true,
        state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
        onRefresh = refreshClicked,
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = trigger,
                contentColor = MaterialTheme.colors.primary
            )
        }
    ) {
        ConstraintLayout(Modifier.fillMaxWidth()) {

            val (menu, lazyList) = createRefs()

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

            LazyColumn(
                modifier = Modifier.constrainAs(lazyList) {
                    linkTo(parent.start, parent.end, startMargin = 16.dp, endMargin = 16.dp)
                    linkTo(menu.bottom, parent.bottom, topMargin = 16.dp, bias = 0F)
                    width = Dimension.fillToConstraints
                }
            ) {

                items(list.size) { index ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = list[index].name,
                            style = MaterialTheme.typography.h5
                        )

                        Text(
                            text = "%.2f".format(list[index].quote.usd.price),
                            style = MaterialTheme.typography.h5
                        )
                    }
                    Divider(color = Color.Transparent, thickness = 5.dp)
                }
            }
        }
    }
}