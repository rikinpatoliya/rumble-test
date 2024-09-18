package com.rumble.ui3.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.rumble.R
import com.rumble.databinding.V3VideoCardBinding
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoStatus
import com.rumble.theme.minVideoCardProgressWidth
import com.rumble.ui3.library.getMetadataLabelText
import com.rumble.utils.extension.getMediumDateTimeString
import com.rumble.utils.extension.toDp
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class VideoCardPresenter @Inject constructor(@ApplicationContext val context: Context) : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = V3VideoCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoCardViewHolder(context, binding)
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val videoCardViewHolder = viewHolder as VideoCardViewHolder

        if (item is VideoEntity) {
            val videoCollectionItem = item as VideoEntity
            videoCardViewHolder.bind(videoCollectionItem)
        }

    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

    class VideoCardViewHolder(private val context: Context, private val binding: V3VideoCardBinding) :
        ViewHolder(binding.root) {


        fun bind(item: VideoEntity) {
            binding.item = item
            binding.status = item.videoStatus

            updateTimestampLabel(item.videoStatus, item)

            binding.root.setOnFocusChangeListener { _, hasFocus ->
                binding.focusFrame.isSelected = hasFocus
                binding.title.isSelected = hasFocus
            }

            binding.metaDataText.text = getMetadataLabelText(
                context = context,
                watchingNow = item.watchingNow,
                uploadDate = item.uploadDate,
                viewsNumber = item.viewsNumber,
                likeNumber = item.likeNumber,
                livestreamStatus = item.livestreamStatus,
                liveDateTime = item.liveDateTime,
                isPremiumExclusiveContent = item.isPremiumExclusiveContent,
                livestreamedOn = item.liveStreamedOn
            )

            val lastPosition = item.lastPositionSeconds
            val progressBar = binding.progressBar
            if (lastPosition != null && lastPosition > 0 && item.duration > 0) {
                progressBar.visibility = View.VISIBLE
                val width = context.resources.getDimension(R.dimen.video_card_width)
                val minProgress = minVideoCardProgressWidth / (width.toInt().toDp(context) / progressBar.max)
                progressBar.progress =
                    maxOf(((lastPosition.toFloat() / item.duration) * 100).toInt(), minProgress.value.toInt())
            } else {
                progressBar.visibility = View.GONE
            }
        }

        private fun updateTimestampLabel(videoStatus: VideoStatus, item: VideoEntity) {
            if (videoStatus == VideoStatus.SCHEDULED) {
                binding.scheduledLabel.text = item.scheduledDate?.getMediumDateTimeString() ?: ""
            }
        }
    }

}