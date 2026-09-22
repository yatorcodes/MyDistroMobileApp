// File: build.gradle.kts (Project: MyDistro) — top-level file

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    // ^ 2.48, not 2.51.1 — Hilt 2.51.x requires Kotlin 1.9.20+.
    //   Your project pins Kotlin 1.9.0 exactly, so use Hilt 2.48,
    //   the last version confirmed compatible with Kotlin 1.9.0.

    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false
    // ^ matches your kotlin = "1.9.0" exactly — this is the pairing that matters
}