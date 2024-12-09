package com.rumble.di.module

import android.content.Context
import android.view.inputmethod.InputMethodManager
import com.rumble.BuildConfig
import com.rumble.domain.channels.model.datasource.ChannelRemoteDataSource
import com.rumble.domain.channels.model.datasource.ChannelRemoteDataSourceImpl
import com.rumble.domain.channels.model.datasource.local.ChannelFollowDao
import com.rumble.domain.channels.model.repository.ChannelRepository
import com.rumble.domain.channels.model.repository.ChannelRepositoryImpl
import com.rumble.domain.common.domain.domainmodel.DeviceType
import com.rumble.domain.common.model.datasource.UserRemoteDataSource
import com.rumble.domain.common.model.datasource.UserRemoteDataSourceImpl
import com.rumble.domain.common.model.datasource.VideoRemoteDataSource
import com.rumble.domain.common.model.datasource.VideoRemoteDataSourceImpl
import com.rumble.domain.database.RumbleDatabase
import com.rumble.domain.feed.model.datasource.local.ChannelViewDao
import com.rumble.domain.feed.model.datasource.local.HomeCategoryViewDao
import com.rumble.domain.feed.model.datasource.local.RoomChannelView
import com.rumble.domain.feed.model.datasource.local.RoomVideoCollectionView
import com.rumble.domain.feed.model.datasource.local.RoomVideoCollectionViewCount
import com.rumble.domain.feed.model.datasource.remote.CommentRemoteDataSource
import com.rumble.domain.feed.model.datasource.remote.CommentRemoteDataSourceImpl
import com.rumble.domain.feed.model.datasource.remote.LiveVideoPlaylistDataSource
import com.rumble.domain.feed.model.datasource.remote.LiveVideoPlaylistDataSourceImpl
import com.rumble.domain.feed.model.repository.FeedRepository
import com.rumble.domain.feed.model.repository.FeedRepositoryImpl
import com.rumble.domain.library.model.datasource.PlayListRemoteDataSource
import com.rumble.domain.library.model.datasource.PlayListRemoteDataSourceImpl
import com.rumble.domain.library.model.repository.PlayListRepository
import com.rumble.domain.library.model.repository.PlayListRepositoryImpl
import com.rumble.domain.livechat.model.repository.RecentEmoteRepository
import com.rumble.domain.livechat.model.repository.RecentEmoteRepositoryImpl
import com.rumble.domain.login.model.LoginRepository
import com.rumble.domain.login.model.LoginRepositoryImpl
import com.rumble.domain.login.model.datasource.LoginRemoteDataSource
import com.rumble.domain.login.model.datasource.LoginRemoteDataSourceImpl
import com.rumble.domain.profile.domainmodel.CountryEntity
import com.rumble.domain.profile.model.datasource.UserProfileLocalDataSource
import com.rumble.domain.profile.model.datasource.UserProfileRemoteDataSource
import com.rumble.domain.profile.model.datasource.UserProfileRemoteDataSourceImpl
import com.rumble.domain.profile.model.repository.ProfileRepository
import com.rumble.domain.profile.model.repository.ProfileRepositoryImpl
import com.rumble.domain.report.model.datasource.ReportContentRemoteDataSource
import com.rumble.domain.report.model.datasource.ReportContentRemoteDataSourceImpl
import com.rumble.domain.report.model.repository.ReportContentRepository
import com.rumble.domain.report.model.repository.ReportContentRepositoryImpl
import com.rumble.domain.rumbleads.model.datasource.RumbleAdsRemoteDataSource
import com.rumble.domain.rumbleads.model.datasource.RumbleAdsRemoteDataSourceImpl
import com.rumble.domain.rumbleads.model.repository.RumbleAdRepository
import com.rumble.domain.rumbleads.model.repository.RumbleAdRepositoryImpl
import com.rumble.domain.search.model.datasource.local.QueryDao
import com.rumble.domain.search.model.repository.SearchRepository
import com.rumble.domain.search.model.repository.SearchRepositoryImpl
import com.rumble.domain.settings.model.datasource.SettingsLocalDataSource
import com.rumble.domain.settings.model.datasource.SettingsLocalDataSourceImpl
import com.rumble.domain.settings.model.datasource.SettingsRemoteDataSource
import com.rumble.domain.settings.model.datasource.SettingsRemoteDataSourceImpl
import com.rumble.domain.settings.model.repository.SettingsRepository
import com.rumble.domain.settings.model.repository.SettingsRepositoryImpl
import com.rumble.domain.uploadmanager.UploadManager
import com.rumble.domain.videolist.model.datasource.VideoListRemoteDataSource
import com.rumble.domain.videolist.model.datasource.VideoListRemoteDataSourceImpl
import com.rumble.domain.videolist.model.repository.VideoListRepository
import com.rumble.domain.videolist.model.repository.VideoListRepositoryImpl
import com.rumble.network.api.ChannelApi
import com.rumble.network.api.LoginApi
import com.rumble.network.api.PreRollApi
import com.rumble.network.api.ReportApi
import com.rumble.network.api.RepostApi
import com.rumble.network.api.RumbleAdsApi
import com.rumble.network.api.RumbleBannerApi
import com.rumble.network.api.SearchApi
import com.rumble.network.api.UserApi
import com.rumble.network.api.VideoApi
import com.rumble.network.dto.livechat.ErrorResponse
import com.rumble.network.dto.login.RegisterErrorResponse
import com.rumble.utils.HashCalculator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.ResponseBody
import retrofit2.Converter
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class TvSingletonModule {

    @Singleton
    @Provides
    fun provideInputMethodManager(@ApplicationContext context: Context): InputMethodManager {
        return context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    @Provides
    fun provideQueryDao(database: RumbleDatabase): QueryDao = database.queryDao()

    @Provides
    @Singleton
    fun provideSearchRepository(searchApi: SearchApi, queryDao: QueryDao): SearchRepository =
        SearchRepositoryImpl(queryDao = queryDao, searchApi = searchApi, dispatcher = Dispatchers.IO)

    @Provides
    @Singleton
    fun provideChannelViewDao(): ChannelViewDao = object : ChannelViewDao {
        override suspend fun save(roomChannelView: RoomChannelView) {
            TODO("Not yet implemented")
        }

        override suspend fun getByChannelId(channelId: String): RoomChannelView? {
            TODO("Not yet implemented")
        }
    }

    @Provides
    fun provideChannelFollowDao(database: RumbleDatabase): ChannelFollowDao =
        database.channelFollowDao()

    @Provides
    fun provideCommentsRemoteDataSource(videoApi: VideoApi): CommentRemoteDataSource =
        CommentRemoteDataSourceImpl(videoApi)

    @Provides
    fun provideVideoRemoteDataSource(videoApi: VideoApi, errorConverter: Converter<ResponseBody, ErrorResponse>?): VideoRemoteDataSource =
        VideoRemoteDataSourceImpl(videoApi, errorConverter)

    @Provides
    fun provideLiveVideoPlaylistDataSource(videoApi: VideoApi): LiveVideoPlaylistDataSource =
        LiveVideoPlaylistDataSourceImpl(videoApi)

    @Provides
    @Singleton
    fun provideFeedRepository(
        videoApi: VideoApi,
        repostApi: RepostApi,
        channelRemoteDataSource: ChannelRemoteDataSource,
        channelViewDao: ChannelViewDao,
        commentRemoteDataSource: CommentRemoteDataSource,
        videoRemoteDataSource: VideoRemoteDataSource,
        liveVideoPlaylistDataSource: LiveVideoPlaylistDataSource,
        errorConverter: Converter<ResponseBody, ErrorResponse>?
    ): FeedRepository =
        FeedRepositoryImpl(
            videoApi,
            repostApi,
            channelRemoteDataSource,
            Dispatchers.IO,
            channelViewDao,
            commentRemoteDataSource,
            videoRemoteDataSource,
            homeCategoryViewDao = object : HomeCategoryViewDao {
                override suspend fun save(roomVideoCollectionView: RoomVideoCollectionView) {
                    TODO("Not yet implemented")
                }

                override suspend fun getRecentViewCounts(userId: String): List<RoomVideoCollectionViewCount> {
                    TODO("Not yet implemented")
                }

                override suspend fun deleteOlderViews(userId: String) {
                    TODO("Not yet implemented")
                }
            },
            liveVideoPlaylistDataSource = liveVideoPlaylistDataSource,
            errorConverter = errorConverter,
        )

    @Provides
    @Singleton
    fun provideLoginRemoteDataSource(loginApi: LoginApi): LoginRemoteDataSource =
        LoginRemoteDataSourceImpl(loginApi = loginApi)

    @Provides
    @Singleton
    fun provideLoginRepository(
        loginApi: LoginApi,
        loginRemoteDataSource: LoginRemoteDataSource,
        registerErrorConverter: Converter<ResponseBody, RegisterErrorResponse>?,
    ): LoginRepository =
        LoginRepositoryImpl(
            loginApi = loginApi,
            loginRemoteDataSource = loginRemoteDataSource,
            dispatcher = Dispatchers.IO,
            hashCalculator = HashCalculator,
            registerErrorConverter = registerErrorConverter
        )

    @Provides
    @Singleton
    fun provideChannelRemoteDataSource(
        channelApi: ChannelApi,
        videoApi: VideoApi,
        userApi: UserApi,
        errorConverter: Converter<ResponseBody, ErrorResponse>?
    ): ChannelRemoteDataSource =
        ChannelRemoteDataSourceImpl(
            channelApi,
            videoApi,
            userApi,
            Dispatchers.IO,
            errorConverter
        )

    @Provides
    @Singleton
    fun provideChannelRepository(
        channelRemoteDataSource: ChannelRemoteDataSource,
        channelViewDao: ChannelViewDao,
        channelFollowDao: ChannelFollowDao,
    ): ChannelRepository =
        ChannelRepositoryImpl(
            channelRemoteDataSource,
            channelViewDao,
            channelFollowDao,
            Dispatchers.IO,
        )

    @Provides
    @Singleton
    fun provideUserProfileRemoteDataSource(
        userApi: UserApi,
        uploadManager: UploadManager,
    ): UserProfileRemoteDataSource =
        UserProfileRemoteDataSourceImpl(
            userApi,
            uploadManager,
            Dispatchers.IO,
        )

    @Provides
    @Singleton
    fun provideProfileRepository(
        loginApi: LoginApi,
        userProfileRemoteDataSource: UserProfileRemoteDataSource,
    ): ProfileRepository =
        ProfileRepositoryImpl(
            loginApi = loginApi,
            userProfileRemoteDataSource = userProfileRemoteDataSource,
            userProfileLocalDataSource = object : UserProfileLocalDataSource {
                override suspend fun getCountries(): List<CountryEntity> {
                    TODO("Not yet implemented")
                }
            },
            dispatcher = Dispatchers.IO,
        )

    @Provides
    @Singleton
    fun provideSettingsRemoteDataSource(userApi: UserApi): SettingsRemoteDataSource =
        SettingsRemoteDataSourceImpl(
            userApi
        )

    @Provides
    @Singleton
    fun provideSettingsLocalDataSource(@ApplicationContext context: Context): SettingsLocalDataSource =
        SettingsLocalDataSourceImpl(context, BuildConfig.OPEN_SOURCE_LICENCES_FILENAME)

    @Provides
    @Singleton
    fun provideSettingsRepository(
        settingsRemoteDataSource: SettingsRemoteDataSource,
        settingsLocalDataSource: SettingsLocalDataSource,
    ): SettingsRepository =
        SettingsRepositoryImpl(
            settingsRemoteDataSource,
            settingsLocalDataSource,
            Dispatchers.IO,
        )

    @Provides
    @Singleton
    fun provideReportContentRemoteDataSource(
        reportApi: ReportApi,
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

    @Provides
    @Singleton
    fun provideDeviceType(): DeviceType {
        return DeviceType.Tv
    }

    @Provides
    fun provideUserRemoteDataSource(userApi: UserApi): UserRemoteDataSource =
        UserRemoteDataSourceImpl(userApi)

    @Provides
    fun provideAdRemoteDataSource(
        rumbleBannerApi: RumbleBannerApi,
        adsApi: RumbleAdsApi,
        preRollApi: PreRollApi
    ): RumbleAdsRemoteDataSource =
        RumbleAdsRemoteDataSourceImpl(rumbleBannerApi, adsApi, preRollApi)

    @Provides
    fun provideRumbleAdRepository(remoteDataSource: RumbleAdsRemoteDataSource): RumbleAdRepository =
        RumbleAdRepositoryImpl(remoteDataSource = remoteDataSource, dispatcher = Dispatchers.IO)

    @Provides
    fun providePlayListRemoteDataSource(
        videoApi: VideoApi,
    ): PlayListRemoteDataSource =
        PlayListRemoteDataSourceImpl(
            videoApi,
            Dispatchers.IO
        )

    @Provides
    fun providePlayListRepository(
        playlistRemoteDataSource: PlayListRemoteDataSource,
    ): PlayListRepository =
        PlayListRepositoryImpl(
            playlistRemoteDataSource
        )

    @Provides
    fun provideVideoListRemoteDataSource(
        videoApi: VideoApi,
    ): VideoListRemoteDataSource =
        VideoListRemoteDataSourceImpl(
            videoApi = videoApi,
            dispatcher = Dispatchers.IO,
        )

    @Provides
    fun provideVideoListRepository(
        videoListRemoteDataSource: VideoListRemoteDataSource,
    ): VideoListRepository =
        VideoListRepositoryImpl(
            videoListRemoteDataSource = videoListRemoteDataSource
        )

    @Provides
    fun provideEmoteRepository(database: RumbleDatabase): RecentEmoteRepository =
        RecentEmoteRepositoryImpl(database.emoteDao(),  Dispatchers.IO)
}