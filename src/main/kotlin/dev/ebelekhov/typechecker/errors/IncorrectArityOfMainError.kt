package dev.ebelekhov.typechecker.errors

class IncorrectArityOfMainError(val n: Int) : BaseError() {
    override fun getMessage(): String {
        return """
            ERROR_INCORRECT_ARITY_OF_MAIN:
                функция main объявлена с $n параметрами
                а должна быть с 1
       """.trimIndent()
    }
}