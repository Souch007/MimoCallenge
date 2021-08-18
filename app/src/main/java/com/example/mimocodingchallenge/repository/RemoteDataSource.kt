package com.example.mimocodingchallenge.repository

import androidx.viewbinding.BuildConfig
import com.example.mimocodingchallenge.helpers.AppConstants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDataSource {
    companion object {
    }

    fun <Api> buildApi(api: Class<Api>): Api {
        return Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .client(OkHttpClient.Builder().also { client ->
                if (BuildConfig.DEBUG) {
                    val logginInt = HttpLoggingInterceptor();
                    logginInt.setLevel(HttpLoggingInterceptor.Level.BODY)
                    client.addInterceptor(logginInt)
                }
            }.build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()
            .create(api)
    }
}