package com.mindorks.kaushiknsanji.instagram.demo.data.remote

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.mindorks.kaushiknsanji.instagram.demo.BuildConfig
import okhttp3.Authenticator
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Singleton object for managing the API Header constants, configuring the Retrofit and providing the
 * instance of the required API Service.
 *
 * @author Kaushik N Sanji
 */
object Networking {

    // Header constants for the API
    const val HEADER_API_KEY = "x-api-key"
    const val HEADER_ACCESS_TOKEN = "x-access-token"
    const val HEADER_USER_ID = "x-user-id"

    // Timeout constant (in seconds)
    private const val NETWORK_CALL_TIMEOUT = 60L

    // Stores the API Key
    lateinit var API_KEY: String

    /**
     * Function type to create and provide [HttpLoggingInterceptor] with
     * [HttpLoggingInterceptor.Level.BODY] level logging enabled only
     * for DEBUG Build. For other build types, no logging is enabled.
     */
    val httpLoggingInterceptor: () -> HttpLoggingInterceptor = {
        HttpLoggingInterceptor()
            .apply {
                // Configure Logging with BODY level info only for DEBUG Build
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                // No Logging for any other Build
                else HttpLoggingInterceptor.Level.NONE
            }
    }

    /**
     * Creates [OkHttpClient] with the provided configuration parameters.
     *
     * @param cacheDir [File] location in the disk to be used for reading and writing cached responses
     * @param cacheSize [Long] value representing the Max size of the Cache in bytes
     * @param loggingInterceptor [HttpLoggingInterceptor] instance for logging request and response
     * @param authenticator [Authenticator] instance for re-authenticating unauthorized requests.
     * Defaulted to [Authenticator.NONE].
     *
     * @return Instance of [OkHttpClient]
     */
    fun createOkHttpClient(
        cacheDir: File,
        cacheSize: Long,
        loggingInterceptor: HttpLoggingInterceptor,
        authenticator: Authenticator = Authenticator.NONE
    ): OkHttpClient = OkHttpClient.Builder()
        // Configure Cache
        .cache(Cache(cacheDir, cacheSize))
        // Add authenticator for re-authenticating unauthorized requests
        .authenticator(authenticator)
        // Add Interceptor to log request and response
        .addInterceptor(loggingInterceptor)
        // Read Timeout for response
        .readTimeout(NETWORK_CALL_TIMEOUT, TimeUnit.SECONDS)
        // Write Timeout for request
        .writeTimeout(NETWORK_CALL_TIMEOUT, TimeUnit.SECONDS)
        .build() // Build and generate the OkHttpClient instance

    /**
     * Service Factory method that configures [Retrofit] and provides an instance of the
     * API for the [service] given.
     *
     * @param T The type of [Retrofit] [service] needed
     * @param apiKey [String] containing the API Key
     * @param baseUrl [String] containing the Base URL of the API
     * @param okHttpClient [OkHttpClient] instance for use with [Retrofit]
     *
     * @return Instance of [Retrofit] [service] API
     */
    fun <T : Any> createService(
        apiKey: String,
        baseUrl: String,
        okHttpClient: OkHttpClient,
        service: Class<T>
    ): T {
        // Save the API Key
        API_KEY = apiKey
        // Creating an Instance of Retrofit
        return Retrofit.Builder()
            .baseUrl(baseUrl) // URL on which every endpoint will be appended
            // Setting up HTTP Client using OkHttpClient
            .client(okHttpClient)
            // GSON Converter Factory to parse JSON and construct Objects
            .addConverterFactory(GsonConverterFactory.create())
            // Rx Call Adapter to wrap requests and response into Rx Streams
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build() // Build and generate the Retrofit instance
            // Create the API for the Service with this Retrofit Configuration
            .create(service)
    }

}