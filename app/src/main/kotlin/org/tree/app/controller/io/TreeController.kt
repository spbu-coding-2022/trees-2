import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import org.tree.binaryTree.AVLNode
import org.tree.binaryTree.KVP
import org.tree.binaryTree.Node
import org.tree.binaryTree.RBNode
import org.tree.binaryTree.templates.TemplateBSTree
import org.tree.binaryTree.templates.TemplateNode
import kotlin.math.pow
import kotlin.random.Random

data class NodeExtension(var x: MutableState<Int>, var y: MutableState<Int>, var color: Color = Color.Gray)
class TreeController<NODE_T : TemplateNode<KVP<Int, String>, NODE_T>>(
    val tree: TemplateBSTree<KVP<Int, String>, NODE_T>,
    val nodeSize: Int = 20
) {
    val nodes = mutableMapOf<NODE_T, NodeExtension>()

    init {
        tree.root?.let {
            addNode(it, 0, 0, height(it) - 2)
        }
    }

    fun addNode(curNode: NODE_T, x: Int, y: Int, height: Int) {
        val stateX = mutableStateOf(x)
        val stateY = mutableStateOf(y)
        val col = getNodeCol(curNode)
        nodes[curNode] = NodeExtension(stateX, stateY, col)
        val deltaX = nodeSize * (2).toDouble().pow(height.toDouble()).toInt()
        curNode.left?.let {
            addNode(it, x + deltaX, y + nodeSize, height - 1)
        }
        curNode.right?.let {
            addNode(it, x - deltaX, y + nodeSize, height - 1)
        }
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
            Color.Blue
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
    return TreeController(emptyTree, 50)
}
