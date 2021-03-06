package misha.miner.common.ui.widgets

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import misha.miner.R
import misha.miner.common.util.openWithScope

@Composable
fun BaseScreen(
    modifier: Modifier = Modifier,
    swipeEnabled: Boolean,
    isRefreshing: Boolean,
    drawerState: DrawerState,
    refreshClicked: () -> Unit,
    list: List<String>,
    customTopComposable: @Composable (() -> Unit)? = null,
    onRunClicked: (() -> Unit)? = null,
) {

    val scope = rememberCoroutineScope()

    SwipeRefresh(
        modifier = modifier,
        swipeEnabled = swipeEnabled,
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

            val (menu, lazyList, run) = createRefs()

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

            onRunClicked?.let {
                Button(
                    modifier = Modifier.constrainAs(run) {
                        top.linkTo(menu.top)
                        bottom.linkTo(menu.bottom)
                        end.linkTo(parent.end, 16.dp)
                        height = Dimension.fillToConstraints
                    },
                    onClick = { onRunClicked() }
                ) {
                    Text(text = "run")
                }
            }

            SelectionContainer(
                modifier = Modifier.constrainAs(lazyList) {
                    linkTo(parent.start, parent.end, startMargin = 16.dp, endMargin = 16.dp)
                    linkTo(menu.bottom, parent.bottom, topMargin = 16.dp, bias = 0F)
                    width = Dimension.fillToConstraints
                }
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {

                    customTopComposable?.let {
                        item {
                            customTopComposable.invoke()
                        }
                    }

                    items(list.size) { index ->

                        Text(list[index])
                    }
                }
            }
        }
    }
}
