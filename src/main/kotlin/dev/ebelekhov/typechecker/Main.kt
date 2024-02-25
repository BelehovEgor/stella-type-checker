package dev.ebelekhov.typechecker

import java.nio.file.Paths
import kotlin.io.path.readText

fun main() {
    // D:\itmo\2-sem-2023-2024\languages\project\stella-type-checker\src\test\resources\ok\sum_arg.st
    //val pathToTheFile = readln()
    //val typeValidator = TypeValidator(StellaVisitor())
    //typeValidator.accept(Paths.get(pathToTheFile).readText())

    debug()
}

fun debug() {
    val codeExample = """
language core;

extend with #sum-types, #unit-type;

fn test(first : Nat + Bool) -> Nat {
  return match first {
      inl(n) => n
    | inr(_) => 0
  }
}

fn main(input : Bool) -> Nat {
  return test(inl(0))
}
    """.trimIndent()

    val typeValidator = TypeValidator(StellaVisitor())
    typeValidator.accept(codeExample)
}
