/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.plugin.platform

import io.flutter.plugin.common.BasicMessageChannel
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.StandardMessageCodec
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

data class PigeonPluginData(
    val id: String = "",
    val title: String = "",
    val timestamp: Long = 0L
) {
    companion object {
        fun fromList(list: List<Any?>): PigeonPluginData {
            val id = list.getOrNull(0) as? String ?: ""
            val title = list.getOrNull(1) as? String ?: ""
            val timestamp = (list.getOrNull(2) as? Number)?.toLong() ?: 0L
            return PigeonPluginData(id = id, title = title, timestamp = timestamp)
        }
    }

    fun toList(): List<Any?> {
        return listOf(id, title, timestamp)
    }
}

interface MyPluginHostApi {
    fun getPlatformVersion(): String
    fun getData(callback: (Result<PigeonPluginData>) -> Unit)

    companion object {
        val codec: StandardMessageCodec = StandardMessageCodec.INSTANCE

        fun setUp(binaryMessenger: BinaryMessenger, api: MyPluginHostApi?) {
            run {
                val channel = BasicMessageChannel<Any?>(
                    binaryMessenger,
                    "dev.flutter.pigeon.my_plugin.MyPluginHostApi.getPlatformVersion",
                    codec
                )
                if (api != null) {
                    channel.setMessageHandler { _, reply ->
                        try {
                            reply.reply(listOf(api.getPlatformVersion()))
                        } catch (error: Throwable) {
                            reply.reply(listOf<Any?>(null, error.message, error.cause?.toString()))
                        }
                    }
                } else {
                    channel.setMessageHandler(null)
                }
            }

            run {
                val channel = BasicMessageChannel<Any?>(
                    binaryMessenger,
                    "dev.flutter.pigeon.my_plugin.MyPluginHostApi.getData",
                    codec
                )
                if (api != null) {
                    channel.setMessageHandler { _, reply ->
                        api.getData { result ->
                            if (result.isSuccess) {
                                val data = result.getOrNull() ?: PigeonPluginData()
                                reply.reply(listOf(data.toList()))
                            } else {
                                val error = result.exceptionOrNull()
                                reply.reply(listOf<Any?>(null, error?.message ?: "Unknown error", null))
                            }
                        }
                    }
                } else {
                    channel.setMessageHandler(null)
                }
            }
        }
    }
}
