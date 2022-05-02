package misha.miner.presentation.ssh

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.Job
import misha.miner.R
import misha.miner.common.ui.widgets.ErrorAlert
import misha.miner.common.ui.widgets.ExposedDropDownMenu
import misha.miner.models.common.ErrorState

@Composable
fun RunCommand(openDrawer: () -> Job) {

    val viewModel: RunCommandViewModel = hiltViewModel()

    viewModel.initialize()

    val status by viewModel.status.collectAsState()
    val output by viewModel.output.collectAsState()
    val pc: String by viewModel.pc.observeAsState("")

    val error by viewModel.error.collectAsState()

    val commandList: List<String> by viewModel.commandList.observeAsState(listOf())
    val pcLabelList: List<String> by viewModel.pcLabelList.observeAsState(listOf())

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {

        val (menu, pcDropDown, statusText, commandDropDown, run, lazyList) = createRefs()

        Button(
            modifier = Modifier.constrainAs(menu) {
                top.linkTo(parent.top, 16.dp)
                start.linkTo(parent.start, 16.dp)
            },
            onClick = {
                openDrawer()
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.img_burger_menu),
                contentDescription = null
            )
        }

        ExposedDropDownMenu(
            modifier = Modifier.constrainAs(pcDropDown) {
                top.linkTo(menu.bottom, 16.dp)
                linkTo(parent.start, parent.end, startMargin = 16.dp, endMargin = 16.dp)
                width = Dimension.fillToConstraints
            },
            label = { Text("Выбрать ПК") },
            text = pc,
            suggestions = pcLabelList,
            onTextChanged = {
                viewModel.selectedPC(it)
            }
        )

        if (pc.isNotBlank()) {

            Text(
                modifier = Modifier.constrainAs(statusText) {
                    top.linkTo(pcDropDown.bottom, 16.dp)
                    linkTo(
                        parent.start,
                        parent.end,
                        startMargin = 16.dp,
                        endMargin = 16.dp,
                        bias = 0F
                    )
                },
                text = status
            )

            ExposedDropDownMenu(
                modifier = Modifier.constrainAs(commandDropDown) {
                    top.linkTo(statusText.bottom, 16.dp)
                    linkTo(parent.start, run.start, startMargin = 16.dp, endMargin = 16.dp)
                    width = Dimension.fillToConstraints
                },
                label = { Text("Выбрать или добавить команду") },
                suggestions = commandList,
                onTextChanged = {
                    viewModel.commandSelected(it)
                }
            )

            Button(
                modifier = Modifier.constrainAs(run) {
                    top.linkTo(commandDropDown.top)
                    height = Dimension.value(56.dp)
                    end.linkTo(parent.end, 16.dp)
                },
                onClick = { viewModel.runClicked() }
            ) {
                Text(text = "run")
            }

            SelectionContainer(
                modifier = Modifier
                    .constrainAs(lazyList) {
                        linkTo(run.bottom, parent.bottom, topMargin = 16.dp)
                        height = Dimension.fillToConstraints
                        linkTo(parent.start, parent.end, startMargin = 16.dp, endMargin = 16.dp)
                        width = Dimension.fillToConstraints
                    }
                    .verticalScroll(rememberScrollState())
                    .horizontalScroll(rememberScrollState())
            ) {
                Text(text = output)
            }
        }
    }

    if (error is ErrorState.Error) {
        val message = (error as ErrorState.Error).message

        ErrorAlert(message = message) {
            viewModel.error.value = ErrorState.None
        }
    }
}

// @Preview(showBackground = true)
// @Composable
// fun HomePreview() {
//    MyMinerTheme() {
//        val homeViewModel = HomeViewModel()
//        Home(homeViewModel, openDrawer)
//    }
// }
