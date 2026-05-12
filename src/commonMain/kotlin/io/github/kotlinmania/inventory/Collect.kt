// port-lint: source src/lib.rs
package io.github.kotlinmania.inventory

import kotlin.reflect.KClass

/**
 * Marker interface for types that can be iterated by [iter].
 *
 * This interface cannot be implemented manually beyond inheriting from it.
 * The actual binding between a plugin type and its registry is established by
 * the [collect] call, which records the type as a collectable plugin type at
 * runtime.
 *
 * # Examples
 *
 * ```
 * fun <T : Any> countPlugins(klass: KClass<T>): Int = iter(klass).asSequence().count()
 * ```
 */
public interface Collect

/**
 * Associate a plugin registry with the specified type.
 *
 * This call must be in the same module that defines the plugin type. It does
 * not "run" anything; it simply ensures the per-type [Registry] exists. Place
 * it outside of any function body — for example, behind a top-level
 * initializer — so that it runs once when the module loads.
 *
 * # Examples
 *
 * Suppose we are writing a command line flags library and want to allow any
 * source file in the application to register command line flags that are
 * relevant to it.
 *
 * This is the flag registration style used by gflags and is better suited for
 * large scale development than maintaining a single central list of flags, as
 * the central list would become an endless source of merge conflicts.
 *
 * ```
 * class Flag(
 *     val short: Char,
 *     val name: String,
 *     /* ... */
 * )
 *
 * collect(Flag::class)
 * ```
 *
 * Refer to the package-level documentation for a complete example of
 * submitting plugins and iterating a plugin registry.
 */
public fun <T : Any> collect(klass: KClass<T>): Registry = registryFor(klass)
