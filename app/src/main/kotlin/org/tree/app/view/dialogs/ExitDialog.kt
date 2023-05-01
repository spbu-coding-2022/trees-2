package org.tree.app.view.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState

@Composable
fun ExitDialog(
    isOpen: Boolean,
    onCloseRequest: () -> Unit,
    onExitRequest: () -> Unit,
    additionalButtons: @Composable () -> Unit
) {
    if (isOpen) {
        Dialog(
            title = "Confirm exit",
            onCloseRequest = { onCloseRequest() },
            state = rememberDialogState(width = 750.dp, height = 300.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.weight(0.1f))
                Row {
                    Spacer(modifier = Modifier.weight(0.1f))
                    Text(
                        "Are you sure you want to exit?",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1.0f)
                    )
                    Spacer(modifier = Modifier.weight(0.1f))
                }

                Spacer(modifier = Modifier.weight(0.1f))

                Column {
                    Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                        additionalButtons()
                    }
                    Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { onCloseRequest() }, colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.Gray,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = { onExitRequest() }, colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xffff5b79),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Exit")
                        }
                    }
                }
            }
        }
    }
}
