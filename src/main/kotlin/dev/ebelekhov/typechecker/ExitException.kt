package dev.ebelekhov.typechecker

import dev.ebelekhov.typechecker.errors.BaseError

class ExitException(val error: BaseError) : Exception()