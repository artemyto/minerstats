package misha.miner.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import misha.miner.ui.theme.MyMinerTheme

@Composable
fun Settings(settingsViewModel: SettingsViewModel) {

    val title = settingsViewModel.title.collectAsState(initial = "")

    var wallet by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var port by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var command by rememberSaveable { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxHeight()
        .background(Color.White)) {

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
        TextField(value = command,
            onValueChange = {
                command = it
            },
            label = { Text("Добавить команду") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    MyMinerTheme() {
        val settingsViewModel = SettingsViewModel()
        Settings(settingsViewModel)
    }
}