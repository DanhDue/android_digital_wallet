/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.data.remote

import com.danhdue.settings.data.remote.dto.BootstrapRequestDto
import com.danhdue.settings.data.remote.dto.BootstrapResponseDto
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface SettingsApiService {
    @POST("settings/sync/bootstrap")
    suspend fun bootstrap(
        @Body request: BootstrapRequestDto,
    ): BootstrapResponseDto

    @GET("translations/{code}")
    suspend fun getTranslations(
        @Path("code") code: String,
        @Query("since_version") sinceVersion: String? = null,
    ): ResponseBody
}
