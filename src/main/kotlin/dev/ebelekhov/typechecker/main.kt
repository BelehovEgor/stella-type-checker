package dev.ebelekhov.typechecker

import dev.ebelekhov.typechecker.antlr.parser.stellaLexer
import dev.ebelekhov.typechecker.antlr.parser.stellaParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.util.*
import kotlin.system.exitProcess

fun main() {
    debug()

    //run()
}

fun run() {
    val code = readCode()

    val parser = getParser(code)
    val typeValidator = TypeValidator(parser)
    val result = typeValidator.accept()

    if (result.isFailure) {
        println((result.exceptionOrNull() as ExitException).error.getMessage(parser))
        exitProcess(1)
    }
}

fun readCode() : String {
    val code = StringBuilder()
    val scanner = Scanner(System.`in`)
    scanner.useDelimiter(System.lineSeparator())
    while (scanner.hasNext()) {
        code.appendLine(scanner.next())
    }

    scanner.close()
    return code.toString()
}

fun getParser(code: String) : stellaParser {
    val lexer = stellaLexer(CharStreams.fromString(code))
    val tokens = CommonTokenStream(lexer)

    return stellaParser(tokens)
}

fun debug() {
    val codeExample = """
language core;

extend with #try-cast-as, #structural-patterns, #natural-literals;

fn main(n : Nat) -> Nat {
  return try { true } cast as Nat
    { 1 => 12 }
    with
    { 0 }
}

    """.trimIndent()

    val parser = getParser(codeExample)
    val typeValidator = TypeValidator(parser)
    val result = typeValidator.accept()

    if (result.isFailure) {
        println((result.exceptionOrNull() as ExitException).error.getMessage(parser))
        exitProcess(1)
    }
}
