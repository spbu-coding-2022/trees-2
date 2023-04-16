package org.tree.app.controller.io

import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.exceptions.SessionExpiredException
import java.io.Closeable
import java.io.IOException

class Neo4jIO() : Closeable {
    private var driver: Driver? = null

    fun open(uri: String, username: String, password: String) {
        try {
            driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password))
        } catch (ex: IllegalArgumentException) {
            throw IOException("Wrong URI", ex)
        } catch (ex: SessionExpiredException) {
            throw IOException("Session failed, try restarting the app", ex)
        }
    }

    override fun close() {
        driver?.close()
    }
}
