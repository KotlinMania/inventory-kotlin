// port-lint: ignore
// Smoke tests for the runtime-registered plugin inventory.
package io.github.kotlinmania.inventory

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private class Flag(val short: Char, val name: String)

private class Other(val tag: String)

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

private class CopyKey
