// port-lint: source src/lib.rs
package io.github.kotlinmania.inventory

/**
 * An iterator over plugins registered of a given type.
 *
 * The function [iter] returns an iterator with element type `T`.
 *
 * There is no guarantee about the order that plugins of the same type are
 * visited by the iterator. They may be visited in any order.
 *
 * # Examples
 *
 * ```
 * for (flag in iter<Flag>()) {
 *     println("-${flag.short}, --${flag.name}")
 * }
 * ```
 *
 * Refer to the package-level documentation for a complete example of
 * instantiating a plugin registry and submitting plugins.
 */
public class Iter<T : Any> @PublishedApi internal constructor(
    private var node: Node?,
    @PublishedApi internal val extract: (Any) -> T,
) : Iterator<T> {

    override fun hasNext(): Boolean = node != null

    override fun next(): T {
        val current = node ?: throw NoSuchElementException()
        node = current.next.load()
        val payload = current.value
        if (payload !is CollectNode) {
            throw IllegalStateException("inventory: corrupt node")
        }
        return extract(payload.unwrapped)
    }

    /**
     * Returns an iterator that resumes from the same position. Equivalent to
     * `impl Clone for Iter<T>` upstream: the new iterator visits the same
     * remaining nodes as this one, independently.
     */
    public fun copy(): Iter<T> = Iter(node, extract)
}

/**
 * Returns an iterator over all plugins registered for the given type.
 *
 * Equivalent to the upstream value `inventory::iter::<T>` together with its
 * `IntoIterator` impl: calling this function is the same as taking that ghost
 * value and asking it for an iterator.
 */
public inline fun <reified T : Any> iter(): Iter<T> {
    val head = registryFor(T::class).head.load()
    return Iter(head) { value ->
        if (value is T) {
            value
        } else {
            throw IllegalStateException("inventory: registry contained an incompatible value")
        }
    }
}
