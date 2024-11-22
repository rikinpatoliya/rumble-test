package com.rumble.battles.common

import com.appsflyer.AppsFlyerConversionListener
import com.rumble.analytics.ConversionFailedEvent
import com.rumble.analytics.ConversionNonOrganicEvent
import com.rumble.analytics.ConversionOrganicEvent
import com.rumble.domain.analytics.domain.usecases.LogConversionUseCase
import com.rumble.utils.RumbleConstants
import javax.inject.Inject

class RumbleAppsFlyerConversionListener @Inject constructor(
    private val logConversionUseCase: LogConversionUseCase,
) : AppsFlyerConversionListener {
    override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
        // check for conversion event and it's type and log it
        data?.get(RumbleConstants.CONVERSION_TYPE_KEY)?.let {
            when (it) {
                RumbleConstants.ORGANIC_CONVERSION -> {
                    logConversionUseCase.invoke(ConversionOrganicEvent)
                }

                RumbleConstants.NON_ORGANIC_CONVERSION -> {
                    logConversionUseCase.invoke(ConversionNonOrganicEvent)
                }
            }
        }
    }

    override fun onConversionDataFail(error: String?) {
        // log failed conversion event
        logConversionUseCase.invoke(ConversionFailedEvent)
    }

    override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
        // Must be overriden to satisfy the AppsFlyerConversionListener interface.
    }

    override fun onAttributionFailure(error: String?) {
        // Must be overriden to satisfy the AppsFlyerConversionListener interface.
    }
}