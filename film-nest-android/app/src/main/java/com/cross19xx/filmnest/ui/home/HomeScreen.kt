package com.cross19xx.filmnest.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cross19xx.filmnest.R
import com.cross19xx.filmnest.core.theme.FilmNestTheme


@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(stringResource(R.string.app_name))
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FilmNestTheme {
        HomeScreen()
    }
}