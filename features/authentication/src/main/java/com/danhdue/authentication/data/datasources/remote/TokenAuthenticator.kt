/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.data.datasources.remote

import com.danhdue.authentication.data.models.RefreshTokenRequestDto
import com.danhdue.authentication.data.models.RefreshTokenResponseDto
import com.danhdue.core.network.calladapter.NetworkResponse
import com.danhdue.core.pref.SecureCacheStore
import com.danhdue.core.session.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

/**
 * Authenticator that handles 401 Unauthorized responses by refreshing the access token.
 *
 * Note: This class uses [runBlocking] because OkHttp's [Authenticator] runs on a dedicated
 * I/O thread from OkHttp's dispatcher, not the Main thread. This is acceptable and documented.
 */
class TokenAuthenticator @Inject constructor(
    @Named("AuthSecureCacheStore") private val secureCacheStore: SecureCacheStore,
    private val apiServiceProvider: Provider<AuthApiService>,
    private val sessionManager: SessionManager,
) : Authenticator {
    override fun authenticate(
        route: Route?,
        response: Response,
    ): Request? {
        Timber.d("Authentication failed for ${response.request.url}. Attempting to refresh token.")

        return synchronized(this) {
            if (shouldGiveUp(response)) {
                Timber.e("Failed to authenticate $MAX_RETRY_COUNT times, giving up.")
                sessionManager.logout()
                return null
            }

            attemptRefresh(response)
        }
    }

    private fun attemptRefresh(response: Response): Request? {
        val currentAccessToken =
            runBlocking {
                secureCacheStore.read(KEY_ACCESS_TOKEN, "")
            }

        if (isRequestTokenStale(response, currentAccessToken)) {
            Timber.d("Token was already refreshed by another thread. Retrying with new token.")
            return buildRetryRequest(response.request, currentAccessToken)
        }

        val refreshToken =
            runBlocking {
                secureCacheStore.read(KEY_REFRESH_TOKEN, "")
            }

        return if (refreshToken.isNotEmpty()) {
            refreshAndRetry(refreshToken, response)
        } else {
            Timber.d("No refresh token available. Logout required.")
            sessionManager.logout()
            null
        }
    }

    private fun shouldGiveUp(response: Response): Boolean = response.code == HTTP_UNAUTHORIZED && responseCount(response) >= MAX_RETRY_COUNT

    private fun isRequestTokenStale(
        response: Response,
        currentAccessToken: String,
    ): Boolean {
        val requestToken = response.request.header(HEADER_AUTHORIZATION)?.replace(PREFIX_BEARER, "")
        return requestToken != null && requestToken != currentAccessToken && currentAccessToken.isNotEmpty()
    }

    private fun buildRetryRequest(
        request: Request,
        accessToken: String,
    ): Request =
        request
            .newBuilder()
            .header(HEADER_AUTHORIZATION, "$PREFIX_BEARER$accessToken")
            .build()

    private fun refreshAndRetry(
        refreshToken: String,
        response: Response,
    ): Request? =
        runBlocking {
            try {
                val apiService = apiServiceProvider.get()
                val refreshResponse = apiService.refresh(RefreshTokenRequestDto(refresh = refreshToken))

                if (refreshResponse is NetworkResponse.Success) {
                    handleRefreshSuccess(refreshResponse.body, response)
                } else {
                    Timber.e("Failed to refresh token: ${refreshResponse::class.simpleName}")
                    handleRefreshFailure()
                    null
                }
            } catch (e: Exception) {
                Timber.e(e, "Exception during token refresh")
                handleRefreshFailure()
                null
            }
        }

    private suspend fun handleRefreshSuccess(
        tokens: RefreshTokenResponseDto,
        response: Response,
    ): Request? {
        val newAccessToken = tokens.access
        val newRefreshToken = tokens.refresh

        val updates = mutableMapOf<String, Any>()
        if (newAccessToken.isNotEmpty()) {
            updates[KEY_ACCESS_TOKEN] = newAccessToken
        }
        if (newRefreshToken.isNotEmpty()) {
            updates[KEY_REFRESH_TOKEN] = newRefreshToken
        }

        if (updates.isNotEmpty()) {
            secureCacheStore.writeBatch(updates)
        }

        return if (newAccessToken.isNotEmpty()) {
            Timber.d("Token refreshed successfully. Retrying request.")
            buildRetryRequest(response.request, newAccessToken)
        } else {
            Timber.e("Refresh successful but no access token returned.")
            null
        }
    }

    private suspend fun handleRefreshFailure() {
        secureCacheStore.clearAll()
        sessionManager.logout()
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val PREFIX_BEARER = "Bearer "
        private const val HTTP_UNAUTHORIZED = 401
        private const val MAX_RETRY_COUNT = 3
    }
}
