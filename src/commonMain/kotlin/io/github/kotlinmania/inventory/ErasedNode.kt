// port-lint: source src/lib.rs
package io.github.kotlinmania.inventory

import kotlin.reflect.KClass

// Not public API. Used by generated code.
public interface ErasedNode {
    // Requires that the underlying value's type matches the registry this
    // node will join.
    public fun submit(node: Node)
}

internal class CollectNode(
    val unwrapped: Any,
    private val klass: KClass<*>,
) : ErasedNode {
    override fun submit(node: Node) {
        registryFor(klass).submit(node)
    }
}
