package org.tree.app.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InsertRow(onClick: (keyString: String, value: String) -> Unit) {
    var keyString by remember { mutableStateOf("") }
    var valueString by remember { mutableStateOf("") }

    fun execute() {
        onClick(keyString, valueString)
        keyString = ""
        valueString = ""
    }

    Row(verticalAlignment = Alignment.CenterVertically) {

        Button(
            onClick = {
                execute()
            },
            modifier = Modifier.weight(0.3f).defaultMinSize(minWidth = 100.dp)
        ) {
            Text("Insert")
        }
        OutlinedTextField(
            label = { Text("Key") },
            value = keyString,
            onValueChange = { keyString = it },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White.copy(alpha = 0.3f)),
            modifier = Modifier.weight(0.35f).onKeyEvent {
                if ((it.key == Key.Enter) && (it.type == KeyEventType.KeyDown)) {
                    execute()
                    return@onKeyEvent true
                }
                return@onKeyEvent false
            }
        )
        OutlinedTextField(
            label = { Text("Value") },
            value = valueString,
            onValueChange = { valueString = it },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White.copy(alpha = 0.3f)),
            modifier = Modifier.weight(0.35f).onKeyEvent {
                if ((it.key == Key.Enter) && (it.type == KeyEventType.KeyDown)) {
                    execute()
                    return@onKeyEvent true
                }
                return@onKeyEvent false
            }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RemoveRow(onClick: (key: String) -> Unit) {
    var keyString by remember { mutableStateOf("") }

    fun execute() {
        onClick(keyString)
        keyString = ""
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(
            onClick = {
                execute()
            },
            modifier = Modifier.weight(0.3f)
        ) {
            Text("Remove")
        }

        OutlinedTextField(
            label = { Text("Key") },
            value = keyString,
            onValueChange = { keyString = it },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White.copy(alpha = 0.3f)),
            modifier = Modifier.weight(0.7f).onKeyEvent {
                if ((it.key == Key.Enter) && (it.type == KeyEventType.KeyDown)) {
                    execute()
                    return@onKeyEvent true
                }
                return@onKeyEvent false
            }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FindRow(onClick: (key: String) -> (Unit)) {
    var keyString by remember { mutableStateOf("") }

    fun execute() {
        onClick(keyString)
        keyString = ""
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = ::execute, modifier = Modifier.weight(0.3f)) {
            Text("Find")
        }
        OutlinedTextField(
            label = { Text("Key") },
            value = keyString,
            onValueChange = { keyString = it },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White.copy(alpha = 0.3f)),
            modifier = Modifier.weight(0.7f).onKeyEvent {
                if ((it.key == Key.Enter) && (it.type == KeyEventType.KeyDown)) {
                    execute()
                    return@onKeyEvent true
                }
                return@onKeyEvent false
            }
        )
    }
}
