package com.rumble.domain.landing.usecases

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.rumble.domain.landing.domainmodel.OldUser
import com.rumble.domain.settings.domain.domainmodel.BackgroundPlay
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.network.session.SessionManager
import com.rumble.utils.RumbleTransferConstant.CAR_MODE
import com.rumble.utils.RumbleTransferConstant.COOKIES_KEY
import com.rumble.utils.RumbleTransferConstant.SHARE_STORE_NAME
import com.rumble.utils.RumbleTransferConstant.SUBDOMAIN
import com.rumble.utils.RumbleTransferConstant.USER_KEY
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TransferUserDataUseCase @Inject constructor(
    private val sessionManager: SessionManager,
    private val userPreferenceManager: UserPreferenceManager,
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke() {
        val oldPreferences =
            context.getSharedPreferences(SHARE_STORE_NAME, Context.MODE_PRIVATE)
        transferCookies(oldPreferences)
        transferUserData(oldPreferences)
        transferSubdomain(oldPreferences)
    }

    private suspend fun transferCookies(sharedPreferences: SharedPreferences) {
        val oldCoolies = sharedPreferences.getString(COOKIES_KEY, "")
        oldCoolies?.let {
            if (it.isNotEmpty()) {
                sessionManager.saveUserCookies(it)
                sharedPreferences.edit().putString(COOKIES_KEY, "").apply()
            }
        }
    }

    private suspend fun transferUserData(sharedPreferences: SharedPreferences) {
        sharedPreferences.getString(USER_KEY, null)?.let { jsonString ->
            Gson().fromJson(jsonString, OldUser::class.java)?.let { user ->
                user.pictureUrl?.let { picture -> sessionManager.saveUserPicture(picture) }
                user.username?.let { name -> sessionManager.saveUserName(name) }
                user.password?.let { password -> sessionManager.savePassword(password) }
            }
            if (sharedPreferences.getBoolean(CAR_MODE, false)) {
                userPreferenceManager.saveBackgroundPlay(BackgroundPlay.SOUND)
            }
        }
        sharedPreferences.edit().putString(USER_KEY, null).apply()
    }

    private suspend fun transferSubdomain(sharedPreferences: SharedPreferences) {
        sharedPreferences.getString(SUBDOMAIN, null)?.let {
            sessionManager.saveSubdomain(it)
        }
        sharedPreferences.edit().putString(SUBDOMAIN, null).apply()
    }
}