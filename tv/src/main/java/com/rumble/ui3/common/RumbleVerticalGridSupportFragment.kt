package com.rumble.ui3.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*


abstract class RumbleVerticalGridSupportFragment : VerticalGridSupportFragment(), BrowseSupportFragment.MainFragmentAdapterProvider {


    /***/
    protected lateinit var arrayObjectAdapter  : ArrayObjectAdapter
    /***/
    abstract val numberOfColumns: Int

    /***/
    private val fragmentAdapter: BrowseSupportFragment.MainFragmentAdapter<RumbleVerticalGridSupportFragment> =
        object : BrowseSupportFragment.MainFragmentAdapter<RumbleVerticalGridSupportFragment>(this) {}

    abstract fun getPresenter() : Presenter

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<RumbleVerticalGridSupportFragment> = fragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = super.onCreateView(inflater, container, savedInstanceState)
        updateBrowseGridDockMargins(rootView)
        return rootView
    }

    open fun leftMargin() : Int = 0

    open fun topMargin() : Int = 0

    private fun setupAdapter() {
        val presenter = VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_NONE, false)
        presenter.shadowEnabled = false
        presenter.numberOfColumns = numberOfColumns
        setGridPresenter(presenter)
        val creditsPresenter = getPresenter()
        arrayObjectAdapter = ArrayObjectAdapter(creditsPresenter)
        adapter = arrayObjectAdapter
    }

    private fun updateBrowseGridDockMargins(rootView: View) {
        // assume that root view is VerticalGridSupportFragment(grid_fragment.xml)
        // In this view group we looking for browse_grid_dock and move it to bottom
        val gridDock = rootView.findViewById<ViewGroup>(androidx.leanback.R.id.browse_grid_dock)
        val layoutParams: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams.setMargins(leftMargin(), topMargin(), 0, 0)
        gridDock.layoutParams = layoutParams
    }
}