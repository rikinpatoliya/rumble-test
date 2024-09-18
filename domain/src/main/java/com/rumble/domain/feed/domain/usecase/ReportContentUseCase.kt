package com.rumble.domain.feed.domain.usecase

import android.content.Context
import com.rumble.domain.report.model.repository.ReportContentRepository
import com.rumble.network.dto.channel.ReportContentType
import com.rumble.videoplayer.player.config.ReportType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ReportContentUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val reportContentRepository: ReportContentRepository
) {
    suspend operator fun invoke(
        contentId: Long,
        reportType: ReportType,
        contentReportType: ReportContentType,
    ): Boolean {
        return reportContentRepository.report(
            contentType = contentReportType,
            contentId = contentId,
            reason = context.getString(reportType.value),
        )
    }
}