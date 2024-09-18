package com.rumble.ui3.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.rumble.databinding.V4TopLiveCategoriesCardBinding
import com.rumble.databinding.V4TopLiveCategoriesCardViewAllBinding
import com.rumble.domain.discover.domain.domainmodel.CategoryEntity


class TopLiveCategoriesViewAllCardPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = V4TopLiveCategoriesCardViewAllBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return CategoriesViewAllCardViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val categoriesViewAllCardViewHolder = viewHolder as CategoriesViewAllCardViewHolder
        categoriesViewAllCardViewHolder.bind(item)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

    class CategoriesViewAllCardViewHolder(private val binding: V4TopLiveCategoriesCardViewAllBinding) :
        ViewHolder(binding.root) {
        fun bind(item: Any?) {
            binding.root.setOnFocusChangeListener { _, hasFocus ->
                binding.focusFrame.isSelected = hasFocus
            }
        }
    }
}