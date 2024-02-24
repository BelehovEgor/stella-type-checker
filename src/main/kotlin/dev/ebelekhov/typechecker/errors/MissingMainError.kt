package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser

class MissingMainError : BaseError() {
    override fun getMessage(): String {
        return """
            ERROR_MISSING_MAIN:
                в программе отсутствует функция main
        """.trimIndent()
    }
}

