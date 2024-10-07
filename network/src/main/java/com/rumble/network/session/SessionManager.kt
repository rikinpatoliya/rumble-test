package com.rumble.network.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "SessionManager"

@Singleton
class SessionManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")
    private val cookiesKey = stringPreferencesKey("cookiesKey")
    private val userIdKey = stringPreferencesKey("userIdKey")
    private val userNameKey = stringPreferencesKey("userNameKey")
    private val userPictureKey = stringPreferencesKey("userPictureKey")
    private val loginTypeKey = intPreferencesKey("loginTypeKey")

    /**
     * @see subdomainKey doesn’t get reset when the user logs in and logs out with another account,
     * even if another account doesn’t have the right to set up a custom subdomain.
     */
    private val subdomainKey = stringPreferencesKey("subdomainKey")
    private val userInitiatedSubdomainKey = stringPreferencesKey("userInitiatedSubdomainKey")
    private val viewIdKey = stringPreferencesKey("viewIdKey")
    private val passwordKey = stringPreferencesKey("passwordKey")
    private val livePingEndpointKey = stringPreferencesKey("livePingEndpointKey")
    private val livePingIntervalKey = intPreferencesKey("livePingIntervalKey")
    private val chatEndpointKey = stringPreferencesKey("chatEndPointKey")
    private val chatEndpointUpdatedKey = booleanPreferencesKey("chatEndpointUpdatedKey")
    private val canSubmitLogsKey = booleanPreferencesKey("canSubmitLogsKey")
    private val timeRangeEndpointKey = stringPreferencesKey("timeRangeEndpointKey")
    private val timeRangeIntervalKey = intPreferencesKey("timeRangeIntervalKey")
    private val watchedTimeSinceLastAdKey = floatPreferencesKey("watchedTimeSinceLastAdKey")
    private val isPremiumUserKey = booleanPreferencesKey("isUserPremiumKey")
    private val eventEndpointKey = stringPreferencesKey("eventEndpointKey")
    private val watchProgressIntervalKey = intPreferencesKey("watchProgressIntervalKey")
    private val uniqueSessionKey = stringPreferencesKey("uniqueSessionKey")
    private val userGenderKey = stringPreferencesKey("userGenderKey")
    private val userAgeKey = intPreferencesKey("userAgeKey")
    private val allowContentLoadKey = booleanPreferencesKey("allowContentLoadKey")
    private val lastLoginPromptKey = longPreferencesKey("lastLoginPrompt")
    private val videoDetailsStateKey = booleanPreferencesKey("videoDetailsStateKey")
    private val videoDetailsCollapsedKey = booleanPreferencesKey("videoDetailsCollapsedKey")
    private val conversionLoggedKey = booleanPreferencesKey("conversionLoggedKey")

    val cookiesFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[cookiesKey] ?: ""
    }.catch {
        Timber.tag(TAG).e(it)
        emit("")
    }
    val userIdFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[userIdKey] ?: ""
    }.catch {
        Timber.tag(TAG).e(it)
        emit("")
    }
    val userNameFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[userNameKey] ?: ""
    }.catch {
        Timber.tag(TAG).e(it)
        emit("")
    }
    val userPictureFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[userPictureKey] ?: ""
    }.catch {
        Timber.tag(TAG).e(it)
        emit("")
    }
    val loginTypeFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[loginTypeKey] ?: 0
    }.catch {
        Timber.tag(TAG).e(it)
        emit(0)
    }
    val viewerIdFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[viewIdKey] ?: ""
    }.catch {
        Timber.tag(TAG).e(it)
        emit("")
    }
    val passwordFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[passwordKey] ?: ""
    }.catch {
        Timber.tag(TAG).e(it)
        emit("")
    }
    val livePingEndpointFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[livePingEndpointKey] ?: ""
    }.catch {
        Timber.tag(TAG).e(it)
        emit("")
    }
    val livePingIntervalFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[livePingIntervalKey] ?: 0
    }.catch {
        Timber.tag(TAG).e(it)
        emit(0)
    }
    val chatEndPointFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[chatEndpointKey] ?: ""
    }.catch {
        Timber.tag(TAG).e(it)
        emit("")
    }
    val chatEndpointUpdatedFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[chatEndpointUpdatedKey] ?: false
    }.catch {
        Timber.tag(TAG).e(it)
        emit(false)
    }
    val canSubmitLogs: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[canSubmitLogsKey] ?: false
    }.catch {
        Timber.tag(TAG).e(it)
        emit(false)
    }
    val timeRangeEndpoint: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[timeRangeEndpointKey] ?: ""
    }.catch {
        Timber.tag(TAG).e(it)
        emit("")
    }
    val timeRangeInterval: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[timeRangeIntervalKey] ?: 0
    }.catch {
        Timber.tag(TAG).e(it)
        emit(0)
    }
    val watchedTimeSinceLastAd: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[watchedTimeSinceLastAdKey] ?: 0f
    }.catch {
        Timber.tag(TAG).e(it)
        emit(0f)
    }
    val isPremiumUserFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[isPremiumUserKey] ?: false
    }.catch {
        Timber.tag(TAG).e(it)
        emit(false)
    }
    val eventEndpointFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[eventEndpointKey] ?: ""
    }.catch {
        Timber.tag(TAG).e(it)
        emit("")
    }
    val watchProgressIntervalFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[watchProgressIntervalKey] ?: 0
    }.catch {
        Timber.tag(TAG).e(it)
        emit(0)
    }
    val uniqueSession: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[uniqueSessionKey] ?: ""
    }.catch {
        Timber.tag(TAG).e(it)
        emit("")
    }
    val userGenderFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[userGenderKey] ?: ""
    }.catch {
        Timber.tag(TAG).e(it)
        emit("")
    }
    val userAgeFlow: Flow<Int?> = context.dataStore.data.map { prefs ->
        prefs[userAgeKey]
    }.catch {
        Timber.tag(TAG).e(it)
        emit(null)
    }
    val allowContentLoadFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[allowContentLoadKey] ?: true
    }.catch {
        Timber.tag(TAG).e(it)
        emit(value = false)
    }
    val lastLoginPromptTimeFlow: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[lastLoginPromptKey] ?: 0
    }.catch {
        Timber.tag(TAG).e(it)
        emit(value = 0)
    }
    val videDetailsOpenedFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[videoDetailsStateKey] ?: false
    }.catch {
        Timber.tag(TAG).e(it)
        emit(value = false)
    }
    val videoDetailsCollapsedFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[videoDetailsCollapsedKey] ?: false
    }.catch {
        Timber.tag(TAG).e(it)
        emit(value = false)
    }
    val conversionLoggedKeyFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[conversionLoggedKey] ?: false
    }.catch {
        Timber.tag(TAG).e(it)
        emit(value = false)
    }

    suspend fun saveWatchedTimeSinceLastAd(value: Float) {
        try {
            context.dataStore.edit { prefs ->
                prefs[watchedTimeSinceLastAdKey] = value
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveUserCookies(cookies: String) {
        try {
            context.dataStore.edit { prefs ->
                prefs[cookiesKey] = cookies
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveUserId(userId: String) {
        try {
            context.dataStore.edit { prefs ->
                prefs[userIdKey] = userId
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveUserName(userName: String) {
        try {
            context.dataStore.edit { prefs ->
                prefs[userNameKey] = userName
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun savePassword(password: String) {
        try {
            context.dataStore.edit { prefs ->
                prefs[passwordKey] = password
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveUserPicture(userPicture: String) {
        try {
            context.dataStore.edit { prefs ->
                prefs[userPictureKey] = userPicture
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveLoginType(loginType: Int) {
        try {
            context.dataStore.edit { prefs ->
                prefs[loginTypeKey] = loginType
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveSubdomain(subdomain: String) {
        try {
            context.dataStore.edit { prefs ->
                prefs[subdomainKey] = subdomain
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveUserInitiatedSubdomain(subdomain: String) {
        try {
            context.dataStore.edit { prefs ->
                prefs[userInitiatedSubdomainKey] = subdomain
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveViewerId(viewId: String) {
        try {
            context.dataStore.edit { prefs ->
                prefs[viewIdKey] = viewId
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveCanSubmitLogs(canSubmitLogs: Boolean) {
        try {
            context.dataStore.edit { prefs ->
                prefs[canSubmitLogsKey] = canSubmitLogs
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun isUserSignedIn(): Boolean {
        try {
            return cookiesFlow.first().isNotEmpty()
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
            return false
        }
    }

    suspend fun getAppSubdomain(): String? = try {
        context.dataStore.data.first()[subdomainKey]
    } catch (e: Exception) {
        Timber.tag(TAG).e(e)
        null
    }

    suspend fun getUserInitiatedSubdomain(): String? = try {
        context.dataStore.data.first()[userInitiatedSubdomainKey]
    } catch (e: Exception) {
        Timber.tag(TAG).e(e)
        null
    }

    suspend fun saveLivePingEndpoint(endpoint: String?) {
        try {
            endpoint?.let {
                if (context.dataStore.data.first()[livePingEndpointKey].isNullOrEmpty()) {
                    context.dataStore.edit { prefs ->
                        prefs[livePingEndpointKey] = it
                    }
                }
            } ?: run {
                context.dataStore.edit { prefs ->
                    prefs[livePingEndpointKey] = ""
                }
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveLivePingInterval(interval: Int?) {
        try {
            interval?.let {
                if (context.dataStore.data.first()[livePingIntervalKey] == 0 && it > 0) {
                    context.dataStore.edit { prefs ->
                        prefs[livePingIntervalKey] = it
                    }
                }
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveChatEndpoint(chatEndpoint: String?) {
        try {
            chatEndpoint?.let {
                val endpoint = context.dataStore.data.first()[chatEndpointKey]
                if (endpoint.isNullOrEmpty() || endpoint.contentEquals(chatEndpoint).not()) {
                    context.dataStore.edit { prefs ->
                        prefs[chatEndpointKey] = it
                    }
                }
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveChatEndpointUpdateForCurrentSession(updated: Boolean) {
        try {
            context.dataStore.edit { prefs ->
                prefs[chatEndpointUpdatedKey] = updated
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveTimeRangeEndpoint(endpoint: String) {
        try {
            context.dataStore.edit { prefs ->
                prefs[timeRangeEndpointKey] = endpoint
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveTimeRangeInterval(interval: Int) {
        try {
            context.dataStore.edit { prefs ->
                prefs[timeRangeIntervalKey] = interval
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun clearUserData() {
        try {
            context.dataStore.edit { prefs ->
                prefs[cookiesKey] = ""
                prefs[userIdKey] = ""
                prefs[userNameKey] = ""
                prefs[userPictureKey] = ""
                prefs[loginTypeKey] = 0
                prefs[isPremiumUserKey] = false
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveIsPremiumUser(isPremium: Boolean) {
        try {
            context.dataStore.edit { prefs ->
                prefs[isPremiumUserKey] = isPremium
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveEventEndpoint(eventEndpoint: String) {
        try {
            context.dataStore.edit { prefs ->
                prefs[eventEndpointKey] = eventEndpoint
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveWatchProgressInterval(interval: Int) {
        try {
            context.dataStore.edit { prefs ->
                prefs[watchProgressIntervalKey] = interval
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveUniqueSession(session: String) {
        try {
            context.dataStore.edit { prefs ->
                prefs[uniqueSessionKey] = session
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveUserGender(gender: String) {
        try {
            context.dataStore.edit { prefs ->
                prefs[userGenderKey] = gender
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveUserAge(age: Int) {
        try {
            context.dataStore.edit { prefs ->
                prefs[userAgeKey] = age
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun allowContentLoadFlow(allow: Boolean) {
        try {
            context.dataStore.edit { prefs ->
                prefs[allowContentLoadKey] = allow
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveLastLoginPromptTime(time: Long) {
        try {
            context.dataStore.edit { prefs ->
                prefs[lastLoginPromptKey] = time
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveVideoDetailsState(opened: Boolean) {
        try {
            context.dataStore.edit { prefs ->
                prefs[videoDetailsStateKey] = opened
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveVideoDetailsCollapsed(collapsed: Boolean) {
        try {
            context.dataStore.edit { prefs ->
                prefs[videoDetailsCollapsedKey] = collapsed
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveConversionLoggedState(isLogged: Boolean) {
        try {
            context.dataStore.edit { prefs ->
                prefs[conversionLoggedKey] = isLogged
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }
}