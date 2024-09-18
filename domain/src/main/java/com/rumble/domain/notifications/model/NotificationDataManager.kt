package com.rumble.domain.notifications.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "NotificationDataManager"

@Singleton
class NotificationDataManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "notification_data")

    private val deepLinkChannelIdKey = stringPreferencesKey("deepLinkChannelIdKey")
    private val showPremiumMenuKey = booleanPreferencesKey("showPremiumMenuKey")

    val deepLinkChannelId: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[deepLinkChannelIdKey] ?: ""
    }.catch {
        Timber.tag(TAG).e(it)
        emit("")
    }
    val showPremiumMenu: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[showPremiumMenuKey] ?: false
    }.catch {
        Timber.tag(TAG).e(it)
        emit(false)
    }

    suspend fun saveDeepLinkChannelId(channelId: String) {
        try {
            context.dataStore.edit { prefs ->
                prefs[deepLinkChannelIdKey] = channelId
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun setShowPremiumMenu(show: Boolean) {
        try {
            context.dataStore.edit { prefs ->
                prefs[showPremiumMenuKey] = show
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }
}