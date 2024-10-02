package com.rumble.network.interceptors

import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import com.rumble.network.NetworkRumbleConstants.CAN_SUBMIT_LOGS_KEY
import com.rumble.network.NetworkRumbleConstants.CHAT_KEY
import com.rumble.network.NetworkRumbleConstants.COOKIES_HEADER
import com.rumble.network.NetworkRumbleConstants.DEBUG_KEY
import com.rumble.network.NetworkRumbleConstants.ENDPOINT_KEY
import com.rumble.network.NetworkRumbleConstants.EVENT_URL_KEY
import com.rumble.network.NetworkRumbleConstants.INTERVAL_KEY
import com.rumble.network.NetworkRumbleConstants.LIVE_PING_ENDPOINT_KEY
import com.rumble.network.NetworkRumbleConstants.LOGGED_IN_KEY
import com.rumble.network.NetworkRumbleConstants.RETRY_DELAY
import com.rumble.network.NetworkRumbleConstants.RETRY_NUMBER
import com.rumble.network.NetworkRumbleConstants.TIME_RANGE_INTERVAL_KEY
import com.rumble.network.NetworkRumbleConstants.URL_KEY
import com.rumble.network.NetworkRumbleConstants.USER_KEY
import com.rumble.network.NetworkRumbleConstants.WATCHING_NOW_KEY
import com.rumble.network.NetworkRumbleConstants.WATCH_PROGRESS_INTERVAL_KEY
import com.rumble.network.NetworkRumbleConstants.WATCH_TIME_KEY
import com.rumble.network.session.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import timber.log.Timber
import java.nio.charset.Charset
import javax.inject.Inject

