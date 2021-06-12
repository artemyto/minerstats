package misha.miner.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.Job
import misha.miner.R

@Composable
fun Settings(settingsViewModel: SettingsViewModel, openDrawer: () -> Job) {

    val title = settingsViewModel.title.collectAsState(initial = "")

    var wallet by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var port by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var command by rememberSaveable { mutableStateOf("") }

    val commandList by rememberSaveable { mutableStateOf(mutableListOf<String>()) }

    Column(modifier = Modifier
        .fillMaxHeight()
        .background(Color.White)) {

        Button(onClick = {
            openDrawer()
        }) {
            Icon(painter = painterResource(id = R.drawable.img_burger_menu), contentDescription = null)
        }
        TextField(value = wallet,
            onValueChange = {
                wallet = it
            },
            label = { Text("Кошелёк") }
        )
        TextField(value = address,
            onValueChange = {
                address = it
            },
            label = { Text("Адрес сервера") }
        )
        TextField(value = port,
            onValueChange = {
                port = it
            },
            label = { Text("Порт") }
        )
        TextField(value = name,
            onValueChange = {
                name = it
            },
            label = { Text("Имя") }
        )
        TextField(value = password,
            onValueChange = {
                password = it
            },
            label = { Text("Пароль") }
        )
        Row {
            TextField(value = command,
                onValueChange = {
                    command = it
                },
                label = { Text("Добавить команду") }
            )
            Button(onClick = {
                commandList.add(command)
                command = ""
            }) {
                Text("Add")
            }
        }
        CommandList(items = commandList)
    }
}

@Composable
fun CommandList(
    modifier: Modifier = Modifier,
    items: List<String>
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(items) { index, item ->
            Text(item)
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