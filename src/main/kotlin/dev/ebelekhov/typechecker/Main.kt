package dev.ebelekhov.typechecker

import java.nio.file.Paths
import kotlin.io.path.readText
import kotlin.system.exitProcess

fun main() {
    // D:\itmo\2-sem-2023-2024\languages\project\stella-type-checker\src\test\resources\ok\sum_arg.st
    //val pathToTheFile = readln()
    //val typeValidator = TypeValidator(StellaVisitor())
    //
    //val result = typeValidator.accept(Paths.get(pathToTheFile).readText())
    //
    //if (result.isFailure) {
    //    println((result.exceptionOrNull() as ExitException).error.getMessage())
    //    exitProcess(1)
    //}


    debug()
}

fun debug() {
    val codeExample = """
language core;

extend with #fixpoint-combinator;


fn main(n : Nat) -> Nat {
  return fix(true);
}

    """.trimIndent()

    val typeValidator = TypeValidator(StellaVisitor())
    val result = typeValidator.accept(codeExample)

    if (result.isFailure) {
        println((result.exceptionOrNull() as ExitException).error.getMessage())
        exitProcess(1)
    }
}
