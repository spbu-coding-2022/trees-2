package org.tree.app.view.dialogs.io

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import org.tree.app.controller.io.HandledIOException
import org.tree.app.controller.io.Neo4jIO
import org.tree.app.view.Logger


@Composable
fun Neo4jConnect(onSuccess: (Neo4jIO) -> Unit, onFail: (Neo4jIO) -> Unit) {
    var urlString by remember { mutableStateOf("bolt://localhost:7687") }
    var loginString by remember { mutableStateOf("neo4j") }
    var passwordString by remember { mutableStateOf("qwertyui") }
    var connectionStatus by remember { mutableStateOf("Not connected") }
    var iconColor by remember { mutableStateOf(Color.Red) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(value = urlString, onValueChange = { urlString = it })
        OutlinedTextField(value = loginString, onValueChange = { loginString = it })
        OutlinedTextField(
            value = passwordString,
            onValueChange = { passwordString = it },
            visualTransformation = PasswordVisualTransformation()
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = {
                val db = Neo4jIO()
                try {
                    db.open(urlString, loginString, passwordString)
                } catch (ex: HandledIOException) {
                    connectionStatus = ex.toString()
                    iconColor = Color.Yellow
                    onFail(db)
                    return@Button
                }
                iconColor = Color.Green
                connectionStatus = "Connected"
                onSuccess(db)
            }) {
                Text("Connect")
            }
            Logger(connectionStatus, iconColor)
        }
    }
}
