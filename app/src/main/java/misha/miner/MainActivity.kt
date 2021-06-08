package misha.miner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import misha.miner.home.Home
import misha.miner.home.HomeViewModel
import misha.miner.settings.Settings
import misha.miner.settings.SettingsViewModel
import misha.miner.ui.theme.MyMinerTheme

class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyMinerTheme {
                AppMainScreen(homeViewModel, settingsViewModel)
            }
        }
    }
}


@Composable
fun AppMainScreen(homeViewModel: HomeViewModel, settingsViewModel: SettingsViewModel) {
    val navController = rememberNavController()
    Surface(color = MaterialTheme.colors.background) {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
//        val openDrawer = {
//            scope.launch {
//                drawerState.open()
//            }
//        }
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
                            popUpTo = navController.graph.startDestinationId
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
                        homeViewModel = homeViewModel
                    )
                }
                composable(DrawerScreens.Settings.route) {
                    Settings(
                        settingsViewModel = settingsViewModel
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
        val homeViewModel = HomeViewModel()
        val settingsViewModel = SettingsViewModel()
        AppMainScreen(homeViewModel, settingsViewModel)
    }
}