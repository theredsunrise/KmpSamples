package org.example.kmpsamples.presentation

import androidx.compose.runtime.Stable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Stable
data class TimestampState<T : Any>(val timestamp: Long, val entity: T) {
    companion object {
        fun <T : Any> createOrNull(entity: T?): TimestampState<T>? {
            entity ?: return null
            return create(entity)
        }

        @OptIn(ExperimentalTime::class)
        fun <T : Any> create(entity: T): TimestampState<T> {
            return TimestampState(
                Clock.System.now().toEpochMilliseconds(),
                entity
            )
        }
    }
}