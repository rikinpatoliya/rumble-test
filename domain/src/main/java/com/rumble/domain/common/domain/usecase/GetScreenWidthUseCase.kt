package com.rumble.domain.common.domain.usecase

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetScreenWidthUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {

    operator fun invoke(): Int {
        val metrics = context.resources.displayMetrics
        return (metrics.widthPixels / metrics.density).toInt()
    }
}