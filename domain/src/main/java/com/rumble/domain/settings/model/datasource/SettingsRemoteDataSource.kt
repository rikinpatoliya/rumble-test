package com.rumble.domain.settings.model.datasource

import com.rumble.domain.login.domain.domainmodel.LoginType
import com.rumble.domain.settings.domain.domainmodel.*

interface SettingsRemoteDataSource {

    suspend fun fetchNotificationSettings(): NotificationSettingsResult

    suspend fun fetchAuthProviders(): AuthProvidersResult

    suspend fun unlinkAuthProvider(loginType: LoginType): Boolean

    suspend fun updateNotificationSettings(notificationSettingsEntity: NotificationSettingsEntity): NotificationSettingsResult

    suspend fun updateEmail(email: String, password: String): UpdateUserDetailsResult

    suspend fun updatePassword(newPassword: String, currentPassword: String): UpdateUserDetailsResult

    suspend fun closeAccount(): CloseAccountResult
    suspend fun fetchCanSubmitLogs(): CanSubmitLogsResult
}