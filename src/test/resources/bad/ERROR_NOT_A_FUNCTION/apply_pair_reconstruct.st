// ERROR_UNEXPECTED_TYPE_FOR_EXPRESSION
language core;

extend with #type-reconstruction, #pairs;

fn main(n : Nat) -> Nat {
  return {0, false}(0)
}