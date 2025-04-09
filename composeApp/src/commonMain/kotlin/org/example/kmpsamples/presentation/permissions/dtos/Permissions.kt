package org.example.kmpsamples.presentation.permissions.dtos

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@OptIn(ExperimentalObjCName::class)
@Stable
@Immutable
@ObjCName(swiftName = "Permission")
sealed class Permission {
    abstract val rationaleMessage: String
    abstract val deniedMessage: String

    @OptIn(ExperimentalObjCName::class)
    @Stable
    @Immutable
    @ObjCName(swiftName = "ContactsReadPermission")
    class ContactsReadPermission() : Permission() {
        override val rationaleMessage: String =
            "This app needs read access to your contacts so that you can send messages of your friends."
        override val deniedMessage: String =
            "It seems you denied the app access to read your contacts."
    }

    @Stable
    @Immutable
    @ObjCName(swiftName = "ContactsWritePermission")
    class ContactsWritePermission() : Permission() {
        override val rationaleMessage: String =
            "This app needs write access to your contacts to add your friends."
        override val deniedMessage: String =
            "It seems you denied the app access to write to your contacts."
    }

    @Stable
    @Immutable
    @ObjCName(swiftName = "CalendarReadPermission")
    class CalendarReadPermission() : Permission() {
        override val rationaleMessage: String =
            "This app needs read access to your calendar so that you can be notified about your friends' birthdays."
        override val deniedMessage: String =
            "It seems you denied the app access to read your calendar."
    }

    @Stable
    @Immutable
    @ObjCName(swiftName = "CalendarWritePermission")
    class CalendarWritePermission() : Permission() {
        override val rationaleMessage: String =
            "This app needs write access to your calendar to add your friends' birthdays."
        override val deniedMessage: String =
            "It seems you denied the app access to write to your calendar."
    }

    @Stable
    @Immutable
    @ObjCName(swiftName = "RecordAudioPermission")
    class RecordAudioPermission() : Permission() {
        override val rationaleMessage: String =
            "This app requires to record calls of your friends."
        override val deniedMessage: String =
            "It seems you denied the app access to record the audio."
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Permission

        if (rationaleMessage != other.rationaleMessage) return false
        if (deniedMessage != other.deniedMessage) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rationaleMessage.hashCode()
        result = 31 * result + deniedMessage.hashCode()
        return result
    }
}
