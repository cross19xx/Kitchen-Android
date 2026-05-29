package com.cross19xx.filmnest.ui.details

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
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
import com.cross19xx.filmnest.data.model.MediaItemDetail
import com.cross19xx.filmnest.utils.DateUtils


@SuppressLint("DefaultLocale")
@Composable
fun MediaDetailsScreen(
    uiState: MediaDetailsUiState,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        contentWindowInsets = if (uiState is MediaDetailsUiState.Success) {
            ScaffoldDefaults.contentWindowInsets.exclude(WindowInsets.statusBars)
        } else {
            ScaffoldDefaults.contentWindowInsets
        }
    ) { innerPadding ->
        when (uiState) {
            is MediaDetailsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is MediaDetailsUiState.Error -> {
                ErrorDisplay(modifier = Modifier.padding(innerPadding))
            }

            is MediaDetailsUiState.Success -> {
                val scrollState = rememberScrollState()
                val media = uiState.media
                val releaseDate = DateUtils.formatDate(
                    input = media.releaseDate,
                    outputFormat = "EEEE, MMMM dd, yyyy"
                )

                val bannerHeightDp = 352.dp
                val bannerHeightPx = with(LocalDensity.current) { bannerHeightDp.toPx() }
                val pastBanner by remember {
                    derivedStateOf { scrollState.value > bannerHeightPx }
                }
                val topBarColor by animateColorAsState(
                    targetValue = if (pastBanner) MaterialTheme.colorScheme.background
                    else Color.Transparent,
                    label = "topBarBackground"
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        MediaBanner(media)

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = media.title,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp,
                                lineHeight = 28.sp,
                                modifier = Modifier.fillMaxWidth()
                            )

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
                                        value = String.format("%.1f", media.voteAverage)
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    MovieDescription(
                                        label = stringResource(R.string.runtime),
                                        value = "${media.runtimeMinutes} min",
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    MovieDescription(
                                        label = "Status",
                                        value = media.status,
                                    )
                                }
                            }

                            Text(
                                text = media.overview,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )

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

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(topBarColor)
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        OutlinedIconButton(
                            onClick = onBackPressed,
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
            }
        }
    }
}

@Composable
fun MediaBanner(media: MediaItemDetail) {
    val backdropUrl = media.backdropPath ?: media.posterPath

    val backdropHeight = 320.dp
    val offset = 32.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(backdropHeight + offset)
    ) {
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
                    contentDescription = media.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(20.dp)
                )
            }
        }

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
                    .data(media.posterPath)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.placeholder_background),
                contentDescription = media.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
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
