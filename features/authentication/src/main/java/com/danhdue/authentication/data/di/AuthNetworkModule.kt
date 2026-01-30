package com.danhdue.authentication.data.di

import com.danhdue.authentication.data.datasources.remote.AuthApi
import com.danhdue.authentication.data.datasources.remote.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit

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
            @Named("AuthClient") authClient: OkHttpClient
    ): Retrofit {
        return retrofitBuilder.baseUrl("https://api.wallet.com/auth/").client(authClient).build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(@Named("AuthRetrofit") retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }
}
