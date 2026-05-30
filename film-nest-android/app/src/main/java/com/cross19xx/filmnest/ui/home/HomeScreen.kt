package com.cross19xx.filmnest.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.cross19xx.filmnest.R
import com.cross19xx.filmnest.components.ErrorDisplay
import com.cross19xx.filmnest.components.MediaCard
import com.cross19xx.filmnest.data.model.Genre
import com.cross19xx.filmnest.data.model.MediaItem
import com.cross19xx.filmnest.data.model.MediaType
import kotlinx.coroutines.flow.Flow

data class FeedItem(
    val title: String,
    val description: String,
    val mediaItems: List<MediaItem>
)


@Composable
fun HomeScreen(
    events: Flow<HomeEvent>,
    isRefreshing: Boolean,
    uiState: HomeUiState,
    onRefresh: () -> Unit,
    onViewMediaDetails: (mediaId: Int, mediaType: MediaType) -> Unit,
    onViewGenreDetails: (genreId: Int) -> Unit,
    onViewSettings: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val retryText = stringResource(R.string.retry)

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is HomeEvent.ShowMessage -> {
                    val snackbarResult = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = retryText,
                        duration = SnackbarDuration.Short
                    )

                    if (snackbarResult == SnackbarResult.ActionPerformed) {
                        onRefresh()
                    }
                }
            }
        }
    }


    Scaffold(
        topBar = {
            if (uiState is HomeUiState.Success) {
                HomeHeader(onViewSettings = onViewSettings)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        when (uiState) {
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is HomeUiState.Success -> {
                val sections = listOf(
                    FeedItem(
                        title = stringResource(R.string.now_playing),
                        description = stringResource(
                            R.string.see_what_the_world_is_watching_now
                        ),
                        mediaItems = uiState.nowPlaying
                    ),
                    FeedItem(
                        title = stringResource(R.string.popular),
                        description = stringResource(R.string.trending_hits_and_fan_favorites),
                        mediaItems = uiState.popular
                    ),
                    FeedItem(
                        title = stringResource(R.string.top_rated),
                        description = stringResource(
                            R.string.critically_acclaimed_masterpieces_of_cinema
                        ),
                        mediaItems = uiState.topRated
                    ),
                    FeedItem(
                        title = stringResource(R.string.upcoming),
                        description = stringResource(
                            R.string.sneak_peek_at_upcoming_cinematic_experiences
                        ),
                        mediaItems = uiState.upcoming
                    )
                )

                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = onRefresh,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item(key = "now_playing", contentType = "featured") {
                            BannerSection()
                        }

                        item(key = "categories", contentType = "category_list") {
                            GenresRow(
                                genres = uiState.genres,
                                onViewGenreDetails
                            )
                        }

                        items(
                            items = sections,
                            key = { it.title },
                            contentType = { "media_section" }
                        ) { section ->
                            MediaSection(
                                section.title,
                                mediaItems = section.mediaItems,
                                onMediaPressed = onViewMediaDetails,
                                description = section.description
                            )
                        }
                    }
                }
            }

            is HomeUiState.Error -> {
                ErrorDisplay(modifier = Modifier.padding(innerPadding))
            }
        }
    }

}

@Composable
fun HomeHeader(onViewSettings: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(
                top = 0.dp,
                start = 16.dp,
                end = 0.dp, // the button is large enough to cover the padding
                bottom = 8.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Icon(
            painterResource(R.drawable.ic_dove),
            contentDescription = stringResource(R.string.app_name),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = "Hello there",
            style = MaterialTheme.typography.displaySmall,
            fontSize = 20.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onViewSettings) {
            Icon(
                painterResource(R.drawable.ic_settings),
                contentDescription = stringResource(R.string.settings_button),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun BannerSection() {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(R.drawable.banner)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.placeholder_background),
            contentDescription = stringResource(R.string.film_nest_banner_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun GenresRow(
    genres: List<Genre>,
    onViewGenreDetails: (genreId: Int) -> Unit,
) {
    if (genres.isEmpty()) return

    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = stringResource(R.string.genres),
            style = MaterialTheme.typography.displaySmall,
            fontSize = 18.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item { Box(modifier = Modifier.width(4.dp)) }

            items(
                items = genres,
                key = { it.id },
                contentType = { "genres_card" }
            ) { genre ->
                OutlinedButton(
                    onClick = { onViewGenreDetails(genre.id) }
                ) {
                    Text(
                        text = genre.name,
                        fontSize = 14.sp
                    )
                }
            }

            item { Box(modifier = Modifier.width(4.dp)) }
        }
    }
}

@Composable
fun MediaSection(
    title: String,
    description: String?,
    mediaItems: List<MediaItem>,
    onMediaPressed: (mediaId: Int, mediaType: MediaType) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall,
            fontSize = 18.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 2.dp)
        )

        if (description != null) {
            Text(
                text = description,
                fontSize = 12.sp,
                lineHeight = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item { Box(modifier = Modifier.width(4.dp)) } // Left gutter

            items(
                items = mediaItems,
                key = { it.id },
                contentType = { "media_items_card" }
            ) { media ->
                MediaCard(
                    modifier = Modifier.width(120.dp),
                    media = media,
                    onMediaPressed = onMediaPressed
                )
            }

            item { Box(modifier = Modifier.width(4.dp)) } // Right gutter
        }
    }
}
