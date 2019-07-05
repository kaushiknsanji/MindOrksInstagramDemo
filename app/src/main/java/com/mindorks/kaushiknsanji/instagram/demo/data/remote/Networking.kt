package com.mindorks.kaushiknsanji.instagram.demo.data.remote

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.mindorks.kaushiknsanji.instagram.demo.BuildConfig
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Singleton object for managing the API Header constants, configuring the Retrofit and providing the
 * instance of Retrofit API Service [NetworkService]
 *
 * @author Kaushik N Sanji
 */
object Networking {

    //Header constants for the API
    const val HEADER_API_KEY = "x-api-key"
    const val HEADER_ACCESS_TOKEN = "x-access-token"
    const val HEADER_USER_ID = "x-user-id"

    //Timeout constant (in seconds)
    private const val NETWORK_CALL_TIMEOUT = 60

    //Stores the API Key
    lateinit var API_KEY: String

    /**
     * Factory method that configures the [Retrofit] and provides an instance of the [NetworkService] API
     *
     * @param apiKey String containing the API Key
     * @param baseUrl String containing the Base URL of the API
     * @param cacheDir [File] location in the disk to be used for reading and writing cached responses
     * @param cacheSize [Long] value representing the Max size of the Cache in bytes
     *
     * @return Instance of [NetworkService] API
     */
    fun create(apiKey: String, baseUrl: String, cacheDir: File, cacheSize: Long): NetworkService {
        //Save the API Key
        API_KEY = apiKey
        //Creating an Instance of Retrofit
        return Retrofit.Builder()
            .baseUrl(baseUrl) //URL on which every endpoint will be appended
            .client(
                //Setting up HTTP Client using OkHttpClient
                OkHttpClient.Builder()
                    //Configure Cache
                    .cache(Cache(cacheDir, cacheSize))
                    //Add Interceptor to log the request and response
                    .addInterceptor(
                        HttpLoggingInterceptor()
                            .apply {
                                //Configure Logging with BODY level info only for DEBUG Build
                                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                                //No Logging for any other Build
                                else HttpLoggingInterceptor.Level.NONE
                            })
                    //Read Timeout for response
                    .readTimeout(NETWORK_CALL_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    //Write Timeout for request
                    .writeTimeout(NETWORK_CALL_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .build() //Generate the OkHttpClient instance
            )
            //GSON Converter Factory to parse JSON and construct Objects
            .addConverterFactory(GsonConverterFactory.create())
            //Rx Call Adapter to wrap requests and response into Rx Streams
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build() //Generate the Retrofit instance
            //Create the NetworkService API with the Retrofit Configuration
            .create(NetworkService::class.java)
    }

}