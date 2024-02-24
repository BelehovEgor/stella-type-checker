package dev.ebelekhov.typechecker

import dev.ebelekhov.typechecker.errors.BaseError

class ExitException(error: BaseError) : Exception(error.getMessage()) {
}