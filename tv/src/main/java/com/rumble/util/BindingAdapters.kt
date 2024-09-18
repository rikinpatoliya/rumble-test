package com.rumble.util

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.rumble.R
import com.rumble.domain.feed.domain.domainmodel.video.PpvEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoStatus
import com.rumble.util.Constant.VIEWERS_COUNT_MIN
import com.rumble.utils.RumbleUIUtil
import com.rumble.utils.extension.shortString
import com.rumble.widget.TextDrawable
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class BindingAdapters @Inject constructor(
    private val rumbleUIUtil : RumbleUIUtil
) {

    @BindingAdapter(value = ["circleImageUrl", "placeholder"], requireAll = false)
    fun loadImageUsingGlideWithLetterPlaceholder(imageView: ImageView, circleImageUrl: String?, placeholder: String?) {
        var requestBuilder = Glide.with(imageView.context).load(circleImageUrl)

        if (placeholder != null) {
            val background = TextDrawable.builder()
                .beginConfig()
                .useFont(Typeface.DEFAULT)
                .fontSize(imageView.context.resources.getDimensionPixelSize(R.dimen.icons_placeholder_text_size))
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(
                    placeholder.firstOrNull()?.toString() ?: "",
                    ContextCompat.getColor(
                        imageView.context, rumbleUIUtil.getPlaceholderColorResId(
                            placeholder
                        )
                    )
                )

            imageView.background = background
            requestBuilder = requestBuilder.placeholder(background)
        }

        requestBuilder
            .transform(MultiTransformation(CenterCrop(), CircleCrop()))
            .into(imageView)
    }

    @BindingAdapter(value = ["roundedCornerImageUrl", "cornerRadius"], requireAll = true)
    fun loadImageUsingGlideWithRoundedCorner(imageView: ImageView, roundedCornerImageUrl: String?, cornerRadius: Float) {
        if (roundedCornerImageUrl.isNullOrEmpty().not()) {
            Glide.with(imageView.context)
                .load(roundedCornerImageUrl)
                .transform(MultiTransformation(CenterCrop(), RoundedCorners(
                    imageView.context.resources.getDimensionPixelSize(R.dimen.rounded_corner_radius)
                )))
                .placeholder(R.drawable.v3_card_placeholder)
                .into(imageView)
        } else {
            Glide.with(imageView.context)
                .load(R.drawable.v3_card_placeholder)
                .transform(MultiTransformation(CenterCrop(), RoundedCorners(
                    imageView.context.resources.getDimensionPixelSize(R.dimen.rounded_corner_radius)
                )))
                .into(imageView)
        }

    }

    @BindingAdapter("formattedNumber")
    fun formattedNumber(textView: TextView, number : Int) {
        textView.text = number.shortString()
    }

    @BindingAdapter(value = ["formattedFollowers", "is_followed"], requireAll = true)
    fun formattedFollowers(textView: TextView, number : Int, isFollowed : Boolean) {
        if (isFollowed) {
            textView.text = number.shortString()
        } else {
            textView.text = textView.context.getString(
                R.string.followers_pattern,
                number.shortString()
            )
        }

    }
    @BindingAdapter("formattedViewers", "viewerSeparator")
    fun formattedViewers(textView: TextView, number : Long, viewerSeparator: Boolean) {
        if (number > VIEWERS_COUNT_MIN){
            textView.isVisible = true
            val viewers = if (viewerSeparator){
                "${number.shortString()} • ${textView.context.getString(R.string.viewers_label).replaceFirstChar(Char::titlecase)}"
            } else {
                "${number.shortString()} ${textView.context.getString(R.string.viewers_label)}"
            }
            textView.text = viewers
        } else {
            textView.isInvisible = true
        }
    }

    @BindingAdapter("cardPPVStatus", "videoStatus")
    fun cardPPVStatus(view: View, item : VideoEntity, videoStatus: VideoStatus) {
        view.isVisible = !(videoStatus == VideoStatus.LIVE) && item.ppv != null
    }

    @BindingAdapter("cardPPVLabel")
    fun cardPPVLabel(textView: TextView, item : PpvEntity?) {
        Timber.d("cardPPVLabel ppv: ${item}")
        if (item != null) {
            textView.text = if (item.isPurchased){
                textView.context.getString(R.string.video_card_purchased_label)
            } else {
                textView.context.getString(R.string.video_card_ppv_label)
            }
            textView.isVisible = true
        } else {
            textView.isVisible = false
        }
    }

    @BindingAdapter("loadViewAllImageWithRoundedCorner")
    fun loadViewAllImageWithRoundedCorner(imageView: ImageView, roundedCornerImage: Drawable?) {
        Glide.with(imageView.context)
            .load(roundedCornerImage)
            .transform(MultiTransformation(CenterCrop(), RoundedCorners(
                imageView.context.resources.getDimensionPixelSize(R.dimen.rounded_corner_radius)
            )))
            .into(imageView)
    }
}