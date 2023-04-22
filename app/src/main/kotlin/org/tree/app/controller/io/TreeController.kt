import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import org.tree.binaryTree.KVP
import org.tree.binaryTree.RBNode
import org.tree.binaryTree.templates.TemplateBSTree
import org.tree.binaryTree.templates.TemplateNode
import kotlin.math.pow

data class NodeExtension(var x: MutableState<Int>, var y: MutableState<Int>, var color: Color = Color.Gray)
class TreeController<NODE_T : TemplateNode<KVP<Int, String>, NODE_T>>(
    val tree: TemplateBSTree<KVP<Int, String>, NODE_T>,
    val nodeSize: Int = 20
) {
    val nodes = mutableMapOf<NODE_T, NodeExtension>()

    fun addNode(curNode: NODE_T, x: Int, y: Int, height: Int) {
        val stateX = mutableStateOf(x)
        val stateY = mutableStateOf(y)
        val col = if (curNode is RBNode<*>) {
            if (curNode.col == RBNode.Colour.BLACK) {
                Color.DarkGray
            } else {
                Color.Red
            }
        } else {
            Color.Gray
        }
        nodes[curNode] = NodeExtension(stateX, stateY, col)
        val deltaX = nodeSize * (2).toDouble().pow(height.toDouble()).toInt()
        curNode.left?.let {
            addNode(it, x + deltaX, y + nodeSize, height - 1)
        }
        curNode.right?.let {
            addNode(it, x - deltaX, y + nodeSize, height - 1)
        }

    }

    private fun height(curNode: NODE_T?): Int {
        return if (curNode == null) {
            0
        } else {
            Integer.max(height(curNode.left), height(curNode.right)) + 1
        }
    }

    init {
        tree.root?.let {
            addNode(it, 100, 100, height(it) - 2)
        }
    }

}
