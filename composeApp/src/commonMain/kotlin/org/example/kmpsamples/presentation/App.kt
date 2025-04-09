package org.example.kmpsamples.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.hotmovies.presentation.theme.CustomMaterialTheme
import kmpsamples.composeapp.generated.resources.Res
import kmpsamples.composeapp.generated.resources.app_name
import kotlinx.serialization.Serializable
import org.example.kmpsamples.presentation.cryptocurrencies.CryptocurrencyScreen
import org.example.kmpsamples.presentation.cryptocurrencies.viewModel.CryptocurrencyViewModel
import org.example.kmpsamples.presentation.cryptocurrencies.viewModel.CryptocurrencyViewModel.Actions.TrackCryptocurrencies
import org.example.kmpsamples.presentation.permissions.PermissionsScreen
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object Menu

@Serializable
data object Permissions

@Serializable
data object CryptoCurrencies

val fillMaxSizeModifier = Modifier.fillMaxSize()
val fillMaxWidthModifier = Modifier.fillMaxWidth()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    CustomMaterialTheme() {
        val navController = rememberNavController()
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            stringResource(Res.string.app_name),
                        )
                    },
                    navigationIcon = {
                        val state by navController.currentBackStackEntryAsState()
                        if (state != null && navController.previousBackStackEntry != null) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Localized description"
                                )
                            }
                        }
                    },
                )
            },
            modifier = fillMaxSizeModifier
        ) { paddingValues ->
            NavHost(
                modifier = fillMaxSizeModifier.padding(paddingValues),
                navController = navController,
                startDestination = Menu
            ) {
                composable<Menu> {
                    MenuScreen(
                        fillMaxSizeModifier,
                        onPermissions = { navController.navigate(Permissions) },
                        onCryptoCurrencies = { navController.navigate(CryptoCurrencies) })
                }
                composable<Permissions> {
                    PermissionsScreen(fillMaxSizeModifier)
                }
                composable<CryptoCurrencies> {
                    val viewModel = koinViewModel<CryptocurrencyViewModel>()
                    val uiState = viewModel.state.collectAsStateWithLifecycle()

                    viewModel.onStartEmittingUiState = { recentAction ->
                        viewModel.doAction(recentAction)
                    }

                    CryptocurrencyScreen(fillMaxSizeModifier, uiState) {
                        viewModel.doAction(TrackCryptocurrencies(listOf("solusdt", "btcusdt")))
                    }
                }
            }
        }
    }
}