package org.tree.app.view

import org.tree.binaryTree.KVP
import org.tree.binaryTree.templates.TemplateNode

class NodeView<NODE_T : TemplateNode<KVP<Int, String>, NODE_T>>(nd: NODE_T) { // it is just sketch for import/export
    var node: NODE_T = nd
    var x: Double = 0.0
    var y: Double = 0.0
    var l: NodeView<NODE_T>? = null
    var r: NodeView<NODE_T>? = null

    init {
        node.left?.let {
            l = NodeView(it)
        }
        node.right?.let {
            r = NodeView(it)
        }
    }
}
