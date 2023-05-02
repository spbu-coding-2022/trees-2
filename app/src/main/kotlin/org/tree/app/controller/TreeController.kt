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
        val nodesWithWidths = mutableMapOf<NODE_T, Pair<Int, Int>>()
        tree.root?.let {
            countWidth(it, nodesWithWidths)
            getCoordinatesOfNode(it, 0, 0, nodesWithWidths)
        }
    }

    private fun countWidth(node: NODE_T?, map: MutableMap<NODE_T, Pair<Int, Int>>): Int {
        var leftWidth = 0
        var rightWidth = 0
        if (node?.left != null) {
            leftWidth = countWidth(node.left, map) + 1
        }
        if (node?.right != null) {
            rightWidth = countWidth(node.right, map) + 1
        }
        if (node != null) map[node] = Pair(leftWidth, rightWidth)
        return (leftWidth + rightWidth)
    }

    private fun getCoordinatesOfNode(node: NODE_T, x: Int, y: Int, mapOfWidths: MutableMap<NODE_T, Pair<Int, Int>>) {
        val stateX = mutableStateOf(x)
        val stateY = mutableStateOf(y)
        val col = getNodeCol(node)
        nodes[node] = NodeExtension(stateX, stateY, col)

        node.left?.let {
            val count = mapOfWidths[it]?.second ?: 0
            getCoordinatesOfNode(it, x - nodeSize - (count * nodeSize), y + nodeSize, mapOfWidths)
        }

        node.right?.let {
            val count = mapOfWidths[it]?.first ?: 0
            getCoordinatesOfNode(it, x + nodeSize + (count * nodeSize), y + nodeSize, mapOfWidths)
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

    fun find(obj: KVP<Int, String>): NODE_T? {
        return tree.find(obj)
    }

    fun getNodeCol(curNode: NODE_T): Color {
        return if (curNode is RBNode<*>) {
            if (curNode.color == RBNode.Color.BLACK) {
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

    fun nodeType(): NODE_T? {
        return tree.root
    }

}

fun <NODE_T : TemplateNode<KVP<Int, String>, NODE_T>, TREE_T : TemplateBSTree<KVP<Int, String>, NODE_T>> newTree(
    emptyTree: TREE_T,
    nodesCount: Int = 10
): TreeController<NODE_T> {
    val rand = Random(0x1337)
    for (i in 1..nodesCount) {
        emptyTree.insert(KVP(rand.nextInt(100), "Num: $i"))
    }
    return TreeController(emptyTree)
}
