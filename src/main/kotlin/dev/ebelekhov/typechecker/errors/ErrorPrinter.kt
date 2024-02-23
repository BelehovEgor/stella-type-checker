package dev.ebelekhov.typechecker.errors

class ErrorPrinter {
    fun print(error: BaseError) {
        System.err.println(error.getMessage())
    }
}