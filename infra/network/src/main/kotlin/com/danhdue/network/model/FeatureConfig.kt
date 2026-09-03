/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.network.model

/**
 * Data class to be used as a Retrofit @Tag to identify the feature making the request. This allows
 * the GlobalHeaderInterceptor to inject standard X-App-ID and X-Feature-Name headers.
 */
data class FeatureConfig(
    val appId: String,
    val featureName: String,
)
