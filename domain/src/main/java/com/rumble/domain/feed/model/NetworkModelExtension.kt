package com.rumble.domain.feed.model

import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelType
import com.rumble.domain.channels.channeldetails.domain.domainmodel.FollowStatus
import com.rumble.domain.channels.channeldetails.domain.domainmodel.LocalsCommunityEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UserUploadChannelEntity
import com.rumble.domain.earnings.domainmodel.EarningsEntity
import com.rumble.domain.feed.domain.domainmodel.ads.AdEntity
import com.rumble.domain.feed.domain.domainmodel.ads.AdsType
import com.rumble.domain.feed.domain.domainmodel.category.VideoCategoryEntity
import com.rumble.domain.feed.domain.domainmodel.collection.VideoCollectionType
import com.rumble.domain.feed.domain.domainmodel.comments.CommentEntity
import com.rumble.domain.feed.domain.domainmodel.video.PlayListChannelEntity
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.domain.feed.domain.domainmodel.video.PlayListUserEntity
import com.rumble.domain.feed.domain.domainmodel.video.PlaylistVideoEntity
import com.rumble.domain.feed.domain.domainmodel.video.PpvEntity
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoLogView
import com.rumble.domain.feed.domain.domainmodel.video.VideoSource
import com.rumble.domain.feed.domain.domainmodel.video.VideoStatus
import com.rumble.domain.feed.domain.domainmodel.video.WatchingNowEntity
import com.rumble.domain.library.domain.model.PlayListVisibility
import com.rumble.domain.livechat.domain.domainmodel.LiveGateEntity
import com.rumble.domain.profile.domainmodel.CountryEntity
import com.rumble.domain.profile.domainmodel.Gender
import com.rumble.domain.profile.domainmodel.ProfileNotificationEntity
import com.rumble.domain.profile.domainmodel.UserProfileEntity
import com.rumble.domain.referrals.domain.domainmodel.ReferralDetailsEntity
import com.rumble.domain.referrals.domain.domainmodel.ReferralEntity
import com.rumble.domain.settings.domain.domainmodel.NotificationSettingsEntity
import com.rumble.domain.settings.domain.domainmodel.NotificationSettingsLegacyEntity
import com.rumble.network.dto.LiveStreamStatus
import com.rumble.network.dto.ads.revcontent.RevTrackBody
import com.rumble.network.dto.ads.revcontent.Revcontent
import com.rumble.network.dto.categories.Categories
import com.rumble.network.dto.categories.VideoCategory
import com.rumble.network.dto.channel.Channel
import com.rumble.network.dto.channel.UserUploadChannel
import com.rumble.network.dto.collection.VideoCollectionWithoutVideos
import com.rumble.network.dto.comments.Comment
import com.rumble.network.dto.profile.ProfileNotificationItem
import com.rumble.network.dto.profile.UserProfile
import com.rumble.network.dto.referral.Referral
import com.rumble.network.dto.referral.ReferralsData
import com.rumble.network.dto.settings.Earnings
import com.rumble.network.dto.settings.NotificationSettings
import com.rumble.network.dto.video.PlayList
import com.rumble.network.dto.video.PlayListChannel
import com.rumble.network.dto.video.PlayListUser
import com.rumble.network.dto.video.Ppv
import com.rumble.network.dto.video.Video
import com.rumble.network.dto.video.WatchingNowData
import com.rumble.utils.RumbleConstants
import com.rumble.utils.extension.convertUtcToLocal
import com.rumble.utils.extension.toChannelIdString
import com.rumble.utils.extension.toDate
import com.rumble.utils.extension.toUserIdString
import java.time.LocalDateTime
import kotlin.math.max

fun AdEntity.getRevTrackBody(): RevTrackBody =
    RevTrackBody(
        view = viewHash,
        viewType = "widget",
        position = position.toString()
    )

fun Revcontent.getAdEntity(): AdEntity =
    AdEntity(
        videoThumbnail = image,
        title = headline,
        type = AdsType.getByValue(type),
        adUrl = url
    )

