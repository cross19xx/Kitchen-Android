package com.cross19xx.filmnest.ui.genre

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.cross19xx.filmnest.R
import com.cross19xx.filmnest.components.ErrorDisplay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreDetailsScreen(
    uiState: GenreDetailsUiState,
    onViewMovieDetails: (movieId: Int) -> Unit,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        topBar = {
            if (uiState is GenreDetailsUiState.Success) {
                CenterAlignedTopAppBar(
                    title = { Text(uiState.genre.name) },
                    navigationIcon = {
                        IconButton(onClick = onBackPressed) {
                            Icon(
                                painter = painterResource(R.drawable.ic_arrow_back),
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        when (uiState) {
            is GenreDetailsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is GenreDetailsUiState.Error -> {
                ErrorDisplay(modifier = Modifier.padding(innerPadding))
            }

            is GenreDetailsUiState.Success -> {
                val haptic = LocalHapticFeedback.current

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    LazyVerticalGrid(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp),
                        columns = GridCells.Fixed(2)
                    ) {
                        items(
                            items = uiState.movies,
                            key = { it.id },
                            contentType = { "movies_by_${uiState.genre.name}" }
                        ) { movie ->
                            Box(
                                modifier = Modifier
                                    .aspectRatio(2f / 3f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onViewMovieDetails(movie.id)
                                    }
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(movie.posterUrl)
                                        .crossfade(true)
                                        .build(),
                                    placeholder = painterResource(R.drawable.placeholder_background),
                                    contentDescription = movie.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
