package misha.miner.ui.common

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import misha.miner.ui.common.icons.ArrowDropUp

@SuppressLint("ModifierParameter")
@Composable
fun ExposedDropDownMenu(
    modifier: Modifier = Modifier.fillMaxWidth(),
    text: String = "",
    suggestions: List<String>,
    onTextChanged: (String) -> Unit,
    disableListAction: Boolean = false,
    label: @Composable (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(text) }

    val icon = if (expanded)
        Icons.Filled.ArrowDropUp
    else Icons.Filled.ArrowDropDown

    ConstraintLayout(
        modifier = modifier
    ) {

        val (textField, menu) = createRefs()

        OutlinedTextField(
            modifier = Modifier.constrainAs(textField) {
                linkTo(parent.top, menu.top)
                linkTo(parent.start, parent.end)
                width = Dimension.fillToConstraints
            },
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

        BoxWithConstraints(
            modifier = Modifier.constrainAs(menu) {
                linkTo(textField.bottom, parent.bottom)
                linkTo(textField.start, textField.end)
                width = Dimension.fillToConstraints
            },
        ) {
            val scope = this

            DropdownMenu(
                modifier = modifier.width(scope.maxWidth),
                expanded = expanded,
                onDismissRequest = { expanded = false },
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
}