package misha.miner.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun ExposedDropDownMenu(
    text: String = "",
    suggestions: List<String>,
    onTextChanged: (String) -> Unit,
    disableListAction: Boolean = false,
    label: @Composable (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(text) }

    val icon = if (expanded)
        Icons.Filled.ArrowForward
    else Icons.Filled.ArrowDropDown

    Column {
        OutlinedTextField(
            label = label,
            value = selectedText,
            onValueChange = {
                selectedText = it
                onTextChanged(it)
            },
            trailingIcon = {
                Icon(icon, null, Modifier.clickable { expanded = !expanded })
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            suggestions.forEach { label ->
                DropdownMenuItem(onClick = {
                    if (!disableListAction) {
                        selectedText = label
                        expanded = false
                        onTextChanged(label)
                    }
                }) {
                    Text(text = label)
                }
            }
        }
    }
}