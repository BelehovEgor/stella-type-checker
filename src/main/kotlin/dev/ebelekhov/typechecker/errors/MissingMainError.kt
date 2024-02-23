package dev.ebelekhov.typechecker.errors

class MissingMainError : BaseError() {
    override fun getMessage(): String {
        return """
            ERROR_MISSING_MAIN:
                в программе отсутствует функция main
        """.trimIndent()
    }
}

