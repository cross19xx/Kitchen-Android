package com.cross19xx.filmnest.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cross19xx.filmnest.R
import com.cross19xx.filmnest.core.theme.FilmNestTheme


@Composable
fun MovieDetailsScreen(
    movieId: String,
    onBackPressed: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.app_name))

        Text("View details for $movieId")

        OutlinedButton (onClick = onBackPressed) {
            Text("Go back to home")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MovieDetailsScreenPreview() {
    FilmNestTheme {
        MovieDetailsScreen(movieId = "test-movie-id", onBackPressed = { })
    }
}
