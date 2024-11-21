package com.rumble.domain.report.model.datasource

import com.rumble.network.api.ReportApi
import com.rumble.network.dto.channel.ReportContentType
import com.rumble.network.dto.channel.ReportData
import com.rumble.network.dto.channel.ReportRequest

class ReportContentRemoteDataSourceImpl(private val reportApi: ReportApi) :
    ReportContentRemoteDataSource {

    override suspend fun report(
        contentType: ReportContentType,
        contentId: Long,
        reason: String,
        comment: String,
        isTest: Boolean,
    ): Boolean {
        val data = ReportData(
            isTest = isTest,
            contentType = contentType,
            contentId = contentId,
            reason = reason
        )

        val response = reportApi.report(ReportRequest(data))

        return response.isSuccessful
    }

}