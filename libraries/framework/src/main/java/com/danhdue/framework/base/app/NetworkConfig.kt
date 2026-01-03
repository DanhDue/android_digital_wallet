/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.framework.base.app

abstract class NetworkConfig {
    abstract fun baseUrl(): String

    abstract fun timeOut(): Long

    open fun isDev() = false
}
