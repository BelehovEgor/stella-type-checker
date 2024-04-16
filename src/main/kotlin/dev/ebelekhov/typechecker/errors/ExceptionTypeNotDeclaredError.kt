package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser

class ExceptionTypeNotDeclaredError : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_EXCEPTION_TYPE_NOT_DECLARED
                cannot throw exceptions, because exception type is not declared
        """.trimIndent()
    }
}