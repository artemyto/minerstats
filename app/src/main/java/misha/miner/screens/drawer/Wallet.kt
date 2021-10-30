package misha.miner.screens.drawer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import misha.miner.MainViewModel

@Composable
fun Wallet(
    mainViewModel: MainViewModel = viewModel()
) {

    val context = LocalContext.current

    Text(
        text = "Кошелек",
        style = MaterialTheme.typography.h5,
        modifier = Modifier
            .clickable {
                val openUrlIntent = Intent(Intent.ACTION_VIEW)
                openUrlIntent.data =
                    Uri.parse("https://www.etherchain.org/account/${mainViewModel.getStorage().wallet}")
                ContextCompat.startActivity(context, openUrlIntent, Bundle())
            }
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    )
}