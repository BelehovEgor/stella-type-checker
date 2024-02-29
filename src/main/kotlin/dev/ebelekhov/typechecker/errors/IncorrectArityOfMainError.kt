package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser

class IncorrectArityOfMainError(val n: Int) : BaseError() {
    override fun getMessage(parser: stellaParser): String {
        return """
            ERROR_INCORRECT_ARITY_OF_MAIN:
                для функции main
                ожидается 1 параметр
                объявлено $n
       """.trimIndent()
    }
}