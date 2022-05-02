package misha.miner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import misha.miner.common.ui.theme.MyMinerTheme
import misha.miner.presentation.currencies.Currencies
import misha.miner.presentation.drawer.Drawer
import misha.miner.presentation.drawer.DrawerScreens
import misha.miner.presentation.home.Home
import misha.miner.presentation.pc.PCStats
import misha.miner.presentation.scanip.ScanIp
import misha.miner.presentation.settings.Settings
import misha.miner.presentation.ssh.RunCommand
import misha.miner.presentation.wallet.Wallet

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyMinerTheme {
                AppMainScreen()
            }
        }
    }
}

@Composable
fun AppMainScreen(
    mainViewModel: MainViewModel = viewModel(),
) {
    val context = LocalContext.current

    val navController = rememberNavController()
    Surface(color = MaterialTheme.colors.background) {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val openDrawer = {
            scope.launch {
                drawerState.open()
            }
        }
        ModalDrawer(
            drawerState = drawerState,
            gesturesEnabled = true,
            drawerContent = {
                Drawer(
                    onDestinationClicked = { route ->
                        scope.launch {
                            drawerState.close()
                        }
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    onAction = { action -> mainViewModel.onContextAction(context, action) }
                )
            }
        ) {
            NavHost(
                navController = navController,
                startDestination = DrawerScreens.Home.route
            ) {
                composable(DrawerScreens.Home.route) {
                    Home(openDrawer)
                }
                composable(DrawerScreens.PCStats.route) {
                    PCStats(openDrawer)
                }
                composable(DrawerScreens.RunCommand.route) {
                    RunCommand(openDrawer)
                }
                composable(DrawerScreens.Currencies.route) {
                    Currencies(openDrawer)
                }
                composable(DrawerScreens.Settings.route) {
                    Settings(openDrawer)
                }
                composable(DrawerScreens.Wallet.route) {
                    Wallet(openDrawer)
                }
                composable(DrawerScreens.ScanIp.route) {
                    ScanIp(openDrawer)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyMinerTheme {
        AppMainScreen()
    }
}
