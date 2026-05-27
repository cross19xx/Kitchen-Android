package com.cross19xx.filmnest.ui.settings

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
import com.cross19xx.filmnest.theme.FilmNestTheme


@Composable
fun SettingsScreen(onBackPressed: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.app_name))
        Text("Settings Page")
        OutlinedButton(onClick = onBackPressed) {
            Text("Go back home")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    FilmNestTheme {
        SettingsScreen(onBackPressed = {})
    }
}