package com.cross19xx.filmnest.ai

import kotlin.math.sqrt

object VectorMath {
    fun cosineSimilarity(a: FloatArray, b: FloatArray): Float {
        require(a.size == b.size) { "Mismatched dimensions" }

        var dotProduct = 0f
        var aMagnitudeSquared = 0f
        var bMagnitudeSquared = 0f

        for (i in a.indices) {
            val aValue = a[i]
            val bValue = b[i]

            dotProduct += aValue * bValue
            aMagnitudeSquared += aValue * aValue
            bMagnitudeSquared += bValue * bValue
        }

        if (aMagnitudeSquared == 0f || bMagnitudeSquared == 0f) return 0f

        val denominator = sqrt(aMagnitudeSquared) * sqrt(bMagnitudeSquared)

        return dotProduct / denominator
    }
}