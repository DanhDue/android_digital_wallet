package com.danhdue.framework.network.interceptor

import com.danhdue.framework.network.model.FeatureConfig
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that looks for a [FeatureConfig] tag on the request. If found, it injects "X-App-ID"
 * and "X-Feature-Name" headers.
 */
class GlobalHeaderInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val featureConfig = originalRequest.tag(FeatureConfig::class.java)

        return if (featureConfig != null) {
            val newRequest =
                    originalRequest
                            .newBuilder()
                            .header("X-App-ID", featureConfig.appId)
                            .header("X-Feature-Name", featureConfig.featureName)
                            .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }
}
