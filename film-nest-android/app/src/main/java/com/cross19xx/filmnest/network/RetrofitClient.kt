package com.cross19xx.filmnest.network

import com.cross19xx.filmnest.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = BuildConfig.TMDB_BASE_URL
    private const val API_KEY = BuildConfig.TMDB_API_KEY

    private val authInterceptor = Interceptor { chain ->
        val url = chain.request().url.newBuilder()
            .addQueryParameter("api_key", API_KEY)
            .build()

        chain.proceed(chain.request().newBuilder().url(url).build())
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    val instance: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
