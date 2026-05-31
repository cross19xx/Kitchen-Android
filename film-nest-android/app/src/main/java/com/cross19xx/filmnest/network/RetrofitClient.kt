package com.cross19xx.filmnest.network

import com.cross19xx.filmnest.BuildConfig
import androidx.appcompat.app.AppCompatDelegate
import com.cross19xx.filmnest.data.model.AppLanguage
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

object RetrofitClient {
    private const val BASE_URL = BuildConfig.TMDB_BASE_URL
    private const val API_KEY = BuildConfig.TMDB_API_KEY

    private val authInterceptor = Interceptor { chain ->
        val url = chain.request().url.newBuilder()
            .addQueryParameter("api_key", API_KEY)
            .build()

        chain.proceed(chain.request().newBuilder().url(url).build())
    }

    private val languageInterceptor = Interceptor { chain ->
        val url = chain.request().url.newBuilder()
            .addQueryParameter("language", currentLanguageTag())
            .build()

        chain.proceed(chain.request().newBuilder().url(url).build())
    }

    private fun currentLanguageTag(): String = languageTagFor(
        locale = AppCompatDelegate.getApplicationLocales()[0] ?: Locale.getDefault()
    )

    private val supportedTags: Set<String> = AppLanguage.entries.mapNotNull { it.tag }.toSet()

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(languageInterceptor)
        .build()

    val instance: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    internal fun languageTagFor(locale: Locale?): String {
        val lang = locale?.language
        return if (lang != null && lang in supportedTags) lang else "en"
    }
}
