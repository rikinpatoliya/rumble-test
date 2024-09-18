package com.rumble.ui3.settings.pages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.rumble.databinding.V3CreditsCardBinding
import com.rumble.domain.license.domain.domainmodel.Dependency


class CreditsPresenter : Presenter() {


    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        val binding = V3CreditsCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any?) {
        val dependency : Dependency = item as Dependency
        val viewHolder = viewHolder as ViewHolder
        viewHolder.bind(dependency)
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {
        val viewHolder = viewHolder as ViewHolder
        viewHolder.unbind()
    }

    class ViewHolder(private val binding: V3CreditsCardBinding) : Presenter.ViewHolder(binding.root) {

        fun bind(dependency : Dependency) {
            binding.item = dependency

            binding.root.setOnFocusChangeListener { v, hasFocus ->
                binding.mainLayout.isSelected = hasFocus
            }
        }

        fun unbind() {
            binding.item = null
        }
    }

}