class ResponseInterceptor @Inject constructor(private val sessionManager: SessionManager) :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val checkLoginState = shouldCheckLoginState(chain)
        return try {
            var response = chain.proceed(chain.request())
            var userState = extractUserState(response.body)
            if (checkLoginState) {
                val loggedIn = userState?.getBoolean(LOGGED_IN_KEY) ?: true
                if (loggedIn.not()) {
                    runBlocking {
                        sessionManager.saveUserCookies("")
                        response = retryRequest(chain)
                        userState = extractUserState(response.body)
                    }
                }
            }
            savePingData(userState)
            saveChatEndpoint(userState)
            saveCanSubmitLogs(userState)
            saveTimeRangeReportInfo(userState)
            saveReportEventInfo(userState)
            response
        } catch (e: Throwable) {
            val errorResponse = Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_2)
                .code(1001)
                .message(e.message ?: "An unknown error inside response interceptor")
                .body("$e".toResponseBody(null))
                .build()
            return chain.proceed(errorResponse.request)
        }
    }

    private fun shouldCheckLoginState(chain: Interceptor.Chain) =
        chain.request().headers[COOKIES_HEADER].isNullOrEmpty().not()

    private fun extractUserState(responseBody: ResponseBody?): JSONObject? {
        val source = responseBody?.source()
        source?.request(Long.MAX_VALUE)
        val buffer = source?.buffer?.clone()

        try {
            val jsonReader = JsonReader(buffer?.inputStream()?.reader(Charset.forName("UTF-8")))
            jsonReader.beginObject()

            while (jsonReader.hasNext()) {
                val key = jsonReader.nextName()
                if (key == USER_KEY) {
                    val userObjectString = JsonParser().parse(jsonReader).asJsonObject.toString()
                    Timber.d("userObjectString:$userObjectString")
                    return JSONObject(userObjectString)
                } else {
                    jsonReader.skipValue()
                }
            }

            jsonReader.endObject()
        } catch (e: Exception) {
            Timber.d("Unable to extract user state or no user object. ${e.message}")
        }

        return null
    }

    private fun savePingData(userState: JSONObject?) {
        userState?.let {
            if (it.has(WATCHING_NOW_KEY) && it.isNull(WATCHING_NOW_KEY).not()) {
                val watchNow = it.getJSONObject(WATCHING_NOW_KEY)
                val pingEndpoint = watchNow.getString(LIVE_PING_ENDPOINT_KEY)
                val pingInterval = watchNow.getInt(INTERVAL_KEY)
                runBlocking {
                    sessionManager.saveLivePingEndpoint(pingEndpoint)
                    sessionManager.saveLivePingInterval(pingInterval)
                }
            }
        }
    }

    private fun saveChatEndpoint(userState: JSONObject?) {
        userState?.let {
            val chatEndpointUpdatedForCurrentSession =
                runBlocking { sessionManager.chatEndpointUpdatedFlow.first() }
            if (chatEndpointUpdatedForCurrentSession.not()
                && it.has(CHAT_KEY)
                && it.isNull(CHAT_KEY).not()
            ) {
                val chatEndpoint = it.getJSONObject(CHAT_KEY).getString(ENDPOINT_KEY)
                runBlocking {
                    sessionManager.saveChatEndpointUpdateForCurrentSession(true)
                    sessionManager.saveChatEndpoint(chatEndpoint)
                }
            }
        }
    }

    private fun saveCanSubmitLogs(userState: JSONObject?) {
        userState?.let {
            if (it.has(DEBUG_KEY) && it.isNull(DEBUG_KEY).not()) {
                val debug = it.getJSONObject(DEBUG_KEY)
                if (debug.has(CAN_SUBMIT_LOGS_KEY)) {
                    runBlocking {
                        sessionManager.saveCanSubmitLogs(debug.getBoolean(CAN_SUBMIT_LOGS_KEY))
                    }
                }
            }
        }
    }

    private fun saveTimeRangeReportInfo(userState: JSONObject?) {
        userState?.let {
            if (it.has(WATCH_TIME_KEY) && it.isNull(WATCH_TIME_KEY).not()) {
                val watchTime = it.getJSONObject(WATCH_TIME_KEY)
                if (watchTime.has(ENDPOINT_KEY) && watchTime.has(TIME_RANGE_INTERVAL_KEY)) {
                    val endpoint = watchTime.getString(ENDPOINT_KEY)
                    val interval = watchTime.getInt(TIME_RANGE_INTERVAL_KEY)
                    runBlocking {
                        sessionManager.saveTimeRangeEndpoint(endpoint)
                        sessionManager.saveTimeRangeInterval(interval)
                    }
                }
            }
        }
    }

    private fun saveReportEventInfo(userState: JSONObject?) {
        userState?.let {
            if (it.has(EVENT_URL_KEY) && it.isNull(EVENT_URL_KEY).not()) {
                val eventUrlContainer = it.getJSONObject(EVENT_URL_KEY)
                if (eventUrlContainer.has(URL_KEY) && eventUrlContainer.isNull(URL_KEY).not()) {
                    val eventEndpoint = eventUrlContainer.getString(URL_KEY)
                    runBlocking {
                        sessionManager.saveEventEndpoint(eventEndpoint)
                    }
                }
            }
            if (it.has(WATCH_PROGRESS_INTERVAL_KEY) && it.isNull(WATCH_PROGRESS_INTERVAL_KEY).not()) {
                val watchProgressInterval = it.getInt(WATCH_PROGRESS_INTERVAL_KEY)
                runBlocking {
                    sessionManager.saveWatchProgressInterval(watchProgressInterval)
                }
            }
        }
    }

    private suspend fun retryRequest(chain: Interceptor.Chain): Response {
        var retryCount = 0
        while (sessionManager.cookiesFlow.first().isEmpty() && retryCount++ < RETRY_NUMBER
        ) {
            delay(RETRY_DELAY)
        }
        val cookies = runBlocking { sessionManager.cookiesFlow.first() }
        val newRequest = chain.request().newBuilder()
            .removeHeader(COOKIES_HEADER)
            .addHeader(COOKIES_HEADER, cookies)
            .build()
        return chain.proceed(newRequest)
    }
}