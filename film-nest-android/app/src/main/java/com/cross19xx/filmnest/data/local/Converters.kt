package com.cross19xx.filmnest.data.local

import androidx.room.TypeConverter
import com.cross19xx.filmnest.data.model.MediaType
import java.nio.ByteBuffer

class Converters {
    @TypeConverter
    fun fromFloatArray(input: FloatArray): ByteArray {
        val buffer = ByteBuffer.allocate(input.size * Float.SIZE_BYTES)

        input.forEach { buffer.putFloat(it) }
        return buffer.array()
    }

    @TypeConverter
    fun fromIntList(input: List<Int>): ByteArray {
        val buffer = ByteBuffer.allocate(input.size * Int.SIZE_BYTES)

        input.forEach { buffer.putInt(it) }
        return buffer.array()
    }

    @TypeConverter
    fun fromMediaType(input: MediaType): String = input.name

    @TypeConverter
    fun toFloatArray(input: ByteArray): FloatArray {
        val buffer = ByteBuffer.wrap(input)
        return FloatArray(input.size / Float.SIZE_BYTES) { buffer.getFloat() }
    }

    @TypeConverter
    fun toIntList(input: ByteArray): List<Int> {
        val buffer = ByteBuffer.wrap(input)
        return List(input.size / Int.SIZE_BYTES) { buffer.getInt() }
    }

    @TypeConverter
    fun toMediaType(input: String): MediaType = MediaType.valueOf(input)
}