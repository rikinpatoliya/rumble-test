package com.rumble.domain.landing.usecases

import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import javax.inject.Inject

private const val TAG = "IsVersionNameGreaterUseCase"

class IsVersionNameGreaterUseCase @Inject constructor(
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
) {

    operator fun invoke(version1: String, version2: String): Boolean {
        try {
            val parts1 = version1.split(".").map { it.toInt() }
            val parts2 = version2.split(".").map { it.toInt() }

            for (i in 0 until maxOf(parts1.size, parts2.size)) {
                val v1 = parts1.getOrElse(i) { 0 }
                val v2 = parts2.getOrElse(i) { 0 }

                if (v1 > v2) {
                    return true
                } else if (v1 < v2) {
                    return false
                }
            }

            return false
        } catch (e: Exception) {
            unhandledErrorUseCase(TAG, e)
            return false
        }
    }
}