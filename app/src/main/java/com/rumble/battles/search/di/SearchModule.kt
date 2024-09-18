package com.rumble.battles.search.di

import com.rumble.domain.database.RumbleDatabase
import com.rumble.domain.search.model.datasource.local.QueryDao
import com.rumble.domain.search.model.repository.SearchRepository
import com.rumble.domain.search.model.repository.SearchRepositoryImpl
import com.rumble.network.api.SearchApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchModule {

    @Provides
    fun provideQueryDao(database: RumbleDatabase): QueryDao = database.queryDao()

    @Provides
    @Singleton
    fun provideSearchRepository(
        queryDao: QueryDao,
        searchApi: SearchApi,
    ): SearchRepository =
        SearchRepositoryImpl(
            queryDao = queryDao,
            searchApi = searchApi,
            dispatcher = Dispatchers.IO,
        )
}