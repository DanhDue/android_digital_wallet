/*
 * Copyright © 2024, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.wallet.di

import com.danhdue.wallet.BuildConfig
import com.danhdue.framework.base.app.NetworkConfig

class ApiNetworkConfig : NetworkConfig() {
    override fun baseUrl(): String = ""

    override fun timeOut(): Long = 30L

    override fun isDev(): Boolean = BuildConfig.DEBUG
}
