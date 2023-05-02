package org.tree.app.controller.io

import java.io.IOException

fun <T> handleIOException(onCatch: (HandledIOException) -> Unit, handledCode: () -> T): T? {
    try {
        return handledCode()
    } catch (ex: HandledIOException) {
        onCatch(ex)
        return null
    }
}

class HandledIOException : IOException {

    constructor() : super()

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)

}
