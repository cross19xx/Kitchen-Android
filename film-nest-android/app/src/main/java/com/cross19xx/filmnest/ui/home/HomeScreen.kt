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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cross19xx.filmnest.core.theme.FilmNestTheme
import com.cross19xx.filmnest.data.model.Movie


@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onViewMovieDetails: (movieId: String) -> Unit,
    onViewSettings: () -> Unit
) {

    Scaffold { innerPadding ->
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    HomeHeader(topPadding = 0.dp)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        MovieSection("Now Playing", uiState.nowPlaying)
                        MovieSection("Popular", uiState.popular)
                        MovieSection("Top Rated", uiState.topRated)
                        MovieSection("Upcoming", uiState.upcoming)

                        uiState.genres.forEach { genre ->
                            Text(genre.name)
                        }
                    }


                }
            }

            is HomeUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = uiState.message)
                }
            }
        }
    }

}

@Composable
fun HomeHeader(topPadding: Dp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = topPadding,
                start = 16.dp,
                end = 16.dp,
                bottom = 8.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically

    ) {
        Text(
            text = "Hello there",
            style = MaterialTheme.typography.displaySmall,
            fontSize = 24.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Box(
            modifier = Modifier
                .size(width = 24.dp, height = 24.dp)
                .background(MaterialTheme.colorScheme.primary)

        )
    }
}

@Composable
fun MovieSection(title: String, movies: List<Movie>) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = title,
            fontSize = 18.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        movies.forEach { movie ->
            Text(movie.title)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val dummyMovies = listOf(
        Movie(
            id = 1304313,
            title = "Lee Cronin's The Mummy",
            overview = "The young daughter of a journalist disappears into the desert without a trace—eight years later, the broken family is shocked when she is returned to them, as what should be a joyful reunion turns into a living nightmare.",
            posterUrl = null,
            backdropUrl = null,
            releaseDate = "2026-04-15",
            voteAverage = 8.037,
            genreIds = listOf(27, 9648),
        ),
        Movie(
            id = 1380291,
            title = "Tom Clancy's Jack Ryan: Ghost War",
            overview = "Jack Ryan is reluctantly pulled back into espionage when an international covert mission unravels a deadly conspiracy. Racing against time, he joins CIA allies Mike November & James Greer and sharp MI6 officer Emma Marlowe to battle a rogue black-ops unit in a high-stakes, deeply personal fight.",
            posterUrl = null,
            backdropUrl = null,
            releaseDate = "2026-05-20",
            voteAverage = 7.22,
            genreIds = listOf(28, 53),
        ),
        Movie(
            id = 1439930,
            title = "The Punisher: One Last Kill",
            overview = "As Frank Castle searches for meaning beyond revenge, an unexpected force pulls him back into the fight.",
            posterUrl = null,
            backdropUrl = null,
            releaseDate = "2026-05-12",
            voteAverage = 8.437,
            genreIds = listOf(28, 18, 80),
        ),
        Movie(
            id = 1226863,
            title = "The Super Mario Galaxy Movie",
            overview = "Having thwarted Bowser's previous plot to marry Princess Peach, Mario and Luigi now face a fresh threat in Bowser Jr., who is determined to liberate his father from captivity and restore the family legacy. Alongside companions new and old, the brothers travel across the stars to stop the young heir's crusade.",
            posterUrl = null,
            backdropUrl = null,
            releaseDate = "2026-04-01",
            voteAverage = 7.924,
            genreIds = listOf(10751, 35, 12, 14, 16),
        ),
        Movie(
            id = 1007757,
            title = "Swapped",
            overview = "A small woodland creature and a majestic bird, two natural sworn ely trade places and set off on an adventure of a lifetime to switch back. Their journeysoon uncovers a greater threat—one that could endanger not only their species, but the entire valley they call home.",
            posterUrl = null,
            backdropUrl = null,
            releaseDate = "2026-05-01",
            voteAverage = 9.0,
            genreIds = listOf(12, 16, 10751, 14),
        ),
    )

    FilmNestTheme {
        HomeScreen(
            uiState = HomeUiState.Loading,
            onViewSettings = { },
            onViewMovieDetails = { }
        )
    }
}

private val DUMMY_MOVIE_LIST = listOf<Movie>()