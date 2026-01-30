package com.danhdue.framework.di

import com.danhdue.framework.network.calladapter.NetworkResponseAdapterFactory
import com.danhdue.framework.network.interceptor.GlobalHeaderInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkCoreModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    @Provides
    @Singleton
    fun provideBaseOkHttpClient(
            loggingInterceptor: HttpLoggingInterceptor,
            globalHeaderInterceptor: GlobalHeaderInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(globalHeaderInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
    }

    @Provides
    @Singleton
    fun provideMoshiConverterFactory(): MoshiConverterFactory {
        return MoshiConverterFactory.create()
    }

    /**
     * Provides a pre-configured Retrofit.Builder. Note: .baseUrl() and .build() are NOT called
     * here. Use this builder to create specialized Retrofit instances in feature modules.
     */
    @Provides
    @Singleton
    fun provideRetrofitBuilder(
            baseOkHttpClient: OkHttpClient,
            moshiConverterFactory: MoshiConverterFactory
    ): Retrofit.Builder {
        return Retrofit.Builder()
                .client(baseOkHttpClient)
                .addConverterFactory(moshiConverterFactory)
                .addCallAdapterFactory(NetworkResponseAdapterFactory())
    }
}
