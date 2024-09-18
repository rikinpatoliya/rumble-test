package com.rumble.domain.earnings.datasource

import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.earnings.domainmodel.EarningsResult
import com.rumble.domain.feed.model.getEarnings
import com.rumble.network.api.UserApi

private const val TAG = "EarningsRemoteDataSourceImpl"

class EarningsRemoteDataSourceImpl(
    private val userApi: UserApi
) : EarningsRemoteDataSource {

    override suspend fun fetchEarnings(): EarningsResult {
        val response = userApi.fetchEarnings()
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            EarningsResult.Success(body.getEarnings())
        } else {
            EarningsResult.Failure(
                rumbleError = RumbleError(
                    tag = TAG,
                    response = response.raw()
                )
            )
        }
    }
}