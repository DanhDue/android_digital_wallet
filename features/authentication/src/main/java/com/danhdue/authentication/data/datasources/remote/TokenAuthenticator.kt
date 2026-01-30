package com.danhdue.authentication.data.datasources.remote

import javax.inject.Inject
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator @Inject constructor() : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // TODO: Implement actual token refresh logic here.
        // For now, return null to indicate we gave up.
        return null
    }
}
