package com.rumble.domain.referrals.model

import com.rumble.domain.feed.model.getReferrals
import com.rumble.domain.referrals.domain.domainmodel.ReferralDetailsEntity
import com.rumble.network.api.UserApi;

class ReferralsRepositoryImpl(
    private val userApi: UserApi
) : ReferralsRepository {

    override suspend fun getReferrals(): Result<ReferralDetailsEntity> {
        val response = userApi.getReferrals()
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            Result.success(body.referralsData.getReferrals())
        } else {
            Result.failure(RuntimeException("getReferrals failed"))
        }
    }

}