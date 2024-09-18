package com.rumble.domain.channels.channeldetails.domain.usecase

import android.content.Context
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelType
import com.rumble.domain.report.model.repository.ReportContentRepository
import com.rumble.network.dto.channel.ReportContentType
import com.rumble.videoplayer.player.config.ReportType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ReportChannelUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val reportContentRepository: ReportContentRepository
) {

    suspend operator fun invoke(
        channelDetailsEntity: ChannelDetailsEntity,
        reportType: ReportType
    ): Boolean {
        val type = when (channelDetailsEntity.type) {
            ChannelType.CHANNEL -> ReportContentType.CHANNEL
            ChannelType.MEDIA -> ReportContentType.CHANNEL
            ChannelType.USER -> ReportContentType.USER
        }

        return reportContentRepository.report(
            contentType = type,
            contentId = stringIdToLong(channelDetailsEntity.channelId),
            reason = context.getString(reportType.value),
        )
    }

    // Prefixes consist of an `_` followed by a letter
    private fun stringIdToLong(stringId: String): Long {
        return if (stringId.startsWith("_")) { // The prefix exists
            val numberPart = stringId.substring(2) // cut out the '_' and one letter
            if (stringId.startsWith("_u")) {
                numberPart.toLong(36)
            } else {
                numberPart.toLong()
            }

        } else { // There is no prefix, just convert the string
            stringId.toLong()
        }
    }
}