package com.rumble.ui3.subscriptions.v4

import androidx.fragment.app.Fragment
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.PageRow
import androidx.leanback.widget.Row
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.leanback.BrowseSupportFragment
import com.rumble.ui3.channel.details.v4.ChannelDetailsFragmentV4
import com.rumble.ui3.subscriptions.pages.list.HeaderItemWithData
import com.rumble.ui3.subscriptions.v4.fragments.SubscriptionsErrorFragmentV4
import com.rumble.ui3.subscriptions.v4.fragments.SubscriptionsNoSubscriptionsFragmentV4
import com.rumble.ui3.subscriptions.v4.fragments.SubscriptionsUserNotLoggedInFragmentV4
import com.rumble.ui3.subscriptions.v4.list.AllSubscriptionsFragmentV4
import javax.inject.Inject


class SubscriptionsFragmentsFactoryV4 @Inject constructor() :
    BrowseSupportFragment.FragmentFactory<Fragment>() {

    override fun createFragment(row: Any?): Fragment {
        return when (row as ISubscriptionsFragmentRow) {
            is SubscriptionsErrorFragmentRow -> SubscriptionsErrorFragmentV4()
            is SubscriptionsNoSubscriptionsFragmentRow -> SubscriptionsNoSubscriptionsFragmentV4()
            is SubscriptionsNotLoggedInFragmentRow -> SubscriptionsUserNotLoggedInFragmentV4()
            is AllSubscriptionsFragmentRow -> {
                AllSubscriptionsFragmentV4()
            }

            is ChannelFragmentRow -> {
                val headerIconItem = ((row as Row).headerItem as HeaderItemWithData)
                ChannelDetailsFragmentV4.getInstance(
                    (headerIconItem.data as ChannelDetailsEntity),
                    showLogo = false,
                    fromActivity = false,
                    isCachingSupported = true
                )
            }

            is SubscriptionsEmptyFragmentRow -> Fragment()
            is AllSubscriptionsSort -> Fragment()
        }
    }
}

sealed interface ISubscriptionsFragmentRow

class SubscriptionsEmptyFragmentRow(headerItem: HeaderItem) : PageRow(headerItem),
    ISubscriptionsFragmentRow

class SubscriptionsErrorFragmentRow(headerItem: HeaderItem) : PageRow(headerItem),
    ISubscriptionsFragmentRow

class SubscriptionsNoSubscriptionsFragmentRow(headerItem: HeaderItem) : PageRow(headerItem),
    ISubscriptionsFragmentRow

class SubscriptionsNotLoggedInFragmentRow(headerItem: HeaderItem) : PageRow(headerItem),
    ISubscriptionsFragmentRow

class AllSubscriptionsSort(headerItem: HeaderItem) : PageRow(headerItem), ISubscriptionsFragmentRow

class AllSubscriptionsFragmentRow(headerItem: HeaderItemWithData) : PageRow(headerItem),
    ISubscriptionsFragmentRow

class ChannelFragmentRow(headerItem: HeaderItemWithData) : PageRow(headerItem),
    ISubscriptionsFragmentRow