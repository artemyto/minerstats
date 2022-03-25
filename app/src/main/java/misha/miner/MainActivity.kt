package misha.miner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import misha.miner.screens.currencies.Currencies
import misha.miner.screens.currencies.CurrenciesViewModel
import misha.miner.screens.drawer.Drawer
import misha.miner.screens.drawer.DrawerScreens
import misha.miner.screens.home.Home
import misha.miner.screens.home.HomeViewModel
import misha.miner.screens.pc.PCStats
import misha.miner.screens.pc.PCStatsViewModel
import misha.miner.screens.settings.Settings
import misha.miner.screens.settings.SettingsViewModel
import misha.miner.screens.ssh.RunCommand
import misha.miner.screens.ssh.RunCommandViewModel
import misha.miner.screens.wallet.Wallet
import misha.miner.screens.wallet.WalletViewModel
import misha.miner.common.ui.theme.MyMinerTheme
import misha.miner.screens.scanip.ScanIp
import misha.miner.screens.scanip.ScanIpViewModel

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
    homeViewModel: HomeViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel(),
    runCommandViewModel: RunCommandViewModel = viewModel(),
    pcStatsViewModel: PCStatsViewModel = viewModel(),
    currenciesViewModel: CurrenciesViewModel = viewModel(),
    walletViewModel: WalletViewModel = viewModel(),
    scanIpViewModel: ScanIpViewModel = viewModel(),
) {
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
                    }
                )
            }
        ) {
            NavHost(
                navController = navController,
                startDestination = DrawerScreens.Home.route
            ) {
                composable(DrawerScreens.Home.route) {
                    Home(
                        viewModel = homeViewModel,
                        openDrawer
                    )
                }
                composable(DrawerScreens.PCStats.route) {
                    PCStats(
                        viewModel = pcStatsViewModel,
                        openDrawer
                    )
                }
                composable(DrawerScreens.RunCommand.route) {
                    RunCommand(
                        viewModel = runCommandViewModel,
                        openDrawer
                    )
                }
                composable(DrawerScreens.Currencies.route) {
                    Currencies(
                        viewModel = currenciesViewModel,
                        openDrawer
                    )
                }
                composable(DrawerScreens.Settings.route) {
                    Settings(
                        viewModel = settingsViewModel,
                        openDrawer
                    )
                }
                composable(DrawerScreens.Wallet.route) {
                    Wallet(
                        viewModel = walletViewModel,
                        openDrawer
                    )
                }
                composable(DrawerScreens.ScanIp.route) {
                    ScanIp(
                        viewModel = scanIpViewModel,
                        openDrawer
                    )
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