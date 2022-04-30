package misha.miner.screens.drawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import misha.miner.MainAction

@Composable
fun Wallet(
    onAction: (MainAction) -> Unit,
) {
    Text(
        text = "Кошелек",
        style = MaterialTheme.typography.h5,
        modifier = Modifier
            .clickable { onAction(MainAction.LaunchUrlWallet) }
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    )
}
