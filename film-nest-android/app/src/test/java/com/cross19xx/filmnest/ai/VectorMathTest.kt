package com.cross19xx.filmnest.ai

import org.junit.Assert.assertEquals
import org.junit.Test

class VectorMathTest {

    @Test
    fun `identical vectors have cosine similarity of 1`() {
        val a = floatArrayOf(1f, 2f, 3f)
        assertEquals(1.0f, VectorMath.cosineSimilarity(a, a), 0.0001f)
    }

    @Test
    fun `parallel vectors of different magnitudes are still similarity 1`() {
        val a = floatArrayOf(1f, 0f)
        val b = floatArrayOf(2f, 0f) // same direction, different length
        assertEquals(1.0f, VectorMath.cosineSimilarity(a, b), 0.0001f)
    }

    @Test
    fun `orthogonal vectors have cosine similarity of 0`() {
        val a = floatArrayOf(1f, 0f)
        val b = floatArrayOf(0f, 1f)
        assertEquals(0.0f, VectorMath.cosineSimilarity(a, b), 0.0001f)
    }

    @Test
    fun `opposite vectors have cosine similarity of -1`() {
        val a = floatArrayOf(1f, 1f)
        val b = floatArrayOf(-1f, -1f)
        assertEquals(-1.0f, VectorMath.cosineSimilarity(a, b), 0.0001f)
    }

    @Test
    fun `a zero vector yields zero similarity rather than NaN`() {
        val a = floatArrayOf(0f, 0f)
        val b = floatArrayOf(1f, 2f)
        assertEquals(0.0f, VectorMath.cosineSimilarity(a, b), 0.0001f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `mismatched dimensions throw`() {
        VectorMath.cosineSimilarity(floatArrayOf(1f), floatArrayOf(1f, 2f))
    }
}