package com.rumble.ui3.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.rumble.databinding.V4TopLiveCategoriesCardBinding
import com.rumble.domain.discover.domain.domainmodel.CategoryEntity


class TopLiveCategoriesCardPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = V4TopLiveCategoriesCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VideoCardViewHolder(binding)
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val videoCardViewHolder = viewHolder as VideoCardViewHolder

        if (item is CategoryEntity) {
            videoCardViewHolder.bind(item)
        }

    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

    class VideoCardViewHolder(private val binding: V4TopLiveCategoriesCardBinding) :
        ViewHolder(binding.root) {


        fun bind(item: CategoryEntity) {
            binding.item = item

            binding.root.setOnFocusChangeListener { _, hasFocus ->
                binding.focusFrame.isSelected = hasFocus
                binding.title.isSelected = hasFocus
            }
        }
    }

}