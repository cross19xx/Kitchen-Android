package com.cross19xx.filmnest.ui.details

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.cross19xx.filmnest.R
import com.cross19xx.filmnest.components.ErrorDisplay
import com.cross19xx.filmnest.data.model.MovieDetail
import com.cross19xx.filmnest.utils.DateUtils


@SuppressLint("DefaultLocale")
@Composable
fun MovieDetailsScreen(
    uiState: MovieDetailsUiState,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        contentWindowInsets = if (uiState is MovieDetailsUiState.Success) {
            ScaffoldDefaults.contentWindowInsets.exclude(WindowInsets.statusBars)
        } else {
            ScaffoldDefaults.contentWindowInsets
        }
    ) { innerPadding ->
        when (uiState) {
            is MovieDetailsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is MovieDetailsUiState.Error -> {
                ErrorDisplay()
            }

            is MovieDetailsUiState.Success -> {
                val movie = uiState.movie
                val releaseDate = DateUtils.formatDate(
                    input = movie.releaseDate,
                    outputFormat = "EEEE, MMMM dd, yyyy"
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    MovieBanner(movie, onBackPressed = onBackPressed)

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = movie.title,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            lineHeight = 28.sp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Description row (might explore using a grid later)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                MovieDescription(
                                    label = stringResource(R.string.rating),
                                    value = String.format("%.1f", movie.voteAverage)
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                MovieDescription(
                                    label = stringResource(R.string.runtime),
                                    value = "${movie.runtime} min",
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                MovieDescription(
                                    label = "Status",
                                    value = movie.status,
                                )
                            }
                        }

                        // Overview
                        Text(
                            text = movie.overview,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        // Footnote
                        if (!releaseDate.isNullOrBlank()) {
                            Column {
                                Text(
                                    text = stringResource(R.string.release_date),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onBackground

                                )
                                Text(releaseDate)
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun MovieBanner(movie: MovieDetail, onBackPressed: () -> Unit) {
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    val backdropUrl = movie.backdropUrl ?: movie.posterUrl

    val backdropHeight = 320.dp
    val offset = 32.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(backdropHeight + offset)
    ) {
        // Backdrop container with clipping
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(backdropHeight)
                .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
        ) {
            if (!backdropUrl.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(backdropUrl)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.placeholder_background),
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(20.dp)
                )
            }
        }

        // Poster image floating over the bottom edge
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(196.dp)
                .aspectRatio(2f / 3f)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(8.dp)
                )
                .background(
                    Color.White.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(4.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(movie.posterUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.placeholder_background),
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
            )
        }

        OutlinedIconButton(
            onClick = onBackPressed,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 16.dp, y = topPadding + 8.dp),
            colors = IconButtonDefaults.outlinedIconButtonColors(
                containerColor = Color.White.copy(alpha = 0.5f),
                contentColor = Color.Black.copy(alpha = 0.8f)
            )

        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = stringResource(R.string.back),
            )
        }
    }
}

@Composable
fun MovieDescription(label: String, value: String) {
    Text(
        text = value,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onBackground
    )

    Text(
        text = label,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
