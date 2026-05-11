// port-lint: source src/lib.rs
package io.github.kotlinmania.inventory

import kotlin.reflect.KClass

/**
 * Enter an element into the plugin registry corresponding to its type.
 *
 * This call may be in the same module that defines the type, or downstream in
 * any module that depends on that module.
 *
 * Place [submit] invocations outside of any function body. All [submit]
 * invocations across all source files linked into your application take
 * effect at module load time. A [submit] invocation is not a statement that
 * needs to be called from `main` in order to execute.
 *
 * # Examples
 *
 * Put [submit] invocations outside of any function body, behind a top-level
 * initializer:
 *
 * ```
 * private val registerVerbose = submit(Flag(short = 'v', name = "verbose"))
 * ```
 *
 * Do not invoke [submit] conditionally from inside a function body when you
 * want it to take effect at startup; it does not do what you want.
 *
 * Refer to the package-level documentation for a complete example of
 * instantiating and iterating a plugin registry.
 */
public inline fun <reified T : Any> submit(value: T) {
    submit(value, T::class)
}

@PublishedApi
internal fun <T : Any> submit(value: T, klass: KClass<T>) {
    val erased = CollectNode(value, klass)
    val node = Node(erased)
    erased.submit(node)
}
