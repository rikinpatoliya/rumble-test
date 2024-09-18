package com.rumble.domain.report.model.datasource

import com.rumble.network.dto.channel.ReportContentType

interface ReportContentRemoteDataSource {

    suspend fun report(
        contentType: ReportContentType,
        contentId: Long,
        reason: String,
        comment: String = ""
    ): Boolean

}