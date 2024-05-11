//ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION
language core;

extend with #type-reconstruction, #pairs;

fn main(f : Nat) -> { Nat, auto } {
  return { f, f(0) }
}