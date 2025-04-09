package org.example.kmpsamples.shared

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import org.example.kmpsamples.shared.ResultState.Failure
import org.example.kmpsamples.shared.ResultState.None
import org.example.kmpsamples.shared.ResultState.Progress
import org.example.kmpsamples.shared.ResultState.Success

@OptIn(ExperimentalStdlibApi::class)
suspend fun checkIfNotMainThread() {
    check(currentCoroutineContext()[CoroutineDispatcher.Key] != Dispatchers.Main)
}

fun Exception.safeMessage() = this.message ?: this::class.simpleName ?: this::class.toString()

fun <T> Flow<T>.asStateResult(): Flow<ResultState<T>> {
    return this.map<T, ResultState<T>> { it.state() }
        .onStart { emit(progress) }
        .catch { e ->
            if (e !is Exception) throw e
            emit(e.stateFailure())
        }
}

fun <I, O> ResultState<I>.transform(transform: (I) -> O): ResultState<O> {
    return when (this) {
        is None -> none
        is Progress -> progress
        is Failure -> Failure(this.exception)
        is Success -> Success(transform(this.value))
    }
}

fun millisToLocalDate(epochMillis: Long): LocalDateTime {
    val instant = Instant.fromEpochMilliseconds(epochMillis)
    return instant.toLocalDateTime(TimeZone.currentSystemDefault())
}

@OptIn(FormatStringsInDatetimeFormats::class)
fun LocalDateTime.format(): String {
    val dateTimeFormat = LocalDateTime.Format {
        byUnicodePattern("HH:mm:ss[.SSS]")
    }
    return this.format(dateTimeFormat)
}