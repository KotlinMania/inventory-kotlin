// port-lint: source lib.rs
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
// Private uninhabited type corresponding to upstream `void_iter::Void`.
internal enum class Void

public class Iter<T : Any>
    @PublishedApi
    internal constructor(
        private var node: Node?,
        @PublishedApi
        internal val extract: (Any) -> T,
    ) : Iterator<T> {
        public operator fun iterator(): Iterator<T> = this

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
         * Converts this collection into an iterator, corresponding to upstream `IntoIterator::into_iter`.
         */
        public fun intoIter(): Iter<T> = this

        /**
         * Duplicates this iterator at its current position, corresponding to upstream `Clone::clone`.
         */
        public fun clone(): Iter<T> = Iter(node, extract)

        /**
         * Returns this iterator, corresponding to upstream `Deref::deref`.
         */
        public fun deref(): Iter<T> = this

        /**
         * Returns an iterator that resumes from the same position. The new iterator
         * visits the same remaining nodes as this one, independently.
         */
        public fun copy(): Iter<T> = clone()
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

/**
 * Creates an iterator over all registered plugins of type [T], corresponding to upstream `into_iter`.
 */
public inline fun <reified T : Any> intoIter(): Iter<T> = iter<T>()
