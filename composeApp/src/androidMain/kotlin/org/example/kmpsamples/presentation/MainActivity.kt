package org.example.kmpsamples.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.example.kmpsamples.presentation.permissions.AndroidPermissionManager
import org.example.kmpsamples.presentation.permissions.PermissionManagerProxy
import org.example.kmpsamples.presentation.permissions.SystemPermissionMapper
import org.example.kmpsamples.presentation.deepLinks.IntentHandler
import org.koin.core.context.GlobalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("**** Created main activity")
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.data?.also { uri ->
            IntentHandler.emitData(uri.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        println("**** Destroyed main activity")
    }
}