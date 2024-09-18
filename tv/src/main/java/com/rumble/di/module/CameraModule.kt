package com.rumble.di.module

import com.rumble.domain.camera.model.datasource.local.VideoDao
import com.rumble.domain.camera.model.datasource.remote.CameraRemoteDataSource
import com.rumble.domain.camera.model.datasource.remote.CameraRemoteDataSourceImpl
import com.rumble.domain.camera.model.repository.CameraRepository
import com.rumble.domain.camera.model.repository.CameraRepositoryImpl
import com.rumble.domain.database.RumbleDatabase
import com.rumble.domain.uploadmanager.UploadManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CameraModule {

    @Provides
    fun provideVideoDao(
        rumbleDatabase: RumbleDatabase
    ): VideoDao = rumbleDatabase.videoDao()

    @Provides
    @Singleton
    fun provideCameraRemoteDataSource(
        uploadManager: UploadManager
    ): CameraRemoteDataSource =
        CameraRemoteDataSourceImpl(
            uploadManager,
        )

    @Provides
    @Singleton
    fun provideCameraRepository(
        cameraRemoteDataSource: CameraRemoteDataSource,
        videoDao: VideoDao
    ): CameraRepository =
        CameraRepositoryImpl(
            cameraRemoteDataSource = cameraRemoteDataSource,
            dispatcher = Dispatchers.IO,
            videoDao = videoDao
        )
}