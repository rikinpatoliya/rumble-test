package com.rumble.domain.video.domain.usecases

import android.content.Context
import android.net.Uri
import android.net.Uri.Builder
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.rumble.domain.feed.domain.domainmodel.category.VideoCategoryEntity
import com.rumble.network.di.AppStoreUrl
import com.rumble.network.di.BundleId
import com.rumble.network.di.IoDispatcher
import com.rumble.network.di.Publisher
import com.rumble.network.queryHelpers.ContentRating
import com.rumble.network.queryHelpers.PublisherId
import com.rumble.network.queryHelpers.VideoMetadata
import com.rumble.network.session.SessionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BuildVideoUrlWithMetadataParamsUseCase @Inject constructor(
    private val sessionManager: SessionManager,
    @ApplicationContext private val context: Context,
    @IoDispatcher private val backgroundDispatcher: CoroutineDispatcher,
    @BundleId private val bundleId: String,
    @AppStoreUrl private val appStoreUrl: String,
    @Publisher private val publisherId: PublisherId
) {
    private var advertisingIdInfo: AdvertisingIdClient.Info? = null

    suspend operator fun invoke(
        videoId: Long,
        videoUrl: String,
        videoWidth: Int,
        videoHeight: Int,
        resolution: Int,
        videoCategoryEntity: VideoCategoryEntity?,
        ageRestricted: Boolean,
        channelId: String
    ): String {
        initAdvertisingClient()

        val builder = Uri.parse(videoUrl)
            .buildUpon()
            .appendQueryParameter(VideoMetadata.AppName.key, VideoMetadata.AppName.commonValue)
            .appendQueryParameter(VideoMetadata.AppBundleId.key, bundleId)
            .appendQueryParameter(VideoMetadata.AppStoreUrl.key, appStoreUrl)
            .appendQueryParameter(VideoMetadata.Domain.key, VideoMetadata.Domain.commonValue)
            .appendQueryParameter(VideoMetadata.PublisherId.key, "u${publisherId.value}")
            .appendQueryParameter(VideoMetadata.Session.key, sessionManager.uniqueSession.first())
            .appendQueryParameter(VideoMetadata.VideoId.key, videoId.toString())
            .appendQueryParameter(VideoMetadata.GDPR.key, VideoMetadata.GDPR.commonValue)
            .appendQueryParameter(VideoMetadata.Rating.key, if (ageRestricted) ContentRating.PG13.value else ContentRating.G.value)
            .appendQueryParameter(VideoMetadata.ChannelId.key, channelId)
            .appendQueryParameter(VideoMetadata.Testing.key, VideoMetadata.Testing.commonValue)

        setAdvertisingIdParams(builder)
        setSizeParams(builder, videoWidth, videoHeight, resolution)
        setCategoryParam(builder, videoCategoryEntity)

        return builder.build().toString()
    }

    private suspend fun initAdvertisingClient() {
        if (advertisingIdInfo == null) {
            withContext(backgroundDispatcher) {
                advertisingIdInfo = try {
                    AdvertisingIdClient.getAdvertisingIdInfo(context)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    private fun setAdvertisingIdParams(builder: Builder) {
        if (advertisingIdInfo != null && advertisingIdInfo?.isLimitAdTrackingEnabled == false) {
            builder.appendQueryParameter(VideoMetadata.ResettableId.key, advertisingIdInfo?.id)
            builder.appendQueryParameter(VideoMetadata.Uuid.key, advertisingIdInfo?.id)
            builder.appendQueryParameter(VideoMetadata.DoNotTrack.key, "0")
        } else {
            builder.appendQueryParameter(VideoMetadata.DoNotTrack.key, "1")
        }
    }

    private fun setSizeParams(
        builder: Builder,
        videoWidth: Int,
        videoHeight: Int,
        resolution: Int
    ) {
        if (videoHeight > 0 && videoWidth > 0 && resolution > 0) {
            val aspectRatio = videoWidth.toFloat() / videoHeight.toFloat()
            if (aspectRatio >= 1) {
                builder.appendQueryParameter(VideoMetadata.Width.key, resolution.toString())
                builder.appendQueryParameter(VideoMetadata.Height.key, (resolution / aspectRatio).toInt().toString())
            } else {
                builder.appendQueryParameter(VideoMetadata.Width.key, (resolution * aspectRatio).toInt().toString())
                builder.appendQueryParameter(VideoMetadata.Height.key, resolution.toString())
            }
        }
    }

    private fun setCategoryParam(builder: Builder, videoCategoryEntity: VideoCategoryEntity?) {
        videoCategoryEntity?.let {
            builder.appendQueryParameter(VideoMetadata.Category.key, videoCategoryEntity.slug)
        }
    }
}