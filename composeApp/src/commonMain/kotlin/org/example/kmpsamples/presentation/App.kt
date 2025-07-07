package org.example.kmpsamples.presentation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.hotmovies.presentation.theme.CustomMaterialTheme
import kmpsamples.composeapp.generated.resources.Res
import kmpsamples.composeapp.generated.resources.app_name
import kotlinx.serialization.Serializable
import org.example.kmpsamples.presentation.cryptocurrencies.CryptocurrencyScreen
import org.example.kmpsamples.presentation.cryptocurrencies.viewModel.CryptocurrencyViewModel
import org.example.kmpsamples.presentation.cryptocurrencies.viewModel.CryptocurrencyViewModel.Actions.TrackCryptocurrencies
import org.example.kmpsamples.presentation.permissions.PermissionsScreen
import org.example.kmpsamples.presentation.pickers.PickerScreen
import org.example.kmpsamples.presentation.pickers.rememberGalleryPickerManager
import org.example.kmpsamples.presentation.pickers.viewModel.GalleryPickerViewModel
import org.example.kmpsamples.presentation.transitions.ListItem
import org.example.kmpsamples.presentation.transitions.TransitionDetailScreen
import org.example.kmpsamples.presentation.transitions.TransitionListScreen
import org.example.kmpsamples.presentation.video.VideoLooperViewFactoryInterface
import org.example.kmpsamples.presentation.video.VideoScreen
import org.example.kmpsamples.presentation.video.viewModel.VideoLooperViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object Menu

@Serializable
data object Permissions

@Serializable
data object Video

@Serializable
data object CryptoCurrencies

@Serializable
data object TransitionList

@Serializable
data class TransitionDetail(val itemId: Int)

@Serializable
data object GalleryPicker

val fillMaxSizeModifier = Modifier.fillMaxSize()
val fillMaxWidthModifier = Modifier.fillMaxWidth()

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun App() {
    CustomMaterialTheme() {
        val navController = rememberNavController()
        val snackbarHostState = remember { SnackbarHostState() }
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
            SharedTransitionLayout(fillMaxSizeModifier) {
                NavHost(
                    modifier = fillMaxSizeModifier.padding(paddingValues),
                    navController = navController,
                    startDestination = Menu
                ) {
                    composable<Menu> {
                        MenuScreen(
                            fillMaxSizeModifier,
                            onPermissions = { navController.navigate(Permissions) },
                            onCryptoCurrencies = { navController.navigate(CryptoCurrencies) },
                            onVideo = { navController.navigate(Video) },
                            onTransitions = { navController.navigate(TransitionList) },
                            onGalleryPickers = { navController.navigate(GalleryPicker) })
                    }
                    composable<TransitionList> {
                        val items = remember {
                            listOf(
                                ListItem(1, "Fruits", Color.DarkGray),
                                ListItem(2, "Fruits", Color.Red),
                                ListItem(3, "Fruits", Color.DarkGray),
                                ListItem(4, "Fruits", Color.Blue),
                                ListItem(5, "Fruits", Color.Yellow),
                                ListItem(6, "Fruits", Color.Blue),
                                ListItem(7, "Vegetables", Color.Green),
                                ListItem(8, "Vegetables", Color.Cyan),
                                ListItem(9, "Vegetables", Color.Green),
                                ListItem(10, "Vegetables", Color.Cyan),
                                ListItem(11, "Dairy", Color.Magenta),
                                ListItem(12, "Dairy", Color.Magenta),
                                ListItem(13, "Dairy", Color.Red),
                                ListItem(14, "Dairy", Color.Magenta),
                                ListItem(15, "Dairy", Color.White),
                                ListItem(16, "Dairy", Color.Yellow),
                                ListItem(17, "Dairy", Color.Yellow)
                            )
                        }
                        TransitionListScreen(
                            fillMaxSizeModifier,
                            this@SharedTransitionLayout,
                            this@composable,
                            items
                        ) { itemId ->
                            navController.navigate(TransitionDetail(itemId))
                        }
                    }
                    composable<TransitionDetail> {
                        val route = it.toRoute<TransitionDetail>()
                        TransitionDetailScreen(
                            fillMaxSizeModifier,
                            this@SharedTransitionLayout,
                            this@composable,
                            route.itemId
                        )
                    }
                    composable<Permissions> {
                        PermissionsScreen(fillMaxSizeModifier)
                    }
                    composable<Video> {
                        val viewModel = viewModel { VideoLooperViewModel() }
                        val videoLooperViewFactory = koinInject<VideoLooperViewFactoryInterface>()

                        VideoScreen(
                            fillMaxSizeModifier,
                            viewModel.videos,
                            videoLooperViewFactory,
                            viewModel::doAction
                        )

                        DisposableEffect(Unit) {
                            onDispose {
                                videoLooperViewFactory.dispose()
                            }
                        }
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
                    composable<GalleryPicker> {
                        val viewModel = koinViewModel<GalleryPickerViewModel>()
                        val uiState = viewModel.state.collectAsStateWithLifecycle()

                        PickerScreen(
                            fillMaxSizeModifier,
                            uiState,
                            rememberGalleryPickerManager(),
                            snackbarHostState,
                            viewModel::sendIntent
                        )
                    }
                }
            }
        }
    }
}