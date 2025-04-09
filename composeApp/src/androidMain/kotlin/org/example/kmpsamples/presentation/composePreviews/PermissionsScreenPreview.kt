package org.example.kmpsamples.presentation.composePreviews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.hotmovies.presentation.theme.CustomMaterialTheme
import org.example.kmpsamples.presentation.fillMaxSizeModifier
import org.example.kmpsamples.presentation.permissions.PermissionManagerProxy
import org.example.kmpsamples.presentation.permissions.PermissionsScreen
import org.example.kmpsamples.presentation.permissions.mock.MockPermissionManager
import org.example.kmpsamples.presentation.permissions.viewModel.PermissionViewModel
import org.koin.compose.KoinApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

@Preview
@Composable
private fun PreviewPermissionsScreen() {
    CustomMaterialTheme {
        KoinApplication(application = {
            modules(
                module {

                    viewModel { PermissionViewModel() }
                    single<PermissionManagerProxy> {
                        PermissionManagerProxy().apply {
                            setManager(MockPermissionManager())
                        }
                    }
                }
            )
        }) {
            PermissionsScreen(fillMaxSizeModifier)
        }
    }
}