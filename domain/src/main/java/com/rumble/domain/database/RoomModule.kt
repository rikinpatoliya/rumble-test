package com.rumble.domain.database

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application): RumbleDatabase =
        Room.databaseBuilder(
            application.applicationContext,
            RumbleDatabase::class.java,
            "rumble-database"
        ).build()

}