fun PlayList.getPlayListEntity(): PlayListEntity =
    PlayListEntity(
        id = id,
        title = title,
        description = description,
        visibility = PlayListVisibility.getByValue(visibility),
        isFollowing = isFollowing,
        url = url,
        thumbnail = items.getOrNull(0)?.video?.thumbnail ?: "",
        updatedDate = updatedOn.convertUtcToLocal(),
        playListOwnerId = channel?.getPlayListChannelEntity()?.channelId ?: user.getPlayListUserEntity().id,
        playListUserEntity = user.getPlayListUserEntity(),
        playListChannelEntity = channel?.getPlayListChannelEntity(),
        videosQuantity = numberOfItems,
        username = user.username,
        channelName = channel?.title,
        channelThumbnail = channel?.picture ?: user.picture ?: "",
        channelId = channel?.channelId?.toChannelIdString() ?: user.id.toUserIdString(),
        verifiedBadge = channel?.verifiedBadge ?: user.verifiedBadge,
        followers = channel?.channelFollowers ?: user.userFollowers,
        followStatus = FollowStatus(
            channelId = channel?.channelId?.toChannelIdString() ?: user.id.toUserIdString(),
            followed = channel?.channelFollowed ?: user.userFollowed,
        ),
        videoIds = extra?.videoIds?.map { it.toLong() }?.toMutableList(),
        videos = items.map { it.video.getPlaylistVideoEntity() }.toMutableList()
    )

fun PlayListUser.getPlayListUserEntity() : PlayListUserEntity =
    PlayListUserEntity(
        id = id.toUserIdString(),
        username = username,
        thumbnail = picture,
        followers = userFollowers,
        verifiedBadge = verifiedBadge,
        followStatus = FollowStatus(
            channelId = id.toUserIdString(),
            followed = userFollowed
        )
    )

fun PlayListChannel.getPlayListChannelEntity() : PlayListChannelEntity =
    PlayListChannelEntity(
        channelId = channelId.toChannelIdString(),
        url = url,
        title = title,
        thumbnail = picture,
        followers = channelFollowers,
        verifiedBadge = verifiedBadge,
        followStatus = FollowStatus(
            channelId = channelId.toChannelIdString(),
            followed = channelFollowed
        )
    )

fun Video.getPlaylistVideoEntity(): PlaylistVideoEntity =
    PlaylistVideoEntity(
        id = id.toLong(),
        title = title,
        thumbnail = thumbnail,
        uploadDate = uploadDate,
        numberOfView = numberOfView,
        watchingNow = watchingNow,
        duration = duration,
        livestreamStatus = livestreamStatus,
        liveDateTime = liveDateTime,
        liveStreamedOn = liveStreamedOn,
        tags = tags,
        livestreamHasDvr = livestreamHasDvr,
    )

fun Video.getVideoEntity(): VideoEntity =
    VideoEntity(
        id = id.toLong(),
        description = description,
        videoThumbnail = thumbnail ?: "",
        numberOfView = numberOfView,
        url = url,
        channelThumbnail = videoSource?.thumbnail ?: "",
        channelId = videoSource?.id ?: "",
        channelName = videoSource?.name ?: "",
        videoStatus = mapVideoStatus(),
        uploadDate = uploadDate.convertUtcToLocal(),
        scheduledDate = liveDateTime?.convertUtcToLocal(),
        watchingNow = watchingNow ?: 0,
        duration = duration.toLong(),
        title = title,
        commentNumber = comments?.count?.toLong() ?: 0L,
        viewsNumber = numberOfView.toLong(),
        likeNumber = getLikesNumberWithUserVote(rumbleVotes.numVotesUp, rumbleVotes.userVote),
        dislikeNumber = getDisLikesNumberWithUserVote(rumbleVotes.numVotesDown, rumbleVotes.userVote),
        userVote = UserVote.getByVote(rumbleVotes.userVote),
        videoSourceList = videos.map {
            VideoSource(
                it.url,
                it.type,
                it.res,
                it.bitrate,
                it.qualityText,
                it.bitrateText
            )
        },
        channelFollowers = videoSource?.followers ?: 0,
        channelFollowed = videoSource?.followed ?: false,
        channelBlocked = videoSource?.blocked ?: false,
        portraitMode = false, // the real size of the video will be defined by the player after decoding, do not relay on this parameter then video just came from the server
        videoWidth = videoWidth,
        videoHeight = videoHeight,
        livestreamStatus = LiveStreamStatus.get(livestreamStatus),
        liveDateTime = liveDateTime?.convertUtcToLocal(),
        liveStreamedOn = liveStreamedOn?.convertUtcToLocal(),
        supportsDvr = livestreamHasDvr ?: false,
        videoLogView = VideoLogView(log.view),
        commentList = comments?.items?.map { it.getCommentEntity() },
        commentsDisabled = areCommentsDisabled ?: false,
        relatedVideoList = related?.mapIndexed { index, video -> video.getVideoEntity().copy(index = index) },
        tagList = tags,
        categoriesList = categories?.getVideoCategories(),
        verifiedBadge = videoSource?.verifiedBadge ?: false,
        ppv = ppv?.getPpv(),
        ageRestricted = ageRestricted ?: false,
        liveChatDisabled = liveChatDisabled ?: false,
        lastPositionSeconds = watchingProgress?.lastPosition,
        includeMetadata = includeMetadata ?: false,
        isPremiumExclusiveContent = availability?.equals(
            RumbleConstants.PREMIUM_VIDEO_AVAILABILITY,
            true
        ) ?: false,
        subscribedToCurrentChannel = videoSource?.subscribed == true,
        hasLiveGate = liveGate != null,
        liveGateEntity = liveGate?.let { LiveGateEntity(it.timeCode, it.countdown) },
    )

