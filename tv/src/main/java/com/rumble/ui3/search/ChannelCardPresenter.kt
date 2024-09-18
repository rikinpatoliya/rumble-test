package com.rumble.ui3.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.rumble.databinding.V3ChannelCardBinding
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity


class ChannelCardPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        val binding = V3ChannelCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any?) {
        val channelCardViewHolder = viewHolder as ViewHolder

        if (item is ChannelDetailsEntity) {
            channelCardViewHolder.bind(item)
        }
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {
        val viewHolder = viewHolder as ViewHolder
        viewHolder.unBind()
    }

    class ViewHolder(private val binding: V3ChannelCardBinding) : Presenter.ViewHolder(binding.root) {

        fun bind(item: ChannelDetailsEntity) {
            binding.item = item
        }

        fun unBind() {
            Glide.with(binding.icon.context).clear(binding.icon)
            binding.item = null
        }

    }

}