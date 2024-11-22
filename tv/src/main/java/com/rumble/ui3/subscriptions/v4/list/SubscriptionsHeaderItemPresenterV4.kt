package com.rumble.ui3.subscriptions.v4.list

import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.leanback.widget.PageRow
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.RowHeaderPresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.rumble.R
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.ui3.subscriptions.pages.list.HeaderItemWithData
import com.rumble.ui3.subscriptions.v4.AllSubscriptionsFragmentRow
import com.rumble.ui3.subscriptions.v4.AllSubscriptionsSort
import com.rumble.ui3.subscriptions.v4.ChannelFragmentRow
import com.rumble.util.PagingAdapter
import com.rumble.utils.RumbleUIUtil
import com.rumble.utils.extension.shortString
import com.rumble.widget.TextDrawable
import timber.log.Timber

class SubscriptionsHeaderItemPresenterV4(
    private val rumbleUIUtil: RumbleUIUtil,
    private val pagingAdapter: PagingAdapter<PageRow>,
) : RowHeaderPresenter() {


    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.v4_subscription_menu_item, parent, false)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any?) {
        val vh = viewHolder as ViewHolder
        vh.bind(item, rumbleUIUtil)
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {
        val vh = viewHolder as ViewHolder
        vh.unBind()
    }

    override fun onSelectLevelChanged(holder: RowHeaderPresenter.ViewHolder) {}

    class ViewHolder(view: View) : RowHeaderPresenter.ViewHolder(view) {

        private val titleView = view.findViewById<TextView>(R.id.title_text)
        private val imageView = view.findViewById<ImageView>(R.id.image_view)
        private val imageViewBorder = view.findViewById<View>(R.id.image_view_border)
        private val watchingNowText = view.findViewById<TextView>(R.id.watching_now_text)
        private val glideRequestManager = Glide.with(titleView.context)
        private var target: Target<*>? = null


        fun unBind() {
            target?.let {
                glideRequestManager.clear(target)
                target = null
            }
        }

        fun bind(item: Any?, rumbleUIUtil: RumbleUIUtil) {
            watchingNowText.visibility = View.GONE
            when (item) {
                is AllSubscriptionsFragmentRow -> {
                    val headerIconItem = ((item as Row).headerItem as HeaderItemWithData)
                    titleView.text = headerIconItem.name
                    val drawable = TextDrawable.builder()
                        .beginConfig()
                        .useFont(Typeface.DEFAULT)
                        .fontSize(titleView.context.resources.getDimensionPixelSize(R.dimen.icons_sub_text_size))
                        .bold()
                        .toUpperCase()
                        .width(titleView.context.resources.getDimensionPixelSize(R.dimen.icons_sub))
                        .height(titleView.context.resources.getDimensionPixelSize(R.dimen.icons_sub))
                        .endConfig()
                        .buildRound(
                            headerIconItem.data.toString(),
                            ContextCompat.getColor(titleView.context, R.color.white_10_percent)
                        )
                    imageView.setImageDrawable(drawable)
                    imageViewBorder.visibility = View.INVISIBLE
                    watchingNowText.visibility = View.GONE
                }

                is ChannelFragmentRow -> {
                    bindChannelFragmentRow(item, rumbleUIUtil)
                }

                is AllSubscriptionsSort -> {
                    val headerIconItem = ((item as Row).headerItem as HeaderItemWithData)
                    titleView.text = headerIconItem.name

                    val drawable = ContextCompat.getDrawable(
                        titleView.context,
                        R.drawable.ic_filter
                    )

                    imageView.setImageDrawable(drawable)
                    imageViewBorder.visibility = View.INVISIBLE
                }

                else -> {
                    Timber.e("Unsupported item type: ${item?.javaClass}")
                }
            }
        }

        private fun bindChannelFragmentRow(item: ChannelFragmentRow, rumbleUIUtil: RumbleUIUtil) {
            val headerIconItem = ((item as Row).headerItem as HeaderItemWithData)

            titleView.text = headerIconItem.name
            val channelData = headerIconItem.data as CreatorEntity

            imageViewBorder.visibility = View.INVISIBLE
            channelData.watchingNowCount?.let { watchingNow ->
                imageViewBorder.visibility = View.VISIBLE
                watchingNowText.visibility = View.VISIBLE
                watchingNowText.text = watchingNow.shortString(false)
            }

            val placeholder = TextDrawable.builder()
                .beginConfig()
                .useFont(Typeface.DEFAULT)
                .fontSize(titleView.context.resources.getDimensionPixelSize(R.dimen.icons_sub_text_size))
                .bold()
                .width(titleView.context.resources.getDimensionPixelSize(R.dimen.icons_sub))
                .height(titleView.context.resources.getDimensionPixelSize(R.dimen.icons_sub))
                .toUpperCase()
                .endConfig()
                .buildRound(
                    channelData.channelTitle.first().toString(),
                    ContextCompat.getColor(
                        titleView.context,
                        rumbleUIUtil.getPlaceholderColorResId(channelData.channelTitle)
                    )
                )

            imageView.setImageDrawable(placeholder)

            target = glideRequestManager
                .asBitmap()
                .load(channelData.thumbnail)
                .placeholder(placeholder)
                .circleCrop()
                .into(
                    object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?,
                        ) {
                            try {
                                if (resource.height > 1 && resource.width > 1) {
                                    val drawable: Drawable = BitmapDrawable(
                                        imageView.context.resources,
                                        Bitmap.createScaledBitmap(
                                            resource,
                                            titleView.context.resources.getDimensionPixelSize(R.dimen.icons_sub),
                                            titleView.context.resources.getDimensionPixelSize(R.dimen.icons_sub),
                                            true
                                        )
                                    )
                                    imageView.setImageDrawable(drawable)
                                } else {
                                    imageView.setImageDrawable(placeholder)
                                }
                            } catch (e: Exception) {
                                imageView.setImageDrawable(placeholder)
                            }
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            super.onLoadFailed(errorDrawable)
                            imageView.setImageDrawable(placeholder)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    }
                )
        }
    }
}
