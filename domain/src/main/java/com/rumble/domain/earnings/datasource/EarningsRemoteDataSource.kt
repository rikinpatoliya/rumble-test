package com.rumble.domain.earnings.datasource

import com.rumble.domain.earnings.domainmodel.EarningsResult

interface EarningsRemoteDataSource {
    suspend fun fetchEarnings(): EarningsResult
}