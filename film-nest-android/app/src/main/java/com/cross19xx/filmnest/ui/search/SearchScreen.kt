package com.cross19xx.filmnest.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.cross19xx.filmnest.R
import com.cross19xx.filmnest.components.ErrorDisplay
import com.cross19xx.filmnest.components.MediaCard
import com.cross19xx.filmnest.components.PersonCard
import com.cross19xx.filmnest.data.model.MediaType
import com.cross19xx.filmnest.data.model.SearchResult

@Composable
fun SearchScreen(
    query: String,
    uiState: SearchUiState,
    onBackPressed: () -> Unit,
    onQueryChange: (String) -> Unit,
    onViewMediaDetails: (mediaId: Int, mediaType: MediaType) -> Unit,
) {
    Scaffold(
        topBar = {
            SearchInputBar(
                query = query,
                onBackPressed = onBackPressed,
                onQueryChange = onQueryChange,
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState) {
                SearchUiState.Idle -> CenteredMessage(
                    text = stringResource(R.string.search_for_movies_shows_and_people)
                )

                SearchUiState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                SearchUiState.Empty -> CenteredMessage(
                    stringResource(R.string.no_results_for, query)
                )

                is SearchUiState.Error -> ErrorDisplay()

                is SearchUiState.Success -> SearchResultsGrid(
                    results = uiState.results,
                    onViewMediaDetails = onViewMediaDetails,
                )
            }
        }
    }
}

@Composable
private fun CenteredMessage(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(32.dp)
        )
    }
}

@Composable
fun SearchInputBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackPressed: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackPressed) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = stringResource(R.string.back)
            )
        }

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp),
            placeholder = { Text(stringResource(R.string.search_dots)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_clear),
                            contentDescription = stringResource(R.string.clear_search)
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun SearchResultsGrid(
    results: List<SearchResult>,
    onViewMediaDetails: (mediaId: Int, mediaType: MediaType) -> Unit,
) {
    val media = results.filterIsInstance<SearchResult.Media>()
    val people = results.filterIsInstance<SearchResult.Person>()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        if (media.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionHeader(stringResource(R.string.movies_tv_shows))
            }
            items(
                items = media,
                key = { "media-${it.item.id}" }
            ) { result ->
                MediaCard(
                    media = result.item,
                    onMediaPressed = onViewMediaDetails
                )
            }
        }

        if (people.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionHeader(stringResource(R.string.people))
            }
            items(
                items = people,
                key = { "person-${it.id}" }
            ) { person ->
                PersonCard(person = person)
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}
