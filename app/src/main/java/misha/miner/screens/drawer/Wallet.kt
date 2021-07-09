package misha.miner.screens.drawer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import misha.miner.services.storage.StorageManager

@Composable
fun Wallet() {

    val context = LocalContext.current

    Text(
        text = "Кошелек",
        style = MaterialTheme.typography.h5,
        modifier = Modifier
            .clickable {
                val openUrlIntent = Intent(Intent.ACTION_VIEW)
                openUrlIntent.data =
                    Uri.parse("https://www.etherchain.org/account/${StorageManager.getStorage().wallet}")
                ContextCompat.startActivity(context, openUrlIntent, Bundle())
            }
            .fillMaxWidth()
    )
}