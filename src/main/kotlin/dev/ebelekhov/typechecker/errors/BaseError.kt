package dev.ebelekhov.typechecker.errors

import dev.ebelekhov.typechecker.antlr.parser.stellaParser

abstract class BaseError {
    abstract fun getMessage(parser: stellaParser): String;
}
