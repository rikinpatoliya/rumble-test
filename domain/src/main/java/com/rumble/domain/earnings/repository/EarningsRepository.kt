package com.rumble.domain.earnings.repository

import com.rumble.domain.earnings.domainmodel.EarningsResult

interface EarningsRepository {
    suspend fun fetchEarnings(): EarningsResult
}