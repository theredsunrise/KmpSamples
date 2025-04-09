package org.example.kmpsamples.infrastructure.network

import io.ktor.client.engine.darwin.Darwin

actual fun engine() = Darwin.create()