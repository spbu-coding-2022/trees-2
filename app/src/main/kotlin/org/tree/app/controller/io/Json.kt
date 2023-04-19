package org.tree.app.controller.io

import kotlinx.serialization.Serializable

@Serializable
private data class JsonAVLNode(
    val key: Int,
    val value: String?,
    val x: Double,
    val y: Double,
    val height: Int,
    val lkey: Int?,
    val rkey: Int?
)

@Serializable
private data class JsonAVLTree(
    val AVLTree: Array<JsonAVLNode?>
)
class Json {
}
