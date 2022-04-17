package misha.miner.common.ui.widgets

import android.annotation.SuppressLint
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import misha.miner.common.ui.icons.ArrowDropUp

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("ModifierParameter")
@Composable
fun ExposedDropDownMenu(
    modifier: Modifier = Modifier,
    text: String = "",
    suggestions: List<String>,
    onTextChanged: (String) -> Unit,
    disableListAction: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(text) }

    val icon = if (expanded)
        Icons.Filled.ArrowDropUp
    else Icons.Filled.ArrowDropDown

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            readOnly = readOnly,
            value = selectedText,
            onValueChange = {
                selectedText = it
                onTextChanged(it)
            },
            label = label,
            trailingIcon = {
                Icon(icon, null)
            },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
        ) {
            suggestions.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        if (!disableListAction) {
                            selectedText = selectionOption
                            expanded = false
                            onTextChanged(selectionOption)
                        }
                    }
                ) {
                    Text(text = selectionOption)
                }
            }
        }
    }
}
