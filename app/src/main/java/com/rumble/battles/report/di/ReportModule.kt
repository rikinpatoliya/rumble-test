package com.rumble.battles.report.di

import com.rumble.domain.report.model.datasource.ReportContentRemoteDataSource
import com.rumble.domain.report.model.datasource.ReportContentRemoteDataSourceImpl
import com.rumble.domain.report.model.repository.ReportContentRepository
import com.rumble.domain.report.model.repository.ReportContentRepositoryImpl
import com.rumble.network.api.ReportApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ReportModule {
    @Provides
    @Singleton
    fun provideReportContentRemoteDataSource(
        reportApi: ReportApi
    ): ReportContentRemoteDataSource =
        ReportContentRemoteDataSourceImpl(
            reportApi = reportApi
        )

    @Provides
    @Singleton
    fun provideReportContentRepository(
        reportContentRemoteDataSource: ReportContentRemoteDataSource
    ): ReportContentRepository =
        ReportContentRepositoryImpl(
            reportContentRemoteDataSource = reportContentRemoteDataSource,
            dispatcher = Dispatchers.IO
        )
}