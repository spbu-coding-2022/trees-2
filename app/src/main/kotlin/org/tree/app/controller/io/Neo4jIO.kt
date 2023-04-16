package org.tree.app.controller.io

import org.neo4j.driver.Driver
import java.io.Closeable

class Neo4jIO() : Closeable {
    private var driver: Driver? = null
    override fun close() {
        driver?.close()
    }
}
