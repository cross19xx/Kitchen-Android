package com.cross19xx.filmnest.ai


/**
 * Turns text into an embedding vector on-device. Implementations return an
 * L2-normalized [FloatArray] so cosine similarity reduces to a dot product.
 */
interface TextEmbedder {
    suspend fun embed(text: String): FloatArray
}
