package org.tree.binaryTree

class KVP<K : Comparable<K>, V>(val key: K, var v: V? = null) : Comparable<KVP<K, V>> {
    override fun compareTo(other: KVP<K, V>): Int {
        return key.compareTo(other.key)
    }
}
