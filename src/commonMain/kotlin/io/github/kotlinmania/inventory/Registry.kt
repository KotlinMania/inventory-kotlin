// port-lint: source src/lib.rs
package io.github.kotlinmania.inventory

import kotlin.concurrent.atomics.AtomicReference

// Not public API. Used by generated code.
public class Registry @PublishedApi internal constructor() {
    @PublishedApi
    internal val head: AtomicReference<Node?> = AtomicReference(null)

    // The `value` is thread-safe, and `next` is only mutated during submit,
    // which is prior to any reads.
    internal fun submit(new: Node) {
        // The WebAssembly linker uses an unreliable heuristic to determine
        // whether a module is a "command-style" linkage, for which it will
        // insert a call to the constructor entry at the top of every exported
        // function. It expects that the embedder will call into such modules
        // only once per instantiation. If this heuristic goes wrong, we can
        // end up having our constructors invoked multiple times, which
        // without this safeguard would lead to our registry's linked list
        // becoming circular. This check is harmless on non-Wasm platforms, so
        // we apply it uniformly.
        if (new.initialized.exchange(true)) {
            return
        }

        var head = this.head.load()
        while (true) {
            new.next.store(head)
            val prev = this.head.compareAndExchange(head, new)
            if (prev === head) {
                return
            }
            head = prev
        }
    }
}
