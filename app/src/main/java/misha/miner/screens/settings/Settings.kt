package misha.miner.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import misha.miner.R
import misha.miner.ui.common.ExposedDropDownMenu

@Composable
fun Settings(viewModel: SettingsViewModel, openDrawer: () -> Job) {

    viewModel.initialization()

    val wallet: State<String> = viewModel.wallet.collectAsState()
    val address: String by viewModel.address.observeAsState("")
    val port: String by viewModel.port.observeAsState("")
    val nameState: State<String> = viewModel.name.observeAsState("")
    val password: String by viewModel.password.observeAsState("")
    var command by rememberSaveable { mutableStateOf("") }
    val pc: String by viewModel.pc.observeAsState("")

    val commandList: MutableList<String> by viewModel.commandList.observeAsState(mutableListOf())
    val pcLabelList: MutableList<String> by viewModel.pcLabelList.observeAsState(mutableListOf())

    Column(modifier = Modifier
        .fillMaxHeight()
        .background(Color.White)) {

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {

            Button(onClick = {
                openDrawer()
            }) {
                Icon(painter = painterResource(id = R.drawable.img_burger_menu), contentDescription = null)
            }
            Button(onClick = {
                viewModel.save()
            }) {
                Text(text = "Сохранить")
            }
        }
        Box(Modifier.padding(top = 16.dp, bottom = 8.dp)) {
            TextField(value = wallet.value,
                onValueChange = {
                    viewModel.inputWallet(it)
                },
                label = { Text("Кошелёк") }
            )
        }
        Box(Modifier.padding(top = 8.dp, bottom = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.fillMaxWidth()) {
                ExposedDropDownMenu(
                    label = { Text("Добавить команду") },
                    suggestions = commandList,
                    onTextChanged = {
                        command = it
                    },
                    disableListAction = true
                )
                Button(
                    onClick = {
                    viewModel.addToCommandList(command)
                    command = ""
                }) {
                    Text(text = "Add")
                }
            }
        }
        Box(Modifier.padding(top = 8.dp, bottom = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.fillMaxWidth()) {
                ExposedDropDownMenu(
                    label = { Text("Выбрать ПК") },
                    text = pc,
                    suggestions = pcLabelList,
                    onTextChanged = {
                        viewModel.selectedPC(it)
                    }
                )
                Button(
                    onClick = {
                        viewModel.newPC()
                    }) {
                    Text(text = "New pc")
                }
            }
        }
        if (pc.isNotBlank()) {
            TextField(value = address,
                onValueChange = {
                    viewModel.inputAddress(it)
                },
                label = { Text("Адрес сервера") }
            )
            TextField(value = port,
                onValueChange = {
                    viewModel.inputPort(it)
                },
                label = { Text("Порт") }
            )
            TextField(value = nameState.value,
                onValueChange = {
                    viewModel.inputName(it)
                },
                label = { Text("Имя") }
            )
            TextField(value = password,
                onValueChange = {
                    viewModel.inputPassword(it)
                },
                label = { Text("Пароль") }
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun SettingsPreview() {
//    MyMinerTheme() {
//        val settingsViewModel = SettingsViewModel()
//        Settings(settingsViewModel, openDrawer)
//    }
//}