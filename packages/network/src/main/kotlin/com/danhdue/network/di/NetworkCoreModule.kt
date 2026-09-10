/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.network.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.danhdue.core.network.calladapter.NetworkResponseAdapterFactory
import com.danhdue.network.BuildConfig
import com.danhdue.network.createChuckInterceptor
import com.danhdue.network.createOkHttpClient
import com.danhdue.network.interceptor.GlobalHeaderInterceptor
import com.danhdue.network.interceptor.UnauthorizedInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkCoreModule {
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    @Singleton
    fun provideChuckerInterceptor(
        @ApplicationContext context: Context,
    ): ChuckerInterceptor = createChuckInterceptor(context)

    @Provides
    @Singleton
    fun provideBaseOkHttpClient(
        @ApplicationContext context: Context,
        loggingInterceptor: HttpLoggingInterceptor,
        globalHeaderInterceptor: GlobalHeaderInterceptor,
        chuckerInterceptor: ChuckerInterceptor,
        unauthorizedInterceptor: UnauthorizedInterceptor,
    ): OkHttpClient {
        val interceptors =
            mutableListOf<Interceptor>(
                loggingInterceptor,
                globalHeaderInterceptor,
                // Final-response 401 watchdog — publishes AppEvent.UserLoggedOut when a
                // request is still Unauthorized after the token Authenticator gave up.
                unauthorizedInterceptor,
            )
        if (BuildConfig.DEBUG) {
            interceptors.add(chuckerInterceptor)
        }
        return createOkHttpClient(
            isCache = false,
            interceptors = interceptors,
            context = context,
        )
    }

    @Provides
    @Singleton
    fun provideMoshiConverterFactory(): MoshiConverterFactory = MoshiConverterFactory.create()

    @Provides
    @Singleton
    @Named("BaseUrl")
    fun provideBaseUrl(): String = BuildConfig.BASE_URL

    @Provides
    @Singleton
    fun provideRetrofitBuilder(
        baseOkHttpClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory,
    ): Retrofit.Builder =
        Retrofit
            .Builder()
            .client(baseOkHttpClient)
            .addConverterFactory(moshiConverterFactory)
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
}
