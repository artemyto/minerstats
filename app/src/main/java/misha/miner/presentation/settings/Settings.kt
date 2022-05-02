package misha.miner.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.Job
import misha.miner.R
import misha.miner.common.ui.widgets.ExposedDropDownMenu

@Composable
fun Settings(openDrawer: () -> Job) {

    val viewModel: SettingsViewModel = hiltViewModel()

    viewModel.initialization()

    val wallet: State<String> = viewModel.wallet.collectAsState()
    val address: String by viewModel.address.observeAsState("")
    val port: String by viewModel.port.observeAsState("")
    val nameState: State<String> = viewModel.name.observeAsState("")
    val password: String by viewModel.password.observeAsState("")
    var command by rememberSaveable { mutableStateOf("") }
    val pc: String by viewModel.pc.observeAsState("")

    val commandList: List<String> by viewModel.commandList.observeAsState(listOf())
    val pcLabelList: List<String> by viewModel.pcLabelList.observeAsState(listOf())

    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {

        val (
            menu,
            refresh,
            walletText,
            commandDropDown,
            pcDropDown,
            add,
            new,
            remove,
            addressText,
            portText,
            nameText,
            passwordText
        ) = createRefs()

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
            onClick = { viewModel.save() },
            modifier = Modifier.constrainAs(refresh) {
                top.linkTo(parent.top, margin = 16.dp)
                end.linkTo(parent.end, margin = 16.dp)
                linkTo(menu.top, menu.bottom, topMargin = 0.dp, bottomMargin = 0.dp)
                height = Dimension.fillToConstraints
            }
        ) {
            Text(text = "save")
        }

        TextField(
            modifier = Modifier.constrainAs(walletText) {
                top.linkTo(menu.bottom, 16.dp)
                linkTo(parent.start, parent.end, 16.dp, 16.dp)
                width = Dimension.fillToConstraints
            },
            value = wallet.value,
            onValueChange = {
                viewModel.inputWallet(it)
            },
            label = { Text("Кошелёк") }
        )

        ExposedDropDownMenu(
            modifier = Modifier.constrainAs(commandDropDown) {
                top.linkTo(walletText.bottom, 16.dp)
                linkTo(parent.start, add.start, startMargin = 16.dp, endMargin = 16.dp)
                width = Dimension.fillToConstraints
            },
            label = { Text("Добавить команду") },
            suggestions = commandList,
            onTextChanged = {
                command = it
            },
            disableListAction = true
        )

        Button(
            modifier = Modifier.constrainAs(add) {
                top.linkTo(commandDropDown.top)
                height = Dimension.value(56.dp)
                end.linkTo(parent.end, 16.dp)
            },
            onClick = {
                viewModel.addToCommandList(command)
                command = ""
            }
        ) {
            Text(text = "add")
        }

        ExposedDropDownMenu(
            modifier = Modifier.constrainAs(pcDropDown) {
                top.linkTo(add.bottom, 16.dp)
                linkTo(parent.start, new.start, startMargin = 16.dp, endMargin = 16.dp)
                width = Dimension.fillToConstraints
            },
            label = { Text("Выбрать ПК") },
            text = pc,
            suggestions = pcLabelList,
            onTextChanged = {
                viewModel.selectedPC(it)
            }
        )

        Button(
            modifier = Modifier.constrainAs(new) {
                top.linkTo(pcDropDown.top)
                height = Dimension.value(56.dp)
                end.linkTo(remove.start, 16.dp)
            },
            onClick = {
                viewModel.newPC()
            }
        ) {
            Text(text = "New pc")
        }

        Button(
            modifier = Modifier.constrainAs(remove) {
                top.linkTo(pcDropDown.top)
                height = Dimension.value(56.dp)
                end.linkTo(parent.end, 16.dp)
            },
            onClick = {
                viewModel.removePC()
            }
        ) {
            Text(text = "Remove pc")
        }

        if (pc.isNotBlank()) {
            TextField(
                modifier = Modifier.constrainAs(addressText) {
                    top.linkTo(new.bottom, 16.dp)
                    linkTo(parent.start, parent.end, 16.dp, 16.dp)
                    width = Dimension.fillToConstraints
                },
                value = address,
                onValueChange = {
                    viewModel.inputAddress(it)
                },
                label = { Text("Адрес сервера") }
            )
            TextField(
                modifier = Modifier.constrainAs(portText) {
                    top.linkTo(addressText.bottom, 16.dp)
                    linkTo(parent.start, parent.end, 16.dp, 16.dp)
                    width = Dimension.fillToConstraints
                },
                value = port,
                onValueChange = {
                    viewModel.inputPort(it)
                },
                label = { Text("Порт") }
            )
            TextField(
                modifier = Modifier.constrainAs(nameText) {
                    top.linkTo(portText.bottom, 16.dp)
                    linkTo(parent.start, parent.end, 16.dp, 16.dp)
                    width = Dimension.fillToConstraints
                },
                value = nameState.value,
                onValueChange = {
                    viewModel.inputName(it)
                },
                label = { Text("Имя") }
            )
            TextField(
                modifier = Modifier.constrainAs(passwordText) {
                    top.linkTo(nameText.bottom, 16.dp)
                    linkTo(parent.start, parent.end, 16.dp, 16.dp)
                    width = Dimension.fillToConstraints
                },
                value = password,
                onValueChange = {
                    viewModel.inputPassword(it)
                },
                label = { Text("Пароль") }
            )
        }
    }
}

// @Preview(showBackground = true)
// @Composable
// fun SettingsPreview() {
//    MyMinerTheme() {
//        val settingsViewModel = SettingsViewModel()
//        Settings(settingsViewModel, openDrawer)
//    }
// }
