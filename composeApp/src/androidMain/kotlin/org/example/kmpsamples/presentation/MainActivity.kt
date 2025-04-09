package org.example.kmpsamples.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.example.kmpsamples.presentation.permissions.AndroidPermissionManager
import org.example.kmpsamples.presentation.permissions.PermissionManagerProxy
import org.example.kmpsamples.presentation.permissions.SystemPermissionMapper
import org.koin.core.context.GlobalContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val handler = AndroidPermissionManager(SystemPermissionMapper)
        this.lifecycle.addObserver(handler)
        GlobalContext.get().get<PermissionManagerProxy>().apply {
            setManager(
                handler
            )
        }
        setContent {
            App()
        }
    }
}