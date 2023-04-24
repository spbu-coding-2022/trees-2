import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import org.tree.binaryTree.AVLNode
import org.tree.binaryTree.KVP
import org.tree.binaryTree.Node
import org.tree.binaryTree.RBNode
import org.tree.binaryTree.templates.TemplateBSTree
import org.tree.binaryTree.templates.TemplateNode
import kotlin.random.Random

data class NodeExtension(var x: MutableState<Int>, var y: MutableState<Int>, var color: Color = Color.Gray)
class TreeController<NODE_T : TemplateNode<KVP<Int, String>, NODE_T>>(
    val tree: TemplateBSTree<KVP<Int, String>, NODE_T>,
    val nodeSize: Int = 50
) {
    val nodes = mutableMapOf<NODE_T, NodeExtension>()

    init {
        tree.root?.let {
            drawLeft(it, 0, 0, height(it) - 2)
            drawRight(it, 0, 0, height(it) - 2)
        }
    }

    private fun childrenCount(node: NODE_T?): Int {
        return (2 - (node?.countNullChildren() ?: 2))
    }

    private fun drawLeft(node: NODE_T?, parentX: Int, parentY: Int, height: Int) {
        var count = 0
        if (node?.right != null) {
            count = 1 + (height)*childrenCount(node.right)
        }
        val x = parentX - nodeSize - (count*nodeSize)
        val y = parentY + nodeSize
        val stateX = mutableStateOf(x)
        val stateY = mutableStateOf(y)
        if (node != null) {
            val col = getNodeCol(node)
            nodes[node] = NodeExtension(stateX, stateY, col)
        }
        if (node?.left != null) drawLeft(node.left, x, y, height - 1)
        if (node?.right != null) drawRight(node.right, x, y, height - 1)
    }

    private fun drawRight(node: NODE_T?, parentX: Int, parentY: Int, height: Int) {
        var count = 0
        if (node?.left != null) {
            count = 1 + (height)*childrenCount(node.left)
        }
        val x = parentX + nodeSize + (count*nodeSize)
        val y = parentY + nodeSize
        val stateX = mutableStateOf(x)
        val stateY = mutableStateOf(y)
        if (node != null) {
            val col = getNodeCol(node)
            nodes[node] = NodeExtension(stateX, stateY, col)
        }
        if (node?.left != null) drawLeft(node.left, x, y, height - 1)
        if (node?.right != null) drawRight(node.right, x, y, height - 1)
    }

    fun insert(obj: KVP<Int, String>): TreeController<NODE_T> {
        var res = this
        val isChanged = tree.insert(obj)
        if (isChanged) {
            res = TreeController(tree, nodeSize)
        }
        return res
    }

    fun remove(obj: KVP<Int, String>): TreeController<NODE_T> {
        var res = this
        val isChanged = tree.remove(obj)
        if (isChanged) {
            res = TreeController(tree, nodeSize)
        }
        return res
    }

    fun find(obj: KVP<Int, String>): NodeExtension? {
        val node = tree.find(obj)
        return nodes[node]
    }

    private fun getNodeCol(curNode: NODE_T): Color {
        return if (curNode is RBNode<*>) {
            if (curNode.col == RBNode.Colour.BLACK) {
                Color.DarkGray
            } else {
                Color.Red
            }
        } else if (curNode is AVLNode<*>) {
            Color.Cyan
        } else if (curNode is Node<*>) {
            Color.Yellow
        } else {
            Color.Gray
        }
    }

    private fun height(curNode: NODE_T?): Int {
        return if (curNode == null) {
            0
        } else {
            Integer.max(height(curNode.left), height(curNode.right)) + 1
        }
    }


}

fun <NODE_T : TemplateNode<KVP<Int, String>, NODE_T>, TREE_T : TemplateBSTree<KVP<Int, String>, NODE_T>> newTree(
    emptyTree: TREE_T
): TreeController<NODE_T> {
    val rand = Random(0x1337)
    for (i in 0..10) {
        emptyTree.insert(KVP(rand.nextInt(100), "Num: $i"))
    }
    return TreeController(emptyTree)
}
