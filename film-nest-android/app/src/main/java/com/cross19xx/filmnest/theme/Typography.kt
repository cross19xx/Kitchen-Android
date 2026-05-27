package com.cross19xx.filmnest.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.cross19xx.filmnest.R

var bodyFontFamily = FontFamily(
    Font(R.font.inter_thin, FontWeight.Thin),
    Font(R.font.inter_thin_italic, FontWeight.Thin, style = FontStyle.Italic),
    Font(R.font.inter_extralight, FontWeight.ExtraLight),
    Font(R.font.inter_extralight_italic, FontWeight.ExtraLight, style = FontStyle.Italic),
    Font(R.font.inter_light, FontWeight.Light),
    Font(R.font.inter_light_italic, FontWeight.Light, style = FontStyle.Italic),
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_italic, FontWeight.Normal, style = FontStyle.Italic),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_medium_italic, FontWeight.Medium, style = FontStyle.Italic),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_semibold_italic, FontWeight.SemiBold, style = FontStyle.Italic),
    Font(R.font.inter_bold, FontWeight.Bold),
    Font(R.font.inter_bold_italic, FontWeight.Bold, style = FontStyle.Italic),
    Font(R.font.inter_extrabold, FontWeight.ExtraBold),
    Font(R.font.inter_extrabold_italic, FontWeight.ExtraBold, style = FontStyle.Italic),
    Font(R.font.inter_black, FontWeight.Black),
    Font(R.font.inter_black_italic, FontWeight.Black, style = FontStyle.Italic),
)

var displayFontFamily = FontFamily(
    Font(R.font.ibm_plex_serif_thin, FontWeight.Thin),
    Font(R.font.ibm_plex_serif_thin_italic, FontWeight.Thin, style = FontStyle.Italic),
    Font(R.font.ibm_plex_serif_extralight, FontWeight.ExtraLight),
    Font(R.font.ibm_plex_serif_extralight_italic, FontWeight.ExtraLight, style = FontStyle.Italic),
    Font(R.font.ibm_plex_serif_light, FontWeight.Light),
    Font(R.font.ibm_plex_serif_light_italic, FontWeight.Light, style = FontStyle.Italic),
    Font(R.font.ibm_plex_serif_regular, FontWeight.Normal),
    Font(R.font.ibm_plex_serif_italic, FontWeight.Normal, style = FontStyle.Italic),
    Font(R.font.ibm_plex_serif_medium, FontWeight.Medium),
    Font(R.font.ibm_plex_serif_medium_italic, FontWeight.Medium, style = FontStyle.Italic),
    Font(R.font.ibm_plex_serif_semibold, FontWeight.SemiBold),
    Font(R.font.ibm_plex_serif_semibold_italic, FontWeight.SemiBold, style = FontStyle.Italic),
    Font(R.font.ibm_plex_serif_bold, FontWeight.Bold),
    Font(R.font.ibm_plex_serif_bold_italic, FontWeight.Bold, style = FontStyle.Italic),
)


val baseline = Typography()

val Typography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = displayFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = displayFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = displayFontFamily),

    bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily),
)
