package com.cross19xx.filmnest.ui.genre

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cross19xx.filmnest.R
import com.cross19xx.filmnest.components.ErrorDisplay
import com.cross19xx.filmnest.components.MovieCard

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
                            MovieCard(
                                movie = movie,
                                onMoviePressed = onViewMovieDetails,
                            )
                        }
                    }
                }
            }
        }
    }
}
