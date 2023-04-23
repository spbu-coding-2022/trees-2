package org.tree.app.controller.io

import java.io.IOException


class HandledIOException : IOException {

    constructor() : super()

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)

}
