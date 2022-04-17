package misha.miner.common.ui.widgets

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ErrorAlert(message: String, onClick: () -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text(text = "Error") },
        text = {
            Text(
                text = message
            )
        },

        confirmButton = {
        },

        dismissButton = {
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Blue,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Okay")
            }
        }
    )
}
