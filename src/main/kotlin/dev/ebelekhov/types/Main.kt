package dev.ebelekhov.types

import dev.ebelekhov.types.parsing.stellaLexer
import dev.ebelekhov.types.parsing.stellaParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

fun main() {
    val codeExample = """
        language core;

        // addition of natural numbers
        fn Nat::add(n : Nat) -> fn(Nat) -> Nat {
          return fn(m : Nat) {
            return Nat::rec(n, m, fn(i : Nat) {
              return fn(r : Nat) {
                return succ( r ); // r := r + 1
              };
            });
          };
        }

        // square, computed as a sum of odd numbers
        fn square(n : Nat) -> Nat {
          return Nat::rec(n, 0, fn(i : Nat) {
              return fn(r : Nat) {
                // r := r + (2*i + 1)
                return Nat::add(i)( Nat::add(i)( succ( r )));
              };
          });
        }

        fn main(n : Nat) -> Nat {
          return square(n);
        }
    """.trimIndent()

    val lexer = stellaLexer(CharStreams.fromString(codeExample))
    val tokens = CommonTokenStream(lexer)
    val parser = stellaParser(tokens)
    val tree = parser.program()

    println(tree.decl().size)
}