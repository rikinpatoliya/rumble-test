package com.rumble.domain.channels.channeldetails.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CommentAuthorEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CommentAuthorsResult
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UserUploadChannelsResult.UserUploadChannelsError
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UserUploadChannelsResult.UserUploadChannelsSuccess
import com.rumble.domain.channels.model.repository.ChannelRepository
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class GetUserCommentAuthorsUseCase @Inject constructor(
    private val channelRepository: ChannelRepository,
    private val sessionManager: SessionManager,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
) : RumbleUseCase {

    suspend operator fun invoke(): CommentAuthorsResult {
        val username = sessionManager.userNameFlow.firstOrNull()
        val profilePicture = sessionManager.userPictureFlow.firstOrNull()
        val userId = sessionManager.userIdFlow.firstOrNull()

        if (username != null && userId != null) {

            return when (val result = channelRepository.fetchUserUploadChannels()) {
                is UserUploadChannelsError -> {
                    rumbleErrorUseCase(result.rumbleError)
                    CommentAuthorsResult.Failure(rumbleError = result.rumbleError)
                }
                is UserUploadChannelsSuccess -> {
                    val authors =
                        listOf<CommentAuthorEntity>(
                            CommentAuthorEntity.SelfAuthor(title = username, id = userId, thumbnail = profilePicture)
                        ) + result.userUploadChannels.map {
                            CommentAuthorEntity.ChannelAuthor(it)
                        }
                    CommentAuthorsResult.Success(authors)
                }
            }
        } else {
            return CommentAuthorsResult.Failure(rumbleError = null)
        }
    }
}