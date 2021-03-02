package de.ancash.pets.utils.datastructure.tuples;

@FunctionalInterface
public interface TriFunction<A, B, C, R> {
    R apply(A a, B b, C c);
}
