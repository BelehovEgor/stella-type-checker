language core;

extend with #universal-types, #references;

generic fn do[T](x : &T) -> T {
	return *x
}

fn main(x : Nat) -> Nat {
	return do[Nat](new(x))
}