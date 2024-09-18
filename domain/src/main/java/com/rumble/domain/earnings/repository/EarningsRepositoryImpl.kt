package com.rumble.domain.earnings.repository

import com.rumble.domain.earnings.datasource.EarningsRemoteDataSource
import com.rumble.domain.earnings.domainmodel.EarningsResult

class EarningsRepositoryImpl(
    private val earningsRemoteDataSource: EarningsRemoteDataSource
) : EarningsRepository {
    override suspend fun fetchEarnings(): EarningsResult =
        earningsRemoteDataSource.fetchEarnings()
}