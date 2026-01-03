package com.danhdue.framework.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.danhdue.framework.extension.sampleAndKeepLast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class NetworkDetection(
    private val context: Context,
    private val appCoroutineScope: AppCoroutineScope
) {

    private val dispatchFlow = MutableSharedFlow<Boolean>()

    private var pingJob: Job? = null

    private val networkConnectivityReceiver = object : BroadcastReceiver() {

        override fun onReceive(contxt: Context, intent: Intent) {
            if (CONNECTIVITY_STATUS_CHANGE_ACTION_FILTER == intent.action) {
                Timber.w("onReceive - ${System.currentTimeMillis()}")
                appCoroutineScope.launchInIO { dispatchFlow.emit(true) }
            }
        }
    }

    val networkStatus = callbackFlow {

        val intentFilters = IntentFilter()
        intentFilters.addAction(CONNECTIVITY_STATUS_CHANGE_ACTION_FILTER)
        context.registerReceiver(networkConnectivityReceiver, intentFilters)

        dispatchFlow.sampleAndKeepLast(THRESHOLD_NETWORK_STATE_CHANGE_TIME).collectLatest {
            pingJob?.cancel()
            pingJob = appCoroutineScope.launchInIO {
                delay(THRESHOLD_PING_DELAY_TIME)
                try {
                    when (makeProbeRequest()) {
                        InternetStatus.INTERNET -> {
                            Timber.w("InternetStatus.INTERNET")
                            trySend(NetworkStatus.Available).isSuccess
                        }

                        else -> {
                            Timber.w("InternetStatus.NO_INTERNET")
                            trySend(NetworkStatus.Unavailable).isSuccess
                        }
                    }
                } catch (e: Exception) {
                    Timber.w( "${e.message}")
                }
            }
        }
    }.onCompletion {
        Timber.e("unregisterReceiver(networkConnectivityReceiver)")
        context.unregisterReceiver(networkConnectivityReceiver)
    }.distinctUntilChanged()


    private suspend fun makeProbeRequest(): InternetStatus {
        val url = GOOGLE_CONNECTIVITY_CHECK_URL
        return withContext(Dispatchers.IO) {
            var pingConnection: HttpURLConnection? = null
            try {
                pingConnection = if (url.startsWith(REQUEST_PROTOCOL, true)) {
                    URL(url).openConnection() as HttpsURLConnection
                } else URL(url).openConnection() as HttpURLConnection
                pingConnection.requestMethod = REQUEST_METHOD
                pingConnection.connectTimeout = CONNECTION_TIMEOUT
                pingConnection.useCaches = false
                pingConnection.setRequestProperty(
                    HTTP_REQUEST_CONTENT_TYPE_KEY,
                    HTTP_REQUEST_CONTENT_TYPE_VALUE
                )
                pingConnection.connect()
                val responseLength: Int = pingConnection.contentLength
                //this is http(s) response code
                val responseCode = pingConnection.responseCode
                Timber.i("Response Code : $responseCode")
                pingConnection.disconnect()
                resolveProbeResult(responseLength, responseCode)
            } catch (e: java.lang.Exception) {
                pingConnection?.disconnect()
                InternetStatus.NO_INTERNET
            }

        }
    }

    private fun resolveProbeResult(
        responseLength: Int,
        httpCode: Int
    ): InternetStatus {
        // GOOGLE_CONNECTIVITY_CHECK_URL is expected to return HTTP 204
        // with no content as a response
        return if (httpCode == PING_RESPONSE_HTTP_CODE && responseLength == PING_RESPONSE_NO_CONTENT
        ) {
            InternetStatus.INTERNET
        } else {
            // the request would have been intercepted
            InternetStatus.CAPTIVE_PORTAL
        }
    }

    companion object {
        private const val CONNECTIVITY_STATUS_CHANGE_ACTION_FILTER =
            "android.net.conn.CONNECTIVITY_CHANGE"
        private const val THRESHOLD_NETWORK_STATE_CHANGE_TIME = 3_000L
        private const val THRESHOLD_PING_DELAY_TIME = 500L
        private const val REQUEST_PROTOCOL = "https"
        private const val REQUEST_METHOD = "GET"
        private const val CONNECTION_TIMEOUT = 2000
        private const val HTTP_REQUEST_CONTENT_TYPE_KEY = "Content-Type"
        private const val HTTP_REQUEST_CONTENT_TYPE_VALUE = "application/json; utf-8"
        private const val PING_RESPONSE_HTTP_CODE = 204
        private const val PING_RESPONSE_NO_CONTENT = 0
        private const val GOOGLE_CONNECTIVITY_CHECK_URL =
            "https://connectivitycheck.gstatic.com/generate_204"

    }

}

sealed class NetworkStatus {
    data object Available : NetworkStatus()
    data object Unavailable : NetworkStatus()
}

inline fun <Result> Flow<NetworkStatus>.map(
    crossinline onUnavailable: suspend () -> Result,
    crossinline onAvailable: suspend () -> Result,
): Flow<Result> = map { status ->
    when (status) {
        NetworkStatus.Unavailable -> onUnavailable()
        NetworkStatus.Available -> onAvailable()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
inline fun <Result> Flow<NetworkStatus>.flatMap(
    crossinline onUnavailable: suspend () -> Flow<Result>,
    crossinline onAvailable: suspend () -> Flow<Result>,
): Flow<Result> = flatMapConcat { status ->
    when (status) {
        NetworkStatus.Unavailable -> onUnavailable()
        NetworkStatus.Available -> onAvailable()
    }
}
