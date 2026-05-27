package com.cross19xx.filmnest.utils

import java.text.SimpleDateFormat
import java.util.Locale

object DateUtils {

    fun formatDate(
        input: String?,
        inputFormat: String = "yyyy-MM-dd",
        outputFormat: String = "MMMM dd, yyyy"
    ): String? {
        if (input.isNullOrBlank()) return null

        return try {
            val parser = SimpleDateFormat(inputFormat, Locale.getDefault())
            val formatter = SimpleDateFormat(outputFormat, Locale.getDefault())

            val date = parser.parse(input)

            date?.let { formatter.format(it) }
        } catch (e: Exception) {
            e.printStackTrace()

            null
        }
    }
}
