package com.rumble.ui3.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.rumble.databinding.V3VideoCardViewAllBinding


class VideoViewAllCardPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = V3VideoCardViewAllBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}