private fun Categories.getVideoCategories(): List<VideoCategoryEntity> {
    val list = mutableListOf<VideoCategoryEntity>()
    primary?.let {
        list.add(it.getCategoryEntity())
    }
    secondary?.let {
        list.add(it.getCategoryEntity())
    }
    return list
}

private fun VideoCategory.getCategoryEntity(): VideoCategoryEntity =
    VideoCategoryEntity(slug = slug, title = title)

private fun Ppv.getPpv(): PpvEntity =
    PpvEntity(
        priceCents = priceCents,
        isPurchased = isPurchased,
        purchaseDeadline = purchaseDeadline?.convertUtcToLocal(),
        productId = productId
    )

private fun getLikesNumberWithUserVote(numVotes: Int, userVote: Int): Long {
    return when (UserVote.getByVote(userVote)) {
        UserVote.LIKE -> max(1, numVotes).toLong()
        else -> max(0, numVotes).toLong()
    }
}

private fun getDisLikesNumberWithUserVote(numVotes: Int, userVote: Int): Long {
    return when (UserVote.getByVote(userVote)) {
        UserVote.DISLIKE -> max(1, numVotes).toLong()
        else -> max(0, numVotes).toLong()
    }
}

fun UserProfile.getUserProfileEntity(): UserProfileEntity =
    UserProfileEntity(
        apiKey = apiKey,
        fullName = address?.fullName ?: "",
        email = email,
        validated = validated == 1,
        userPicture = thumbnail ?: "",
        phone = address?.phone ?: "",
        address = address?.address1 ?: "",
        city = address?.city ?: "",
        state = address?.stateProv ?: "",
        postalCode = address?.postalCode ?: "",
        country = CountryEntity(
            countryID = address?.countryID ?: 0,
            countryName = address?.countryName ?: "",
        ),
        paypalEmail = address?.payInfo ?: "",
        followedChannelCount = followingCount,
        isPremium = isPremium,
        gender = Gender.getByValue(gender),
        birthday = birthday?.toDate()
    )

fun Channel.getChannelDetailsEntity(): ChannelDetailsEntity =
    ChannelDetailsEntity(
        channelId = id,
        channelTitle = title,
        name = name,
        type = ChannelType.getByValue(type),
        thumbnail = thumbnail ?: "",
        backSplash = backSplash ?: "",
        rumbles = rumbles,
        followers = followers,
        following = following,
        videoCount = videos,
        followed = followed,
        blocked = blocked ?: false,
        pushNotificationsEnabled = isPushLiveStreamsEnabled ?: false,
        emailNotificationsEnabled = notification ?: false,
        emailNotificationsFrequency = notificationFrequency ?: 0,
        localsCommunityEntity = localsCommunity?.let {
            LocalsCommunityEntity(
                title = it.ownerName,
                description = it.description,
                profileImage = it.logoUrl,
                communityMembers = it.counts.members,
                comments = it.counts.comments,
                posts = it.counts.posts,
                likes = it.counts.likes,
                videoUrl = it.urls.videoUrl,
                channelUrl = it.urls.channelUrl,
            )
        },
        latestVideo = latestVideo?.getVideoEntity(),
        featuredVideo = featuredVideo?.getVideoEntity(),
        verifiedBadge = verifiedBadge ?: false,
        channelUrl = url,
        watchingNowCount = watchingNowCount
    )

