<img src=app/src/main/resources/icon.png alt="logo" width="100" align="right">

# Trees-2

Program for binary tree visualization.

---
![gui-example](https://user-images.githubusercontent.com/66139162/235777850-84e8d881-cbc0-429d-a74e-b9b3cbf388fe.png)

## Features

You can:

- **Create** 3 different types of search trees: **AVL**, **RedBlack** and **Common binary tree**
- **Edit** trees: **insert** nodes, **move** nodes and **remove** them
- **Save** it with 3 different ways: **Neo4j**, **Json** and **SQLite**
- Or **load** your own tree from these "databases"
- Use our [library](#Library) to **implement** binary search trees in your own project

---

## Get started

### Requirements

- Java JDK version 11 or later

### Build

To build and run this application locally, you'll need Git and JDK installed on your computer. From your command
line:

```bash
# Clone this repository
git clone https://github.com/spbu-coding-2022/trees-2.git trees-2

# Go into the repository
cd trees-2

# Build
./gradlew assemble      
# use `./gradlew build` if you want to test app

# Run the app
./gradlew run
```

### Neo4j database

Since to save or download from neo4j, the application needs to connect to an instance of this database.
We will tell you how it can be started quickly.

- download
  archive [\[linux/mac\]](https://neo4j.com/download-thanks/?edition=community&release=5.6.0&flavour=unix) [\[windows\]](https://neo4j.com/download-thanks/?edition=community&release=5.6.0&flavour=winzip)
- extract files from it to a dir `~/neo4j-dir/`
- run `~/neo4j-dir/bin/neo4j console`
- now you can visit http://localhost:7474 in your web browser.
- connect using the username 'neo4j' with default password 'neo4j'. You'll then be prompted to change the password.
- to save/load tree enter this url `bolt://localhost:7687` with correct login and password to app

If you got trouble during installation or for more
information visit https://neo4j.com/docs/operations-manual/current/installation/

---

## Databases format specifications

### Plain text

At plain text database we store AVLTree. We use json files. There are 2 types of objects with the following struct:

- `Tree`
  - `root` *AVLNode*
- `AVLNode`
  - `key` *int*
  - `value` *string*
  - `height` *int* - node height
  - `x` *int* - node position at ui
  - `y` *int* - node position at ui
  - `left` *AVLNode* - node left child
  - `right` *AVLNode* - node right child

We have methods:

- `exportTree(TreeController, file.json)` - writes information stored in TreeController object to a file `file.json`.
- `importTree(file.json)` - reads the tree stored in the file `file.json` and returns TreeController object.


### SQLite

At SQLite database we store BinSearchTree. There are 2 types of objects with the following struct:

- `Tree`
  - `root` *Node*
- `Node`
  - `key` *int*
  - `parentKey` *int*
  - `value` *string*
  - `x` *int* - node position at ui
  - `y` *int* - node position at ui

We have functions:

- `exportTree(TreeController, file)` - writes information stored in TreeController object  to a `file` in preorder order. 
- `importTree(file)` - reads the tree stored in the `file` and returns TreeController object. **Nodes must be stored in preorder order (check root -> then check left child -> then check right child).**
If there are some extra tree nodes, that can't fit in the tree, then `importTree` will throw `HandledIOException`. 


### Neo4j

At neo4j database we store **RBTree**. There are 2 types of labels with the following struct:

- `Tree`
  - `name` *string*
- `RBNode`
  - `key` *int*
  - `value` *string*
  - `isBlack` *boolean* - node colour
  - `x` *int* - node position at ui
  - `y` *int* - node position at ui

Also, there are 3 types of links:

- `ROOT`: from **tree**(`Tree`) to its **root**(`RBNode`)
- `LEFT_CHILD`: from **parent**(`RBNode`) to its **left-child**(`RBNode`)
- `RIGHT_CHILD`: from **parent**(`RBNode`) to its **right-child**(`RBNode`)

Using the `Tree` labels, we can store several trees in the database at once. But their nodes should not intersect
(shouldn't have links between each other).
![neo4j-example](https://user-images.githubusercontent.com/66139162/233449145-15476b7d-d1c9-4dfa-b4a6-bd500d3a25d4.png)

---

## Library

[Our library](binaryTree) gives you the opportunity to work with already written
trees (`BinSearchTree`, `AVLTree`, `RBTree`), and write your own based on written templates.

### Gradle

Since our library is a separate gradle module, it is enough to import it:

- copy the module folder to your gradle project
- specify it in the dependencies

```kotlin
dependencies {
  implementation(project(":binaryTree"))
}
```

### Examples

#### Trees

```kotlin
fun example() {
  // create RBTree with int elements
  val intTree = RBTree<Int>()

  // create AVLTree with string elements
  val stringTree = AVLTree<String>()

  // create common BinSearchTree with int keys and string values
  val keyValuePairTree = BinSearchTree<KVP<Int, String>>()

  intTree.insert(3)                  // insert new node
  intTree.remove(3)                  // remove node if it exists
  val foundNode = intTree.find(3)    // find node

  for (i in 0..10) {
    stringTree.insert(i.toString())
  }
  val traverseList = stringTree.traversal(TemplateNode.Traversal.INORDER)
  // get list of nodes' elements at INORDER traverse
}
```

#### Implement your tree

```kotlin
class CoolNode<T : Comparable<T>>(v: T, var coolness: Double) : TemplateNode<T, CoolNode<T>>(v) {
  fun sayYourCoolness() {
    println("My coolness equal to $coolness cats")
  }
}

class MyCoolTree<T : Comparable<T>> : TemplateBSTree<T, CoolNode<T>>() {
  override fun insert(curNode: CoolNode<T>?, obj: T): CoolNode<T>? {
    val newNode = CoolNode(obj, 0.0)
    val parentNode = insertNode(curNode, newNode)
    parentNode?.let {
      newNode.coolness = it.coolness * 1.3
    }
    return parentNode
  }
}
```

#### Implement your balance tree

```kotlin
class MyCoolTree<T : Comparable<T>> : TemplateBalanceBSTree<T, CoolNode<T>>() {
  override fun insert(curNode: CoolNode<T>?, obj: T): CoolNode<T>? {
    return insertNode(curNode, CoolNode(obj, 0.0))
  }

  override fun balance(
    curNode: CoolNode<T>?,
    changedChild: BalanceCase.ChangedChild,
    operationType: BalanceCase.OpType,
    recursive: BalanceCase.Recursive
  ) {
    when (changedChild) {
      BalanceCase.ChangedChild.ROOT -> println("root was changed")
      BalanceCase.ChangedChild.LEFT -> println("left child of curNode was changed")
      BalanceCase.ChangedChild.RIGHT -> println("right child of curNode was changed")
    }

    when (operationType) {
      BalanceCase.OpType.REMOVE_0 -> println("removed node with 0 null children")
      BalanceCase.OpType.REMOVE_1 -> println("removed node with 1 null child")
      BalanceCase.OpType.REMOVE_2 -> println("removed node with 2 null children")
      BalanceCase.OpType.INSERT -> println("inserted new node")
    }

    when (recursive) {
      BalanceCase.Recursive.RECURSIVE_CALL -> println("just returning from traverse")
      BalanceCase.Recursive.END -> println("this was last iteration, so it can change something")
    }

    if (curNode != null) {
      curNode.left?.let {
        rotateLeft(it, curNode)
        // do left rotate on the curNode.left with curNode as parent
      }
    }
  }
}

```

#### More examples

For more examples you can watch code of our [app](app/src/main/kotlin/org/tree/app)
or [library](binaryTree/src/main/kotlin/org/tree/binaryTree).

---

## Contributing

**Quick start**:

1. Create a branch with new feature from `develop` branch (`git checkout -b feat/my-feature develop`)
2. Commit the changes (`git commit -m "feat: Add some awesome feature"`)
3. Push the branch to origin (`git push origin feat/add-amazing-feature`)
4. Open the pull request

For more details, see [CONTRIBUTING.md](CONTRIBUTING.md)

---

## License

This project is licensed under the terms of the **MIT** license. See the [LICENSE](LICENSE.md) for more information.
