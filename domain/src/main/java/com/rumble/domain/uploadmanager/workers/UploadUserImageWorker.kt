package com.rumble.domain.uploadmanager.workers

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import androidx.core.net.toFile
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rumble.network.api.UserApi
import com.rumble.network.di.IoDispatcher
import com.rumble.domain.uploadmanager.UploadManagerConstants
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

@HiltWorker
class UploadUserImageWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val userApi: UserApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        val uriStr = inputData.getString(UploadManagerConstants.KEY_URI)
        try {
            if (TextUtils.isEmpty(uriStr)) throw IllegalArgumentException("Invalid uri!")
            val uri = Uri.parse(uriStr)
            val response = userApi.updateUserImage(
                body = MultipartBody.Part.create(
                    FormBody.Builder()
                        .add("x1", "0")
                        .add("y1", "0")
                        .add("x2", "178")
                        .add("y2", "178")
                        .add("crop_target_width", "178")
                        .build()
                ),
                profileImage = MultipartBody.Part
                    .createFormData(
                        name = "profile_picture",
                        filename = "profile.png",
                        body = uri.toFile().asRequestBody()
                    ),
            )
            val responseBody = response.body()
            if (response.isSuccessful && responseBody != null)
                Result.success()
            else
                Result.failure()
        } catch (t: Throwable) {
            Result.failure()
        }
    }
}