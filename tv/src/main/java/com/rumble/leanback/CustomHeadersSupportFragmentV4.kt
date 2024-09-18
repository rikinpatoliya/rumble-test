package com.rumble.leanback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.rumble.R

open class CustomHeadersSupportFragmentV4 : HeadersSupportFragment() {

    override fun getLayoutResourceId(): Int = R.layout.v3_headers_fragment

    override fun setAlignment(windowAlignOffsetTop: Int) {}

    override fun setOnHeaderViewSelectedListener(listener: OnHeaderViewSelectedListener?) {
        super.setOnHeaderViewSelectedListener(listener)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View? = super.onCreateView(inflater, container, savedInstanceState)

        view?.let {
            val footerFrameLayout = view.findViewById<FrameLayout>(R.id.browse_footer)
            getFooter(inflater, footerFrameLayout)?.let {
                footerFrameLayout.visibility = View.VISIBLE
            }
        }

        return view
    }

    open fun getFooter(inflater: LayoutInflater, viewGroup: ViewGroup): View? = null
}