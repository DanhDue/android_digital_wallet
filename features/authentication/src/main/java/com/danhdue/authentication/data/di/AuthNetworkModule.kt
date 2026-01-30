package com.danhdue.authentication.data.di

import com.danhdue.authentication.data.datasources.remote.AuthApiService
import com.danhdue.authentication.data.datasources.remote.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthNetworkModule {

    @Provides
    @Singleton
    @Named("AuthClient")
    fun provideAuthOkHttpClient(
            baseOkHttpClient: OkHttpClient,
            tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return baseOkHttpClient.newBuilder().authenticator(tokenAuthenticator).build()
    }

    @Provides
    @Singleton
    @Named("AuthRetrofit")
    fun provideAuthRetrofit(
            retrofitBuilder: Retrofit.Builder,
            @Named("AuthClient") authClient: OkHttpClient,
            @Named("BaseUrl") baseUrl: String
    ): Retrofit {
        return retrofitBuilder
                .baseUrl(baseUrl)
                .client(authClient)
                .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(@Named("AuthRetrofit") retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }
}
