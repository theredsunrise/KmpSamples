package org.example.kmpsamples.presentation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.datetime.Clock

@Stable
@Immutable
data class TimestampState<T : Any>(val timestamp: Long, val entity: T) {
    companion object {
        fun <T : Any> createOrNull(entity: T?): TimestampState<T>? {
            entity ?: return null
            return create(entity)
        }

        fun <T : Any> create(entity: T): TimestampState<T> {
            return TimestampState(
                Clock.System.now().toEpochMilliseconds(),
                entity
            )
        }
    }
}