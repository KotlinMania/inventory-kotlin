# inventory-kotlin in Kotlin

[![GitHub link](https://img.shields.io/badge/GitHub-KotlinMania%2Finventory--kotlin-blue.svg)](https://github.com/KotlinMania/inventory-kotlin)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kotlinmania/inventory-kotlin)](https://central.sonatype.com/artifact/io.github.kotlinmania/inventory-kotlin)
[![Build status](https://img.shields.io/github/actions/workflow/status/KotlinMania/inventory-kotlin/ci.yml?branch=main)](https://github.com/KotlinMania/inventory-kotlin/actions)

This is a Kotlin Multiplatform line-by-line transliteration port of [`dtolnay/inventory`](https://github.com/dtolnay/inventory).

**Original Project:** This port is based on [`dtolnay/inventory`](https://github.com/dtolnay/inventory). All design credit and project intent belong to the upstream authors; this repository is a faithful port to Kotlin Multiplatform with no behavioural changes intended.

### Porting status

This is an **in-progress port**. The goal is feature parity with the upstream Rust crate while providing a native Kotlin Multiplatform API. Every Kotlin file carries a `// port-lint: source <path>` header naming its upstream Rust counterpart so the AST-distance tool can track provenance.

---

## Upstream README — `dtolnay/inventory`

> The text below is reproduced and lightly edited from [`https://github.com/dtolnay/inventory`](https://github.com/dtolnay/inventory). It is the upstream project's own description and remains under the upstream authors' authorship; links have been rewritten to absolute upstream URLs so they continue to resolve from this repository.

## Typed distributed plugin registration

[<img alt="github" src="https://img.shields.io/badge/github-dtolnay/inventory-8da0cb?style=for-the-badge&labelColor=555555&logo=github" height="20">](https://github.com/dtolnay/inventory)
[<img alt="crates.io" src="https://img.shields.io/crates/v/inventory.svg?style=for-the-badge&color=fc8d62&logo=rust" height="20">](https://crates.io/crates/inventory)
[<img alt="docs.rs" src="https://img.shields.io/badge/docs.rs-inventory-66c2a5?style=for-the-badge&labelColor=555555&logo=docs.rs" height="20">](https://docs.rs/inventory)
[<img alt="build status" src="https://img.shields.io/github/actions/workflow/status/dtolnay/inventory/ci.yml?branch=master&style=for-the-badge" height="20">](https://github.com/dtolnay/inventory/actions?query=branch%3Amaster)

This crate provides a way to set up a plugin registry into which plugins can be
registered from any source file linked into your application. There does not
need to be a central list of all the plugins.

```toml
[dependencies]
inventory = "0.3"
```

<br>

## Examples

Suppose we are writing a command line flags library and want to allow any source
file in the application to register command line flags that are relevant to it.

This is the flag registration style used by [gflags] and is better suited for
large scale development than maintaining a single central list of flags, as the
central list would become an endless source of merge conflicts in an application
developed simultaneously by thousands of developers.

[gflags]: https://gflags.github.io/gflags/

### Instantiating the plugin registry

Let's use a `struct Flag` as the plugin type, which will contain the short name
of the flag like `-v`, the full name like `--verbose`, and maybe other
information like argument type and help text. We instantiate a plugin registry
with an invocation of `inventory::collect!`.

```rust
pub struct Flag {
    short: char,
    name: &'static str,
    /* ... */
}

impl Flag {
    pub const fn new(short: char, name: &'static str) -> Self {
        Flag { short, name }
    }
}

inventory::collect!(Flag);
```

This `collect!` call must be in the same crate that defines the plugin type.
This macro does not "run" anything so place it outside of any function body.

### Registering plugins

Now any crate with access to the `Flag` type can register flags as a plugin.
Plugins can be registered by the same crate that declares the plugin type, or by
any downstream crate.

```rust
inventory::submit! {
    Flag::new('v', "verbose")
}
```

The `submit!` macro does not "run" anything so place it outside of any function
body. In particular, note that all `submit!` invocations across all source files
linked into your application all take effect simultaneously. A `submit!`
invocation is not a statement that needs to be called from `main` in order to
execute.

### Iterating over plugins

The value `inventory::iter::<T>` is an iterator with element type `&'static T`
that iterates over all plugins registered of type `T`.

```rust
for flag in inventory::iter::<Flag> {
    println!("-{}, --{}", flag.short, flag.name);
}
```

There is no guarantee about the order that plugins of the same type are visited
by the iterator. They may be visited in any order.

<br>

## How it works

Inventory is built on runtime initialization functions similar to
`__attribute__((constructor))` in C, and similar to the [`ctor`] crate. Each
call to `inventory::submit!` produces a shim that evaluates the given
expression and registers it into a registry of its corresponding type. This
registration happens dynamically as part of life-before-main for statically
linked elements. Elements brought in by a dynamically loaded library are
registered at the time that dlopen occurs.

[`ctor`]: https://github.com/mmastrac/rust-ctor

Platform support includes Linux, macOS, iOS, FreeBSD, Android, Windows,
WebAssembly, and a few others. Beyond this, other platforms will simply find
that no plugins have been registered.

For a different approach to plugin registration that *does not* involve
life-before-main, see the [`linkme`] crate.

[`linkme`]: https://github.com/dtolnay/linkme

<br>

#### License

<sup>
Licensed under either of <a href="LICENSE-APACHE">Apache License, Version
2.0</a> or <a href="LICENSE-MIT">MIT license</a> at your option.
</sup>

<br>

<sub>
Unless you explicitly state otherwise, any contribution intentionally submitted
for inclusion in this crate by you, as defined in the Apache-2.0 license, shall
be dual licensed as above, without any additional terms or conditions.
</sub>

---

## About this Kotlin port

### Installation

```kotlin
dependencies {
    implementation("io.github.kotlinmania:inventory-kotlin:0.1.0-SNAPSHOT")
}
```

### Building

```bash
./gradlew build
./gradlew test
```

### Targets

- macOS arm64
- Linux x64
- Windows mingw-x64
- iOS arm64 / simulator-arm64 (Swift export + XCFramework)
- JS (browser + Node.js)
- Wasm-JS (browser + Node.js)
- Android (API 24+)

### Porting guidelines

See [AGENTS.md](AGENTS.md) and [CLAUDE.md](CLAUDE.md) for translator discipline, port-lint header convention, and Rust → Kotlin idiom mapping.

### License

This Kotlin port is distributed under the same MIT license as the upstream [`dtolnay/inventory`](https://github.com/dtolnay/inventory). See [LICENSE](LICENSE) (and any sibling `LICENSE-*` / `NOTICE` files mirrored from upstream) for the full text.

Original work copyrighted by the inventory authors.  
Kotlin port: Copyright (c) 2026 Sydney Renee and The Solace Project.

### Acknowledgments

Thanks to the [`dtolnay/inventory`](https://github.com/dtolnay/inventory) maintainers and contributors for the original Rust implementation. This port reproduces their work in Kotlin Multiplatform; bug reports about upstream design or behavior should go to the upstream repository.
