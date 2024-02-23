package dev.ebelekhov.typechecker

fun main() {
    val codeExample = """
        language core;

        fn main(n : Nat) -> Nat {
          return n;
        }
    """.trimIndent()

    val parser = Parser()
    val typeChecker = TypeChecker()
    parser.parse(codeExample).accept(typeChecker)
}