package com.danhdue.authentication.data.datasources.remote

import com.danhdue.framework.network.model.FeatureConfig
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Tag

interface AuthApi {

        @POST("login")
        suspend fun login(
                @Body
                loginRequest:
                        Map<String, String>, // Using Map for simplicity as per requirement sample
                @Tag
                featureConfig: FeatureConfig =
                        FeatureConfig(appId = "auth_01", featureName = "Authentication")
        ): Response<Unit> // Replace Unit with actual response model

        @POST("register")
        suspend fun register(
                @Body registerRequest: Map<String, String>,
                @Tag
                featureConfig: FeatureConfig =
                        FeatureConfig(appId = "auth_01", featureName = "Authentication")
        ): Response<Unit>
}
