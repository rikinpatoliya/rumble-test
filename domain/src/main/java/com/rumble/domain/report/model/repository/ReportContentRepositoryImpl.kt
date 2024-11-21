package com.rumble.domain.report.model.repository

import com.rumble.domain.report.model.datasource.ReportContentRemoteDataSource
import com.rumble.network.dto.channel.ReportContentType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ReportContentRepositoryImpl(
    private val reportContentRemoteDataSource: ReportContentRemoteDataSource,
    private val dispatcher: CoroutineDispatcher
) : ReportContentRepository {
    override suspend fun report(
        contentType: ReportContentType,
        contentId: Long,
        reason: String,
        comment: String,
        isTest: Boolean,
    ): Boolean =
        withContext(dispatcher) {
            reportContentRemoteDataSource.report(
                contentType,
                contentId,
                reason,
                comment,
                isTest,
            )
        }
}