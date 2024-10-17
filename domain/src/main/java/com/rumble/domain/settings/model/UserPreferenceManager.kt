package com.rumble.domain.settings.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rumble.domain.settings.domain.domainmodel.DebugAdType
import com.rumble.domain.settings.domain.domainmodel.BackgroundPlay
import com.rumble.domain.settings.domain.domainmodel.ColorMode
import com.rumble.domain.settings.domain.domainmodel.ListToggleViewStyle
import com.rumble.domain.settings.domain.domainmodel.PlaybackInFeedsMode
import com.rumble.domain.settings.domain.domainmodel.UploadQuality
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "UserPreferenceManager"

@Singleton
class UserPreferenceManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")
    private val backgroundPlayKey = intPreferencesKey("backgroundPlayKey")
    private val uploadOverWifiKey = booleanPreferencesKey("uploadOverWifiKey")
    private val uploadQualityKey = intPreferencesKey("uploadQualityKey")
    private val colorModeKey = intPreferencesKey("colorModeKey")
    private val channelDetailsListToggleViewStyleKey =
        intPreferencesKey("channelDetailsListToggleViewStyleKey")
    private val myVideosListToggleViewStyleKey = intPreferencesKey("myVideosListToggleViewStyleKey")
    private val videosListToggleViewStyleKey = intPreferencesKey("videosListToggleViewStyleKey")
    private val videoResolutionKey = intPreferencesKey("videoQualityKey")
    private val videoBitrateKey = intPreferencesKey("videoBitrateKey")
    private val videoCardSoundKey = booleanPreferencesKey("videoCardSoundKey")
    private val playbackInFeedKey = intPreferencesKey("playbackInFeedKey")
    private val liveVideoUseAutoKey = booleanPreferencesKey("liveVideoUseAutoKey")
    private val cameraPermissionRequestDeniedKey = booleanPreferencesKey("cameraPermissionRequestDeniedKey")
    private val autoplayOnKey = booleanPreferencesKey("autoplayOnKey")
    private val displayPremiumBannerKey = booleanPreferencesKey("displayPremiumBannerKey")
    private val lastPremiumPromoTimeStampKey = longPreferencesKey("lastPremiumPromoTimeStampKey")
    private val lastNewVersionDisplayTimeStampKey = longPreferencesKey("lastNewVersionDisplayTimeStampKey")
    private val disableAdsKey = booleanPreferencesKey("disableAdsKey")
    private val forceAdsKey = booleanPreferencesKey("forceAdsKey")
    private val debugAdTypeKey = intPreferencesKey("debugAdTypeKey")
    private val customAdTagKey = stringPreferencesKey("customAdTagKey")
    private val uitTestingModeKey = booleanPreferencesKey("uitTestingModeKey")

    val backgroundPlayFlow: Flow<BackgroundPlay> = context.dataStore.data
        .map { prefs ->
            val intValue: Int = prefs[backgroundPlayKey] ?: BackgroundPlay.SOUND.value
            BackgroundPlay.getByValue(intValue)
        }
        .catch {
            Timber.tag(TAG).e(it)
            emit(BackgroundPlay.getByValue(BackgroundPlay.SOUND.value))
        }
    val uploadOverWifiFLow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[uploadOverWifiKey] ?: false }
        .catch {
            Timber.tag(TAG).e(it)
            emit(false)
        }
    val uploadQualityFlow: Flow<UploadQuality> = context.dataStore.data
        .map { prefs ->
            val intValue: Int = prefs[uploadQualityKey] ?: UploadQuality.defaultUploadQuality.value
            UploadQuality.getByValue(intValue)
        }
        .catch {
            Timber.tag(TAG).e(it)
            emit(UploadQuality.getByValue(UploadQuality.defaultUploadQuality.value))
        }
    val colorMode: Flow<ColorMode> = context.dataStore.data
        .map { prefs ->
            val intValue: Int = prefs[colorModeKey] ?: ColorMode.SYSTEM_DEFAULT.value
            ColorMode.getByValue(intValue)
        }
        .catch {
            Timber.tag(TAG).e(it)
            emit(ColorMode.getByValue(ColorMode.SYSTEM_DEFAULT.value))
        }
    val channelDetailsListToggleViewStyle: Flow<ListToggleViewStyle> = context.dataStore.data
        .map { prefs ->
            val intValue: Int =
                prefs[channelDetailsListToggleViewStyleKey] ?: ListToggleViewStyle.GRID.value
            ListToggleViewStyle.getByValue(intValue)
        }
        .catch {
            Timber.tag(TAG).e(it)
            emit(ListToggleViewStyle.getByValue(ListToggleViewStyle.GRID.value))
        }
    val myVideosListToggleViewStyle: Flow<ListToggleViewStyle> = context.dataStore.data
        .map { prefs ->
            val intValue: Int =
                prefs[myVideosListToggleViewStyleKey] ?: ListToggleViewStyle.GRID.value
            ListToggleViewStyle.getByValue(intValue)
        }
        .catch {
            Timber.tag(TAG).e(it)
            emit(ListToggleViewStyle.getByValue(ListToggleViewStyle.GRID.value))
        }
    val videosListToggleViewStyle: Flow<ListToggleViewStyle> = context.dataStore.data
        .map { prefs ->
            val intValue: Int = prefs[videosListToggleViewStyleKey]
                ?: ListToggleViewStyle.GRID.value
            ListToggleViewStyle.getByValue(intValue)
        }
        .catch {
            Timber.tag(TAG).e(it)
            emit(ListToggleViewStyle.getByValue(ListToggleViewStyle.GRID.value))
        }
    val videoQuality: Flow<Int?> = context.dataStore.data
        .map { prefs -> prefs[videoResolutionKey] }
        .catch {
            Timber.tag(TAG).e(it)
            emit(null)
        }
    val videoBitrate: Flow<Int?> = context.dataStore.data
        .map { prefs -> prefs[videoBitrateKey] }
        .catch {
            Timber.tag(TAG).e(it)
            emit(null)
        }
    val videoCardSoundStateFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[videoCardSoundKey] ?: false }
        .catch {
            Timber.tag(TAG).e(it)
            emit(false)
        }
    val playbackInFeedsModeModeFlow: Flow<PlaybackInFeedsMode> = context.dataStore.data
        .map { prefs ->
            PlaybackInFeedsMode.getByValue(prefs[playbackInFeedKey]
                ?: PlaybackInFeedsMode.ALWAYS_ON.value)
        }
        .catch {
            Timber.tag(TAG).e(it)
            emit(PlaybackInFeedsMode.getByValue(PlaybackInFeedsMode.ALWAYS_ON.value))
        }
    val liveVideoUseAutoFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[liveVideoUseAutoKey] ?: true }
        .catch {
            Timber.tag(TAG).e(it)
            emit(true)
        }
    val cameraPermissionRequestDenied: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[cameraPermissionRequestDeniedKey] ?: false }
        .catch {
            Timber.tag(TAG).e(it)
            emit(false)
        }
    val autoplayFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[autoplayOnKey] ?: true }
        .catch {
            Timber.tag(TAG).e(it)
            emit(true)
        }
    val displayPremiumBannerFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[displayPremiumBannerKey] ?: true }
        .catch {
            Timber.tag(TAG).e(it)
            emit(true)
        }
    val lastPremiumPromoTimeStampFlow: Flow<Long> = context.dataStore.data
        .map { prefs -> prefs[lastPremiumPromoTimeStampKey] ?: 0 }
        .catch {
            Timber.tag(TAG).e(it)
            emit(0)
        }
    val lastNewVersionDisplayTimeStampFlow: Flow<Long> = context.dataStore.data
        .map { prefs -> prefs[lastNewVersionDisplayTimeStampKey] ?: 0 }
        .catch {
            Timber.tag(TAG).e(it)
            emit(0)
        }
    val disableAdsFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[disableAdsKey] ?: false
    }.catch {
        Timber.tag(TAG).e(it)
        emit(false)
    }
    val forceAdsFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[forceAdsKey] ?: false
    }.catch {
        Timber.tag(TAG).e(it)
        emit(value = false)
    }
    val debugAdTypeFlow: Flow<DebugAdType> = context.dataStore.data
        .map { prefs ->
            val intValue: Int = prefs[debugAdTypeKey] ?: DebugAdType.REAL_AD.value
            DebugAdType.getByValue(intValue)
        }
        .catch {
            Timber.tag(TAG).e(it)
            emit(DebugAdType.getByValue(DebugAdType.REAL_AD.value))
        }
    val customAdTagFlow: Flow<String> = context.dataStore.data.map { prefs ->
            prefs[customAdTagKey] ?: ""
        }
        .catch {
            Timber.tag(TAG).e(it)
            emit("")
        }
    val uitTestingModeFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[uitTestingModeKey] ?: false }
        .catch {
            Timber.tag(TAG).e(it)
            emit(false)
        }

    suspend fun saveBackgroundPlay(backgroundPlay: BackgroundPlay) {
        try {
            context.dataStore.edit { prefs ->
                prefs[backgroundPlayKey] = backgroundPlay.value
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveUploadOverWifi(value: Boolean) {
        try {
            context.dataStore.edit { prefs ->
                prefs[uploadOverWifiKey] = value
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveUploadQuality(uploadQuality: UploadQuality) {
        try {
            context.dataStore.edit { prefs ->
                prefs[uploadQualityKey] = uploadQuality.value
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveColorMode(colorMode: ColorMode) {
        try {
            context.dataStore.edit { prefs ->
                prefs[colorModeKey] = colorMode.value
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveChannelDetailsListToggleViewStyle(listToggleViewStyle: ListToggleViewStyle) {
        try {
            context.dataStore.edit { prefs ->
                prefs[channelDetailsListToggleViewStyleKey] = listToggleViewStyle.value
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveMyVideosListToggleViewStyle(listToggleViewStyle: ListToggleViewStyle) {
        try {
            context.dataStore.edit { prefs ->
                prefs[myVideosListToggleViewStyleKey] = listToggleViewStyle.value
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveVideosListToggleViewStyle(listToggleViewStyle: ListToggleViewStyle) {
        try {
            context.dataStore.edit { prefs ->
                prefs[videosListToggleViewStyleKey] = listToggleViewStyle.value
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveSelectedVideoResolution(videoQuality: Int) {
        try {
            context.dataStore.edit { prefs ->
                prefs[videoResolutionKey] = videoQuality
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveSelectedVideoBitrate(bitrate: Int) {
        try {
            context.dataStore.edit { prefs ->
                prefs[videoBitrateKey] = bitrate
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveVideoCardSoundState(enable: Boolean) {
        try {
            context.dataStore.edit { prefs ->
                prefs[videoCardSoundKey] = enable
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun savePlaybackInFeedsMode(playbackInFeedsMode: PlaybackInFeedsMode) {
        try {
            context.dataStore.edit { prefs ->
                prefs[playbackInFeedKey] = playbackInFeedsMode.value
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveLiveVideoAutoMode(useAuto: Boolean) {
        try {
            context.dataStore.edit { prefs ->
                prefs[liveVideoUseAutoKey] = useAuto
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveCameraPermissionRequestDenied(denied: Boolean) {
        try {
            context.dataStore.edit { prefs ->
                prefs[cameraPermissionRequestDeniedKey] = denied
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveAutoplayOn(on: Boolean) {
        try {
            context.dataStore.edit { prefs ->
                prefs[autoplayOnKey] = on
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun setDisplayPremiumBanner(display: Boolean) {
        try {
            context.dataStore.edit { prefs ->
                prefs[displayPremiumBannerKey] = display
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveLastPremiumPromoTimeStamp(timestamp: Long) {
        try {
            context.dataStore.edit { prefs ->
                prefs[lastPremiumPromoTimeStampKey] = timestamp
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveNewVersionDisplayTimeStamp(timestamp: Long) {
        try {
            context.dataStore.edit { prefs ->
                prefs[lastNewVersionDisplayTimeStampKey] = timestamp
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveDisableAdsMode(useDebugMode: Boolean) {
        try {
            context.dataStore.edit { prefs ->
                prefs[disableAdsKey] = useDebugMode
                if (useDebugMode) {
                    prefs[forceAdsKey] = false
                    prefs[debugAdTypeKey] = DebugAdType.REAL_AD.value
                }
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveForceAds(forceAds: Boolean) {
        try {
            context.dataStore.edit { prefs ->
                prefs[forceAdsKey] = forceAds
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveDebugAdType(debugAdType: DebugAdType) {
        try {
            context.dataStore.edit { prefs ->
                prefs[debugAdTypeKey] = debugAdType.value
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveCustomAdTag(customAdTag: String) {
        try {
            context.dataStore.edit { prefs ->
                prefs[customAdTagKey] = customAdTag
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    suspend fun saveUitTestingMode(isUitTestingMode: Boolean) {
        try {
            context.dataStore.edit { prefs ->
                prefs[uitTestingModeKey] = isUitTestingMode
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }
}