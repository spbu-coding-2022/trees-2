package org.tree.app.view.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState


@Composable
fun AlertDialog(description: String, isOpen: Boolean, onCloseRequest: () -> Unit) {
    if (isOpen) {
        Dialog(state = rememberDialogState(width = 500.dp, height = 250.dp),
            title = "Something go wrong...",
            onCloseRequest = {
                onCloseRequest()
            }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.weight(0.1f))
                Row {
                    Spacer(modifier = Modifier.weight(0.1f))
                    Text(description, modifier = Modifier.weight(1.0f))
                    Spacer(modifier = Modifier.weight(0.1f))
                }

                Spacer(modifier = Modifier.weight(0.1f))
                Button(onClick = { onCloseRequest() }) {
                    Text("Ok")
                }
            }
        }
    }
}
