package com.cross19xx.filmnest.data.local

import com.cross19xx.filmnest.data.model.MediaType
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class ConvertersTest {
    private val converters = Converters()

    @Test
    fun `float array round-trips through bytes`() {
        val original = floatArrayOf(0.1f, -2.5f, 3.14159f, 0f)
        val bytes = converters.fromFloatArray(original)
        val restored = converters.toFloatArray(bytes)
        assertArrayEquals(original, restored, 0.0f)
    }

    @Test
    fun `int list round-trips through string`() {
        val original = listOf(28, 12, 878)
        val restored = converters.toIntList(converters.fromIntList(original))
        assertEquals(original, restored)
    }

    @Test
    fun `empty int list round-trips to empty list`() {
        assertEquals(emptyList<Int>(), converters.toIntList(converters.fromIntList(emptyList())))
    }

    @Test
    fun `media type round-trips through string`() {
        assertEquals(
            MediaType.TV_SHOW,
            converters.toMediaType(converters.fromMediaType(MediaType.TV_SHOW))
        )
    }
}