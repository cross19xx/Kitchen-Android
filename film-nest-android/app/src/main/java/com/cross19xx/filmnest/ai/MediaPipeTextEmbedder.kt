package com.cross19xx.filmnest.ai

import android.content.Context
import com.google.mediapipe.tasks.core.BaseOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.mediapipe.tasks.text.textembedder.TextEmbedder as MpTextEmbedder

class MediaPipeTextEmbedder(context: Context) : TextEmbedder {
    private companion object {
        const val MODEL_ASSET = "universal_sentence_encoder.tflite"
    }

    private val embedder: MpTextEmbedder = MpTextEmbedder.createFromOptions(
        context,
        MpTextEmbedder.TextEmbedderOptions.builder()
            .setBaseOptions(
                BaseOptions.builder()
                    .setModelAssetPath(MODEL_ASSET)
                    .build()
            )
            .setL2Normalize(true)
            .build()
    )

    override suspend fun embed(text: String): FloatArray = withContext(Dispatchers.Default) {
        val result = embedder.embed(text)
        result.embeddingResult().embeddings().first().floatEmbedding()

    }
}
