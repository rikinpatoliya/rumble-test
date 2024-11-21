package com.rumble.domain.report.model.repository

import com.rumble.network.dto.channel.ReportContentType

interface ReportContentRepository {
    suspend fun report(
        contentType: ReportContentType,
        contentId: Long,
        reason: String,
        comment: String = "",
        isTest: Boolean,
    ): Boolean
}