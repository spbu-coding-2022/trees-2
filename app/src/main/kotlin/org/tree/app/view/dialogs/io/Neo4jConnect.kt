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
import org.tree.app.appDataController
import org.tree.app.controller.io.HandledIOException
import org.tree.app.controller.io.Neo4jIO
import org.tree.app.view.Logger


@Composable
fun Neo4jConnect(onSuccess: (Neo4jIO) -> Unit, onFail: (Neo4jIO) -> Unit) {
    var connectionStatus by remember { mutableStateOf("Not connected") }
    var iconColor by remember { mutableStateOf(Color.Red) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            label = { Text("Url") },
            value = appDataController.data.neo4j.url,
            onValueChange = { appDataController.data.neo4j.url = it },
            singleLine = true
        )
        OutlinedTextField(
            label = { Text("Username") },
            value = appDataController.data.neo4j.login,
            onValueChange = { appDataController.data.neo4j.login = it },
            singleLine = true
        )
        OutlinedTextField(
            label = { Text("Password") },
            value = appDataController.data.neo4j.password,
            onValueChange = { appDataController.data.neo4j.password = it },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = {
                val db = Neo4jIO()
                appDataController.saveData()
                try {
                    db.open(
                        appDataController.data.neo4j.url,
                        appDataController.data.neo4j.login,
                        appDataController.data.neo4j.password
                    )
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
