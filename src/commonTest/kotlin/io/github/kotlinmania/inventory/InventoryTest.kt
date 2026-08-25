// port-lint: tests tests/test.rs
// Upstream tests/test.rs (test_iter) verifies std::mem::size_of and align_of on the Rust ZST ghost type.
// Upstream tests/compiletest.rs executes trybuild compile-fail tests against Rust macro invocations.
package io.github.kotlinmania.inventory

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private class Flag(
    val short: Char,
    val name: String,
)

private class Other(
    val tag: String,
)

private class UnusedPlugin(
    val id: Int,
)

private class CopyKey

class InventoryTest {
    @Test
    fun submitAndIterateRoundTrip() {
        collect(Flag::class)
        submit(Flag('v', "verbose"))
        submit(Flag('q', "quiet"))

        val names = iter<Flag>().asSequence().map { it.name }.toSet()
        assertTrue("verbose" in names)
        assertTrue("quiet" in names)
    }

    @Test
    fun separateTypesHaveSeparateRegistries() {
        submit(Other("alpha"))
        val flagNames = iter<Flag>().asSequence().map { it.name }.toList()
        val otherTags = iter<Other>().asSequence().map { it.tag }.toList()
        assertTrue("alpha" in otherTags)
        assertEquals(0, flagNames.count { it == "alpha" })
    }

    @Test
    fun emptyRegistryIteration() {
        val iterator = iter<UnusedPlugin>()
        assertFalse(iterator.hasNext())
        assertFailsWith<NoSuchElementException> {
            iterator.next()
        }
    }

    @Test
    fun copyResumesIndependently() {
        val key = CopyKey()
        submit(key)
        submit(CopyKey())
        val a = iter<CopyKey>()
        val b = a.copy()
        val countA = a.asSequence().count()
        val countB = b.asSequence().count()
        assertEquals(countA, countB)
    }
}
