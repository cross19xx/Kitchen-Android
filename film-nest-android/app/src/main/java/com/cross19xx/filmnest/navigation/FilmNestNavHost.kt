package com.cross19xx.filmnest.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cross19xx.filmnest.ui.details.MediaDetailsScreen
import com.cross19xx.filmnest.ui.details.MediaDetailsViewModel
import com.cross19xx.filmnest.ui.genre.GenreDetailsScreen
import com.cross19xx.filmnest.ui.genre.GenreDetailsViewModel
import com.cross19xx.filmnest.ui.home.HomeScreen
import com.cross19xx.filmnest.ui.home.HomeViewModel
import com.cross19xx.filmnest.ui.settings.SettingsScreen
import com.cross19xx.filmnest.viewmodel.ThemeViewModel

private const val MOTION_DURATION = 220
private const val ENTER_DELAY = 60
private const val INITIAL_SCALE = 0.985f
private const val FINAL_SCALE = 1.015f


@Composable
fun FilmNestNavHost(
    navController: NavHostController,
    themeViewModel: ThemeViewModel
) {
    NavHost(
        navController,
        startDestination = Home,
        enterTransition = { enterTransition() },
        exitTransition = { exitTransition() },
        popEnterTransition = { popEnterTransition() },
        popExitTransition = { popExitTransition() }
    ) {

        fun handleBackPressed() {
            navController.popBackStack()
        }

        composable<Home> {
            val viewModel: HomeViewModel = hiltViewModel()

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

            HomeScreen(
                events = viewModel.events,
                isRefreshing = isRefreshing,
                uiState = uiState,
                onRefresh = { viewModel.refresh() },
                onViewMediaDetails = { mediaId, mediaType ->
                    navController.navigate(MediaDetails(mediaId, mediaType))
                },
                onViewGenreDetails = { genreId ->
                    navController.navigate(GenreDetails(genreId))
                },
                onViewSettings = { navController.navigate(Settings) },
            )
        }

        composable<MediaDetails> {
            val viewModel: MediaDetailsViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            MediaDetailsScreen(
                uiState = uiState,
                onBackPressed = { handleBackPressed() }
            )
        }

        composable<GenreDetails> {
            val viewModel: GenreDetailsViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            GenreDetailsScreen(
                uiState = uiState,
                onBackPressed = { handleBackPressed() },
                onViewMediaDetails = { mediaId, mediaType ->
                    navController.navigate(MediaDetails(mediaId, mediaType))
                }
            )
        }

        composable<Settings> {
            val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()
            SettingsScreen(
                themeMode = themeMode,
                onBackPressed = { handleBackPressed() },
                onThemeModeChanged = { themeViewModel.setThemeMode(it) },
            )
        }
    }
}

// Define page transitions similar to what Google Pixel 8+ uses

private fun enterTransition() =
    fadeIn(tween(MOTION_DURATION, delayMillis = ENTER_DELAY)) +
            scaleIn(
                initialScale = INITIAL_SCALE,
                animationSpec = tween(MOTION_DURATION, delayMillis = ENTER_DELAY)
            )

private fun exitTransition() =
    fadeOut(tween(MOTION_DURATION)) +
            scaleOut(targetScale = FINAL_SCALE, animationSpec = tween(MOTION_DURATION))

private fun popEnterTransition() =
    fadeIn(tween(MOTION_DURATION, delayMillis = ENTER_DELAY)) +
            scaleIn(
                initialScale = FINAL_SCALE,
                animationSpec = tween(MOTION_DURATION, delayMillis = ENTER_DELAY)
            )

private fun popExitTransition() =
    fadeOut(tween(MOTION_DURATION)) +
            scaleOut(targetScale = INITIAL_SCALE, animationSpec = tween(MOTION_DURATION))