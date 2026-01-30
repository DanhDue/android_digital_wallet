package com.danhdue.authentication.data.datasources.remote

import com.danhdue.authentication.data.models.LoginRequestDto
import com.danhdue.authentication.data.models.LoginResponseDto
import com.danhdue.framework.network.calladapter.NetworkResponse
import com.danhdue.framework.network.model.FeatureConfig
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Tag

interface AuthApi {

        @POST("api/v1/users/login")
        suspend fun login(
                @Body loginRequest: LoginRequestDto,
                @Tag
                featureConfig: FeatureConfig =
                        FeatureConfig(appId = "auth_01", featureName = "Authentication")
        ): NetworkResponse<LoginResponseDto>

        @POST("register")
        suspend fun register(
                @Body registerRequest: Map<String, String>,
                @Tag
                featureConfig: FeatureConfig =
                        FeatureConfig(appId = "auth_01", featureName = "Authentication")
        ): Response<Unit>
}
