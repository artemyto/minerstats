package misha.miner.presentation.currencies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import misha.miner.R
import misha.miner.common.ui.widgets.SearchView
import misha.miner.common.util.openWithScope
import java.util.*

@Composable
fun Currencies(drawerState: DrawerState) {

    val viewModel: CurrenciesViewModel = hiltViewModel()

    viewModel.initialize()

    val outputList: List<CurrencyVO> by viewModel.currencies.observeAsState(listOf())
    val isRefreshing: Boolean by viewModel.isRefreshing.observeAsState(false)

    ListingsScreen(
        drawerState = drawerState,
        isRefreshing = isRefreshing,
        refreshClicked = viewModel::runClicked,
        list = outputList
    )
}

@Composable
fun ListingsScreen(
    drawerState: DrawerState,
    isRefreshing: Boolean,
    refreshClicked: () -> Unit,
    list: List<CurrencyVO>
) {
    val scope = rememberCoroutineScope()

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

            val (menu, search, lazyList) = createRefs()

            Button(
                onClick = {
                    drawerState.openWithScope(scope)
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

            val textState = remember { mutableStateOf(TextFieldValue("")) }

            SearchView(
                state = textState,
                modifier = Modifier.constrainAs(search) {
                    linkTo(menu.end, parent.end, startMargin = 8.dp, endMargin = 16.dp)
                    linkTo(menu.top, menu.bottom)
                    width = Dimension.fillToConstraints
                }
            )

            LazyColumn(
                modifier = Modifier.constrainAs(lazyList) {
                    linkTo(parent.start, parent.end, startMargin = 16.dp, endMargin = 16.dp)
                    linkTo(menu.bottom, parent.bottom, topMargin = 16.dp, bias = 0F)
                    width = Dimension.fillToConstraints
                }
            ) {

                val searchedText = textState.value.text

                val filteredListings = if (searchedText.isEmpty()) {
                    list
                } else {
                    val resultList = mutableListOf<CurrencyVO>()
                    for (listing in list) {
                        if (
                            listing.name.lowercase(Locale.getDefault())
                                .contains(searchedText.lowercase(Locale.getDefault()))
                        ) {
                            resultList.add(listing)
                        }
                    }
                    resultList
                }

                items(filteredListings.size) { index ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = filteredListings[index].name,
                            style = MaterialTheme.typography.h5
                        )

                        Text(
                            text = "%.2f".format(filteredListings[index].quote.usd?.price),
                            style = MaterialTheme.typography.h5
                        )
                    }
                    Divider(color = Color.Transparent, thickness = 5.dp)
                }
            }
        }
    }
}
