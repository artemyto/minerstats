package misha.miner.screens.drawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import misha.miner.common.ui.theme.MyMinerTheme

sealed class DrawerScreens(val title: String, val route: String) {
    object Home : DrawerScreens("Главная", "home")
    object PCStats : DrawerScreens("PC", "pc")
    object RunCommand : DrawerScreens("Запустить команду ssh", "runCommand")
    object Currencies : DrawerScreens("Криптовалюты", "currencies")
    object Wallet : DrawerScreens("Кошелек", "wallet")
    object Settings : DrawerScreens("Настройки", "settings")
}

private val screens = listOf(
    DrawerScreens.Home,
    DrawerScreens.PCStats,
    DrawerScreens.RunCommand,
    DrawerScreens.Currencies,
    DrawerScreens.Wallet,
    DrawerScreens.Settings,
)

@Composable
fun Drawer(
    modifier: Modifier = Modifier,
    onDestinationClicked: (route: String) -> Unit
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(start = 24.dp, top = 48.dp)
    ) {
        screens.forEach { screen ->
            Text(
                text = screen.title,
                style = MaterialTheme.typography.h5,
                modifier = Modifier
                    .clickable {
                        onDestinationClicked(screen.route)
                    }
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )
        }
        Pool()
        Wallet()
        Rate()
    }
}

@Preview
@Composable
fun DrawerPreview() {
    MyMinerTheme {
        Drawer {}
    }
}

