package com.rumble.ui3.subscriptions.v4.list

import android.os.Bundle
import android.view.View
import androidx.leanback.widget.ClassPresenterSelector
import androidx.leanback.widget.FocusHighlightHelper
import androidx.leanback.widget.PageRow
import com.rumble.leanback.CustomHeadersSupportFragmentV4
import com.rumble.ui3.subscriptions.v4.AllSubscriptionsFragmentRow
import com.rumble.ui3.subscriptions.v4.AllSubscriptionsSort
import com.rumble.ui3.subscriptions.v4.ChannelFragmentRow
import com.rumble.ui3.subscriptions.v4.DEFAULT_HEADER_INDEX
import com.rumble.ui3.subscriptions.v4.SubscriptionsEmptyFragmentRow
import com.rumble.ui3.subscriptions.v4.SubscriptionsErrorFragmentRow
import com.rumble.ui3.subscriptions.v4.SubscriptionsNoSubscriptionsFragmentRow
import com.rumble.ui3.subscriptions.v4.SubscriptionsNotLoggedInFragmentRow
import com.rumble.util.PagingAdapter
import com.rumble.utils.RumbleUIUtil


class SubscriptionsHeadersSupportFragmentV4 : CustomHeadersSupportFragmentV4 {
    private lateinit var rumbleUIUtil: RumbleUIUtil
    private lateinit var pagingAdapter: PagingAdapter<PageRow>
    private var subscriptionsHeaderItemPresenter: SubscriptionsHeaderItemPresenterV4? = null

    constructor() : super()
    constructor(rumbleUIUtil: RumbleUIUtil, pagingAdapter: PagingAdapter<PageRow>) : super() {
        this.rumbleUIUtil = rumbleUIUtil
        this.pagingAdapter = pagingAdapter
        subscriptionsHeaderItemPresenter = SubscriptionsHeaderItemPresenterV4(rumbleUIUtil, pagingAdapter)
        init()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view?.post {
            setSelectedPosition(DEFAULT_HEADER_INDEX, false)
        }
    }

    private fun init() {
        // Setup focus highlight for header items
        FocusHighlightHelper.setupHeaderItemFocusHighlight(bridgeAdapter, false)

        // Set presenter selector for different row types
        presenterSelector = ClassPresenterSelector().apply {
            addClassPresenter(AllSubscriptionsFragmentRow::class.java, subscriptionsHeaderItemPresenter)
            addClassPresenter(ChannelFragmentRow::class.java, subscriptionsHeaderItemPresenter)
            addClassPresenter(SubscriptionsErrorFragmentRow::class.java, subscriptionsHeaderItemPresenter)
            addClassPresenter(SubscriptionsNoSubscriptionsFragmentRow::class.java, subscriptionsHeaderItemPresenter)
            addClassPresenter(SubscriptionsNotLoggedInFragmentRow::class.java, subscriptionsHeaderItemPresenter)
            addClassPresenter(SubscriptionsEmptyFragmentRow::class.java, subscriptionsHeaderItemPresenter)
            addClassPresenter(AllSubscriptionsSort::class.java, subscriptionsHeaderItemPresenter)
        }
    }

    override fun onTransitionPrepare() = false
}