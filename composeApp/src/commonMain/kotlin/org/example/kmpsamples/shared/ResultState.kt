package org.example.kmpsamples.shared

import androidx.compose.runtime.Stable

@Stable
sealed interface ResultState<out T> {
    @Stable
    data object None : ResultState<Nothing>

    @Stable
    data object Progress : ResultState<Nothing>

    @Stable
    data class Success<T>(val value: T) : ResultState<T>

    @Stable
    data class Failure(val exception: Exception) : ResultState<Nothing>

    fun isNone(): Boolean = when (this) {
        is None -> true
        else -> false
    }

    fun isSuccess(): Boolean = when (this) {
        is Success -> true
        else -> false
    }

    fun isProgress(): Boolean = when (this) {
        is Progress -> true
        else -> false
    }

    fun isFailure(): Boolean = success() != null

    fun success(): T? {
        return when (this) {
            is Success -> this.value
            else -> null
        }
    }

    fun failure(): Exception? {
        return when (this) {
            is Failure -> this.exception
            else -> null
        }
    }
}

fun <T> T.state(): ResultState.Success<T> = ResultState.Success(this)
fun Exception.stateFailure() = ResultState.Failure(this)
typealias none = ResultState.None
typealias progress = ResultState.Progress