fun NotificationSettings.getNotificationSettingsEntity(): NotificationSettingsEntity =
    NotificationSettingsEntity(
        legacyValues = NotificationSettingsLegacyEntity(
            mediaApproved = mediaApproved,
            mediaComment = mediaComment,
            commentReplied = commentReplied,
            battlePosted = battlePosted,
            winMoney = winMoney,
            videoTrending = videoTrending,
            allowPush = allowPush,
        ),
        moneyEarned = earn,
        videoApprovedForMonetization = videoLive,
        someoneFollowsYou = follow,
        someoneTagsYou = tag,
        commentsOnYourVideo = comment,
        repliesToYourComments = commentReply,
        newVideoBySomeoneYouFollow = newVideo
    )

fun WatchingNowData.getWatchingNowEntity(): WatchingNowEntity = WatchingNowEntity(
    videoId = videoId,
    numWatchingNow = numWatchingNow,
    livestreamStatus = LiveStreamStatus.get(livestreamStatus),
)

fun Comment.getCommentEntity(): CommentEntity = CommentEntity(
    commentId = commentId,
    author = user?.title ?: "",
    authorId = user?.id ?: "",
    authorThumb = user?.thumb ?: "",
    commentText = comment ?: "",
    replayList = replies?.map { it.getCommentEntity() },
    date = date?.convertUtcToLocal(),
    userVote = UserVote.getByVote(userVote),
    likeNumber = max(commentScore, 0),
    verifiedBadge = user?.verifiedBadge ?: false
)

private fun Video.mapVideoStatus(): VideoStatus =
    when (livestreamStatus) {
        0 -> VideoStatus.STREAMED
        1 -> {
            if (liveStreamedOn.isNullOrBlank().not()) VideoStatus.LIVE
            else {
                val liveTime = liveDateTime?.convertUtcToLocal()
                if (liveTime == null) VideoStatus.UPCOMING
                else if (liveTime.isAfter(LocalDateTime.now())) VideoStatus.SCHEDULED
                else VideoStatus.STARTING
            }
        }
        2 -> VideoStatus.LIVE
        else -> VideoStatus.UPLOADED
    }

fun Referral.getReferral(): ReferralEntity =
    ReferralEntity(
        id = id,
        username = username,
        thumb = thumb,
        commission = commission.toBigDecimal()
    )

fun ReferralsData.getReferrals(): ReferralDetailsEntity =
    ReferralDetailsEntity(
        referrals = referrals.map { it.getReferral() } as MutableList<ReferralEntity>,
        impressionCount = impressions,
        commissionTotal = commissionTotal.toBigDecimal(),
        ticketTotal = ticketTotal,
        ownTicketCount = tickets.own,
        referralTicketCount = tickets.referral
    )

fun Earnings.getEarnings(): EarningsEntity =
    EarningsEntity(
        uploaded = uploaded,
        approved = approved,
        currentBalance = currentBalance.toBigDecimal(),
        cpm = cpm.toBigDecimal(),
        total = total.toBigDecimal(),
        rumble = rumble.toBigDecimal(),
        youtube = youtube.toBigDecimal(),
        partners = partners.toBigDecimal(),
        approvedPercentage = if (uploaded != 0) 100 * approved / uploaded else 0
    )

fun ProfileNotificationItem.getProfileNotificationEntity(): ProfileNotificationEntity =
    ProfileNotificationEntity(
        userName = user?.title ?: "",
        userThumb = user?.thumbnail ?: "",
        channelId = user?.id ?: "",
        message = body.substringAfter(user?.title ?: ""),
        timeAgo = sentOn.convertUtcToLocal(),
        videoEntity = video?.getVideoEntity()
    )

fun UserUploadChannel.getUserUploadChannelEntity(): UserUploadChannelEntity =
    UserUploadChannelEntity(
        id = "_c$id",
        channelId = id,
        title = title,
        name = name,
        followers = subscribers,
        thumbnail = thumbnail
    )

fun VideoCollectionWithoutVideos.getVideoCollectionEntity(): VideoCollectionType.VideoCollectionEntity =
    VideoCollectionType.VideoCollectionEntity(
        id = id,
        slug = slug,
        title = title,
        thumbnail = thumbnail,
        type = type,
        name = name,
        backsplash = backsplash,
        videos = videos,
        rumbles = rumbles,
        followers = followers,
        following = following,
        followed = followed
    )
