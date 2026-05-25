package com.cross19xx.filmnest.core.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.cross19xx.filmnest.ui.details.MovieDetailsScreen
import com.cross19xx.filmnest.ui.home.HomeScreen
import com.cross19xx.filmnest.ui.settings.SettingsScreen

private const val MOTION_DURATION = 220
private const val ENTER_DELAY = 60
private const val INITIAL_SCALE = 0.985f
private const val FINAL_SCALE = 1.015f


@Composable
fun FilmNestNavHost(navController: NavHostController) {
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
            HomeScreen(
                onViewSettings = { navController.navigate(route = Settings) },
                onViewMovieDetails = { movieId ->
                    navController.navigate(MovieDetails(movieId))
                })
        }

        composable<MovieDetails> { backStackEntry ->
            val route = backStackEntry.toRoute<MovieDetails>()
            MovieDetailsScreen(
                movieId = route.movieId, onBackPressed = { handleBackPressed() })
        }

        composable<Settings> {
            SettingsScreen(onBackPressed = { handleBackPressed() })
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