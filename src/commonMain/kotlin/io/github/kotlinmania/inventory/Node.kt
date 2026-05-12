// port-lint: source src/lib.rs
package io.github.kotlinmania.inventory

import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.AtomicReference

// Not public API. Used by generated code.
public class Node internal constructor(
    public val value: ErasedNode,
) {
    public val next: AtomicReference<Node?> = AtomicReference(null)
    internal val initialized: AtomicBoolean = AtomicBoolean(false)
}
