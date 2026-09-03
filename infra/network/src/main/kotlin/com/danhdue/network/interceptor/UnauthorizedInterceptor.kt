/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.network.interceptor

import com.danhdue.core.network.HttpStatusCode
import com.danhdue.platform.AppEvent
import com.danhdue.platform.AppEventBus
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Application interceptor that broadcasts [AppEvent.UserLoggedOut] on the
 * [AppEventBus] when a request comes back `401 Unauthorized` and was **not**
 * recovered.
 *
 * As an OkHttp *application* interceptor it observes the single final response —
 * after any [okhttp3.Authenticator] (token refresh + retry) has already run and
 * given up. A recovered 401 is retried by the authenticator and reaches this
 * interceptor as a `2xx`, so it never fires a false positive. Publishing is
 * fire-and-forget (`replay = 0`); with no subscriber the event is simply dropped.
 *
 * An app-side subscriber (`:app` `MainActivity`) collects the event and
 * navigates to the host's post-logout destination (the template ships no auth
 * flow, so it currently re-points at `AppRoutes.ShellRoute`).
 */
@Singleton
class UnauthorizedInterceptor
    @Inject
    constructor(
        private val appEventBus: AppEventBus,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val response = chain.proceed(chain.request())
            if (response.code == HttpStatusCode.Unauthorized.code) {
                appEventBus.publish(AppEvent.UserLoggedOut)
            }
            return response
        }
    }
