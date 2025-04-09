package org.example.kmpsamples.infrastructure.network

import io.ktor.client.engine.cio.CIO

actual fun engine() = CIO.create()