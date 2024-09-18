package com.rumble.domain.common.model.datasource

import com.rumble.domain.common.domain.domainmodel.EmptyResult

interface UserRemoteDataSource {
    suspend fun requestVerificationEmail(email: String): EmptyResult
}