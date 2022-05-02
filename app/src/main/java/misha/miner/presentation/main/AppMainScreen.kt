package misha.miner.presentation.main

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import misha.miner.common.ui.theme.MyMinerTheme
import misha.miner.common.util.closeWithScope
import misha.miner.presentation.currencies.Currencies
import misha.miner.presentation.drawer.Drawer
import misha.miner.presentation.drawer.DrawerScreens
import misha.miner.presentation.home.Home
import misha.miner.presentation.pc.PCStats
import misha.miner.presentation.scanip.ScanIp
import misha.miner.presentation.settings.Settings
import misha.miner.presentation.ssh.RunCommand
import misha.miner.presentation.wallet.Wallet

@Composable
fun AppMainScreen(
    mainViewModel: MainViewModel = viewModel(),
) {
    val context = LocalContext.current

    val navController = rememberNavController()
    Surface(color = MaterialTheme.colors.background) {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        ModalDrawer(
            drawerState = drawerState,
            gesturesEnabled = true,
            drawerContent = {
                Drawer(
                    onDestinationClicked = { route ->
                        drawerState.closeWithScope(scope)
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
                    Home(drawerState)
                }
                composable(DrawerScreens.PCStats.route) {
                    PCStats(drawerState)
                }
                composable(DrawerScreens.RunCommand.route) {
                    RunCommand(drawerState)
                }
                composable(DrawerScreens.Currencies.route) {
                    Currencies(drawerState)
                }
                composable(DrawerScreens.Settings.route) {
                    Settings(drawerState)
                }
                composable(DrawerScreens.Wallet.route) {
                    Wallet(drawerState)
                }
                composable(DrawerScreens.ScanIp.route) {
                    ScanIp(drawerState)
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
