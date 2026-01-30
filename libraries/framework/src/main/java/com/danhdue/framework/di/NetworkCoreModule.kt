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
import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.danhdue.framework.BuildConfig
import com.danhdue.framework.network.createChuckInterceptor
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named

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
    fun provideChuckerInterceptor(@ApplicationContext context: Context): ChuckerInterceptor {
        return createChuckInterceptor(context)
    }

    @Provides
    @Singleton
    fun provideBaseOkHttpClient(
            loggingInterceptor: HttpLoggingInterceptor,
            globalHeaderInterceptor: GlobalHeaderInterceptor,
            chuckerInterceptor: ChuckerInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
                .apply {
                    if (BuildConfig.DEBUG) {
                        addInterceptor(chuckerInterceptor)
                    }
                }
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

    @Provides
    @Singleton
    @Named("BaseUrl")
    fun provideBaseUrl(): String {
        return BuildConfig.BASE_URL
    }
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
