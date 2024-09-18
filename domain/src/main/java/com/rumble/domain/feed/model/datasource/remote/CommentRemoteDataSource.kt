package com.rumble.domain.feed.model.datasource.remote

import com.rumble.domain.feed.domain.domainmodel.comments.UserComment
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.network.api.VideoApi
import com.rumble.network.dto.comments.CommentVoteBody
import com.rumble.network.dto.comments.CommentVoteData
import com.rumble.network.dto.comments.CommentVoteResponse
import com.rumble.network.dto.comments.UserCommentResponse
import okhttp3.FormBody
import retrofit2.Response

interface CommentRemoteDataSource {
    suspend fun postComment(userComment: UserComment): Response<UserCommentResponse>
    suspend fun deleteComment(commentId: Long): Response<UserCommentResponse>
    suspend fun likeComment(commentId: Long, userVote: UserVote): Response<CommentVoteResponse>
}

class CommentRemoteDataSourceImpl(private val videoApi: VideoApi) : CommentRemoteDataSource {
    override suspend fun postComment(userComment: UserComment): Response<UserCommentResponse> =
        videoApi.postComment(
            body = FormBody.Builder()
                .add("comment", userComment.comment)
                .add("video", userComment.videoId.toString())
                .add("comment_id", userComment.commentId?.toString() ?: "")
                .build()
        )

    override suspend fun deleteComment(commentId: Long): Response<UserCommentResponse> =
        videoApi.deleteComment(commentId)

    override suspend fun likeComment(commentId: Long, userVote: UserVote): Response<CommentVoteResponse>{
        val voteBody = CommentVoteBody(
            data = CommentVoteData(commentId, userVote.value)
        )
        return videoApi.voteComment(voteBody)
    }
}