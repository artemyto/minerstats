package misha.miner.common.util

import androidx.compose.material.DrawerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun DrawerState.openWithScope(scope: CoroutineScope) {
    val state = this
    scope.launch { state.open() }
}

fun DrawerState.closeWithScope(scope: CoroutineScope) {
    val state = this
    scope.launch { state.close() }
}
