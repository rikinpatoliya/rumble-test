package com.rumble.ui3.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.RowHeaderPresenter
import com.rumble.R

class HomeCustomRowHeaderPresenter : RowHeaderPresenter() {
    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.v4_lb_home_row_header, parent, false)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any?) {
        val vh = viewHolder as ViewHolder
        vh.bind(item)
    }

    override fun onSelectLevelChanged(holder: RowHeaderPresenter.ViewHolder) {}

    class ViewHolder(view: View) : RowHeaderPresenter.ViewHolder(view) {

        private val titleView = view.findViewById<TextView>(R.id.home_row_title)

        fun bind(item: Any?) {
            val listRowItem = item as ListRow
            val headerText = listRowItem.headerItem.name
            titleView.text = headerText
        }
    }
}
