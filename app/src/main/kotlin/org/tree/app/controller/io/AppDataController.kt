package org.tree.app.controller.io

import TreeController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import newTree
import org.tree.app.appDataController
import org.tree.binaryTree.trees.BinSearchTree
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files

@Serializable(with = Neo4jDataSerializer::class)
class Neo4jData(
    urlValue: String = "bolt://localhost:7687",
    loginValue: String = "neo4j",
    passwordValue: String = "qwertyui"
) {
    var url by mutableStateOf(urlValue)
    var login by mutableStateOf(loginValue)
    var password by mutableStateOf(passwordValue)
}


object Neo4jDataSerializer : KSerializer<Neo4jData> {
    @Serializable
    class SurrogateNeo4jData(val url: String, val login: String, val password: String)

    override val descriptor: SerialDescriptor = SurrogateNeo4jData.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Neo4jData) {
        val surrogate = SurrogateNeo4jData(value.url, value.login, value.password)
        encoder.encodeSerializableValue(SurrogateNeo4jData.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Neo4jData {
        val surrogate = decoder.decodeSerializableValue(SurrogateNeo4jData.serializer())
        return Neo4jData(surrogate.url, surrogate.login, surrogate.password)
    }
}

enum class SavedType {
    SQLite,
    Json,
    Neo4j
}

@Serializable
class SavedTree(
    val type: SavedType,
    val path: String
)

@Serializable
class AppData {
    var neo4j = Neo4jData()
    var lastTree: SavedTree? = null
}

class AppDataController {
    val file = getAppDataFile()
    var data: AppData

    init {
        data = loadData()
        saveData()
    }

    fun loadData(): AppData {
        val appData = try {
            val fileContent = file.readText()
            Json.decodeFromString(fileContent)
        } catch (ex: Exception) {
            when (ex) {
                is FileNotFoundException, is IllegalArgumentException -> {
                    AppData()
                }

                else -> throw ex
            }
        }
        return appData
    }

    fun saveData(appData: AppData = data) {
        try {
            file.writeText(Json.encodeToString(appData))
        } catch (ex: SecurityException) {
            // If we can't create file, we wouldn't create it.
            // Because it is bad to throw warning or exception at the start of app
            // throw HandledIOException("Directory ${file.toPath()} cannot be created: no access", ex)
        }
    }

    fun loadLastTree(): TreeController<*> {
        var treeController: TreeController<*> = newTree(BinSearchTree())
        data.lastTree?.let {
            when (it.type) {
                SavedType.SQLite -> {
                    val db = SQLiteIO()
                    treeController = db.importTree(File(it.path))
                }

                SavedType.Json -> {
                    val db = Json()
                    treeController = db.importTree(File(it.path))
                }

                SavedType.Neo4j -> {
                    val db = Neo4jIO()
                    db.open(
                        appDataController.data.neo4j.url,
                        appDataController.data.neo4j.login,
                        appDataController.data.neo4j.password
                    )
                    treeController = db.importRBTree(it.path)
                    db.close()
                }
            }
        }
        return treeController
    }

    private fun getAppDataFile(): File {
        val workFile = File(getWorkingDirPath() + "appData.json")
        try {
            Files.createDirectories(workFile.toPath().parent)
            workFile.createNewFile()
        } catch (ex: SecurityException) {
            // If we can't create directory, we wouldn't create it.
            // Because it is bad to throw warning or exception at the start of app
            // throw HandledIOException("Directory ${workFile.toPath().parent} cannot be created: no access", ex)
        }
        return workFile
    }

    private fun getWorkingDirPath(): String {
        val os = (System.getProperty("os.name")).uppercase()
        var workingDirectory: String
        if (os.contains("WIN")) {
            workingDirectory = System.getenv("AppData")
            workingDirectory += "\\trees2\\"
        } else {
            workingDirectory = System.getProperty("user.home")
            workingDirectory += "/.trees2/"
        }
        return workingDirectory
    }
}
