package com.rumble.ui3.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.rumble.databinding.V3ChannelCardViewAllBinding
import com.rumble.databinding.V3VideoCardViewAllBinding
import com.rumble.databinding.V4TopLiveCategoriesCardViewAllBinding
import com.rumble.ui3.common.TopLiveCategoriesViewAllCardPresenter


class ChannelViewAllCardPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = V3ChannelCardViewAllBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ChannelViewAllCardViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val channelViewAllCardViewHolder = viewHolder as ChannelViewAllCardViewHolder
        channelViewAllCardViewHolder.bind()
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

    class ChannelViewAllCardViewHolder(private val binding: V3ChannelCardViewAllBinding) :
        ViewHolder(binding.root) {
        fun bind() {
            binding.root.setOnFocusChangeListener { _, hasFocus ->
                binding.focusFrame.isSelected = hasFocus
            }
        }
    }
}