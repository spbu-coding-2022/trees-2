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
            nodes[it] = NodeExtension(mutableStateOf(0), mutableStateOf(0), getNodeCol(it))
            countWidth(it, nodesWithWidths)
            drawLeft(it.left, 0, 0, nodesWithWidths)
            drawRight(it.right, 0, 0, nodesWithWidths)
        }
    }

    private fun countWidth(node: NODE_T?, map: MutableMap<NODE_T, Pair<Int, Int>>): Pair<Int, Int>{
        var leftWidth = 0
        var rightWidth = 0
        if (node?.left != null){
            leftWidth = countWidth(node.left, map).first + countWidth(node.left, map).second + 1
        }
        if (node?.right != null){
            rightWidth = countWidth(node.right, map).first + countWidth(node.right, map).second + 1
        }
        if (node != null) map[node] = Pair(leftWidth, rightWidth)
        return Pair(leftWidth, rightWidth)
    }

    private fun drawLeft(node: NODE_T?, parentX: Int, parentY: Int, mapOfWidths: MutableMap<NODE_T, Pair<Int, Int>>) {
        var count = 0
        if (node?.right != null) {
            count = mapOfWidths[node]?.second ?: 0
        }
        val x = parentX - nodeSize - (count * nodeSize)
        val y = parentY + nodeSize
        val stateX = mutableStateOf(x)
        val stateY = mutableStateOf(y)
        if (node != null) {
            val col = getNodeCol(node)
            nodes[node] = NodeExtension(stateX, stateY, col)
        }
        if (node?.left != null) drawLeft(node.left, x, y, mapOfWidths)
        if (node?.right != null) drawRight(node.right, x, y, mapOfWidths)
    }

    private fun drawRight(node: NODE_T?, parentX: Int, parentY: Int, mapOfWidths: MutableMap<NODE_T, Pair<Int, Int>>) {
        var count = 0
        if (node?.left != null) {
            count = mapOfWidths[node]?.first ?: 0
        }
        val x = parentX + nodeSize + (count * nodeSize)
        val y = parentY + nodeSize
        val stateX = mutableStateOf(x)
        val stateY = mutableStateOf(y)
        if (node != null) {
            val col = getNodeCol(node)
            nodes[node] = NodeExtension(stateX, stateY, col)
        }
        if (node?.left != null) drawLeft(node.left, x, y, mapOfWidths)
        if (node?.right != null) drawRight(node.right, x, y, mapOfWidths)
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
