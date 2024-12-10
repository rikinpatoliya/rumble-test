package com.rumble.network.api

import com.rumble.network.NetworkRumbleConstants.RUMBLE_UPLOAD_API_VERSION
import com.rumble.network.NetworkRumbleConstants.VIDEO_UPLOAD_CHUNK_SIZE
import com.rumble.network.dto.camera.MergeVideoChunksResponse
import com.rumble.network.dto.camera.SetVideoMetadataResponse
import com.rumble.network.dto.camera.UploadThumbnailResponse
import com.rumble.network.dto.camera.UploadVideoChunkResponse
import okhttp3.FormBody
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface CameraApi {

    @Multipart
    @POST("upload.php")
    suspend fun uploadVideoThumbnail(
        @Query("api") api: String = RUMBLE_UPLOAD_API_VERSION,
        @Query("cthumb") filename: String,
        @Query("json") json: String = "1",
        @Part thumbnailImage: MultipartBody.Part
    ): Response<UploadThumbnailResponse>

    @PUT("upload.php")
    suspend fun uploadVideoChunk(
        @Query("api") api: String = RUMBLE_UPLOAD_API_VERSION,
        @Query("chunkSz") chunkSize: String = "$VIDEO_UPLOAD_CHUNK_SIZE",
        @Query("chunkQty") chunkQty: String,
        @Query("chunk") chunk: String,
        @Query("format") format: String = "json",
        @Body videoChunk: RequestBody
    ): Response<UploadVideoChunkResponse>

    @PUT("upload.php")
    suspend fun mergeVideoChunks(
        @Query("api") api: String = RUMBLE_UPLOAD_API_VERSION,
        @Query("chunkSz") chunkSize: String = "$VIDEO_UPLOAD_CHUNK_SIZE",
        @Query("chunkQty") chunkQty: String,
        @Query("chunk") chunk: String,
        @Query("merge") merge: String,
        @Query("format") format: String = "json",
    ): Response<MergeVideoChunksResponse>

    @POST("upload.php")
    suspend fun setVideoMetadata(
        @Query("api") api: String = RUMBLE_UPLOAD_API_VERSION,
        @Query("form") form: String = "1",
        @Query("json") json: String = "1",
        @Body body: FormBody
    ): Response<SetVideoMetadataResponse>
}