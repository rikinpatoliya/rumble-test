package com.rumble.ui3.subscriptions.v4

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.PageRow
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.paging.insertHeaderItem
import androidx.recyclerview.widget.DiffUtil
import com.rumble.R
import com.rumble.leanback.BrowseSupportFragment
import com.rumble.leanback.HeadersSupportFragment
import com.rumble.ui3.channel.details.v4.ChannelDetailsFragmentV4
import com.rumble.ui3.channel.details.v4.ChannelDetailsSharedHandler
import com.rumble.ui3.channel.details.v4.ChannelDetailsSharedViewModel
import com.rumble.ui3.subscriptions.FollowingSortDialogFragment
import com.rumble.ui3.subscriptions.SubscriptionsFragmentStates
import com.rumble.ui3.subscriptions.pages.list.AllSubscriptionState
import com.rumble.ui3.subscriptions.pages.list.HeaderItemWithData
import com.rumble.ui3.subscriptions.v4.list.SubscriptionsHeadersSupportFragmentV4
import com.rumble.util.PagingAdapter
import com.rumble.util.showAlert
import com.rumble.utils.RumbleUIUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

const val ROW_NO_SUBSCRIPTION = 0
const val ROW_NOT_LOGGED_IN = 1
const val ROW_ERROR = 2
const val ROW_EMPTY = 3

const val DEFAULT_HEADER_INDEX = 1

@AndroidEntryPoint
class SubscriptionsMainFragmentV4 : BrowseSupportFragment(), BrowseSupportFragment.MainFragmentAdapterProvider {

    @Inject
    lateinit var rumbleUIUtil: RumbleUIUtil

    private val viewModel: SubscriptionsViewModelV4 by viewModels()

    private val channelDetailsSharedViewModel: ChannelDetailsSharedHandler
            by activityViewModels<ChannelDetailsSharedViewModel>()

    private var sortDialogFragment: FollowingSortDialogFragment? = null

    @Inject
    lateinit var subscriptionsFragmentsFactory: SubscriptionsFragmentsFactoryV4

    private val fragmentAdapter: MainFragmentAdapter<SubscriptionsMainFragmentV4> =
        object : MainFragmentAdapter<SubscriptionsMainFragmentV4>(this) {}

    private val subscriptionsListHeadersSupportFragment by lazy {
        SubscriptionsHeadersSupportFragmentV4(
            rumbleUIUtil,
            pagingDataAdapter
        )
    }

    private val headersDisableTime: Long = 500
    private val headersAdapter = ArrayObjectAdapter(ListRowPresenter()).apply {
        // No Subscriptions
        add(
            SubscriptionsNoSubscriptionsFragmentRow(
                HeaderItem(SubscriptionsNoSubscriptionsFragmentRow::class.simpleName!!)
            )
        )

        // Not Logged In
        add(
            SubscriptionsNotLoggedInFragmentRow(
                HeaderItem(SubscriptionsNotLoggedInFragmentRow::class.simpleName!!)
            )
        )

        // error
        add(
            SubscriptionsErrorFragmentRow(
                HeaderItem(SubscriptionsErrorFragmentRow::class.simpleName!!)
            )
        )

        add(
            SubscriptionsEmptyFragmentRow(
                HeaderItem(SubscriptionsEmptyFragmentRow::class.simpleName!!)
            )
        )

    }

    private val pagingDataAdapter: PagingAdapter<PageRow> = PagingAdapter(ListRowPresenter(),
        object : DiffUtil.ItemCallback<PageRow>() {
            override fun areItemsTheSame(
                oldItem: PageRow,
                newItem: PageRow,
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: PageRow,
                newItem: PageRow,
            ): Boolean {
                return oldItem == newItem
            }
        })

    init {
        progressBarManager.initialDelay = 0
    }

    override fun onCreateHeadersSupportFragment(): HeadersSupportFragment = subscriptionsListHeadersSupportFragment

    override fun getContainerListClosingType() = ContainerListClosingType.MARGIN

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        parentFragmentManager.showAlert(getString(R.string.error_fragment_message), true)
        viewModel.onError(throwable)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        val browseHeadersDock = view?.findViewById<View>(R.id.browse_headers_dock)
        browseHeadersDock?.setPadding(0)
        val scaleFrame = view?.findViewById<View>(R.id.scale_frame)
        scaleFrame?.setPadding(0, 0, 0, 0)

        channelDetailsSharedViewModel.onEmptyCache()

        AllSubscriptionState.subscriptionsVideosPagingDataMap = null
        mainFragmentRegistry.registerFragment(SubscriptionsErrorFragmentRow::class.java, subscriptionsFragmentsFactory)
        mainFragmentRegistry.registerFragment(
            SubscriptionsNoSubscriptionsFragmentRow::class.java,
            subscriptionsFragmentsFactory
        )
        mainFragmentRegistry.registerFragment(
            SubscriptionsNotLoggedInFragmentRow::class.java,
            subscriptionsFragmentsFactory
        )
        mainFragmentRegistry.registerFragment(AllSubscriptionsFragmentRow::class.java, subscriptionsFragmentsFactory)
        mainFragmentRegistry.registerFragment(ChannelFragmentRow::class.java, subscriptionsFragmentsFactory)
        mainFragmentRegistry.registerFragment(SubscriptionsEmptyFragmentRow::class.java, subscriptionsFragmentsFactory)
        mainFragmentRegistry.registerFragment(AllSubscriptionsSort::class.java, subscriptionsFragmentsFactory)

        setBrowseTransitionListener(object : BrowseTransitionListener() {
            override fun onHeadersTransitionStart(withHeaders: Boolean) {
                super.onHeadersTransitionStart(withHeaders)
                SubscriptionState.showingHeaders = withHeaders

                if (withHeaders.not()) {
                    childFragmentManager.fragments.forEach { fragment ->
                        if (fragment is ChannelDetailsFragmentV4) {
                            fragment.lastFocusFromSubscriptionListScreen()
                        }
                    }
                }
            }

            override fun onHeadersTransitionStop(withHeaders: Boolean) {
                super.onHeadersTransitionStop(withHeaders)
                SubscriptionState.showingHeaders = withHeaders
                if (withHeaders) {
                    AllSubscriptionState.lastFocusedView = AllSubscriptionState.LastFocusedView.NONE
                }
            }
        })

        viewLifecycleOwner.lifecycleScope.launch(errorHandler) {
            viewModel.uiState.collect { state ->
                progressBarManager.hide()
                when (state) {
                    is SubscriptionsFragmentStates.Error -> {
                        brandColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
                        adapter = headersAdapter
                        setSelectedPosition(ROW_ERROR, false)
                        headersState = HEADERS_HIDDEN
                        Handler(Looper.getMainLooper()).postDelayed({
                            headersState = HEADERS_DISABLED
                        }, headersDisableTime)
                    }

                    is SubscriptionsFragmentStates.Loading -> {
                        progressBarManager.show()
                        adapter = headersAdapter
                        setSelectedPosition(ROW_EMPTY, false)
                        brandColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
                        headersState = HEADERS_DISABLED
                    }

                    is SubscriptionsFragmentStates.NoSubscriptions -> {
                        brandColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
                        adapter = headersAdapter
                        setSelectedPosition(ROW_NO_SUBSCRIPTION, false)
                        headersState = HEADERS_HIDDEN
                        Handler(Looper.getMainLooper()).postDelayed({
                            headersState = HEADERS_DISABLED
                        }, headersDisableTime)
                    }

                    is SubscriptionsFragmentStates.NotLoggedIn -> {
                        brandColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
                        adapter = headersAdapter
                        setSelectedPosition(ROW_NOT_LOGGED_IN, false)
                        headersState = HEADERS_HIDDEN
                        Handler(Looper.getMainLooper()).postDelayed({
                            headersState = HEADERS_DISABLED
                        }, headersDisableTime)
                    }

                    is SubscriptionsFragmentStates.SubscriptionsList -> {
                        viewLifecycleOwner.lifecycleScope.launch(errorHandler) {
                            pagingDataAdapter.submitData(
                                PagingData.from(state.sortedList.map {
                                    ChannelFragmentRow(
                                        HeaderItemWithData(
                                            it.channelTitle,
                                            it
                                        )
                                    ) as PageRow
                                })
                                    .insertHeaderItem(
                                        item = AllSubscriptionsFragmentRow(
                                            HeaderItemWithData(
                                                name = getString(R.string.subscriptions_all_subscriptions_label),
                                                data = state.sortedList.size
                                            )
                                        )
                                    )
                                    .insertHeaderItem(
                                        item = AllSubscriptionsSort(
                                            HeaderItemWithData(
                                                data = 0,
                                                name = getString(R.string.subscriptions_all_subscriptions_sort_label)
                                            )
                                        )
                                    )
                            )

                            brandColor = ContextCompat.getColor(requireContext(), R.color.gray_950_60_percent)
                            adapter = pagingDataAdapter
                            headersState = HEADERS_ENABLED
                            Handler(Looper.getMainLooper()).postDelayed({
                                headersState = HEADERS_ENABLED
                            }, headersDisableTime)

                            if (state.sortedList.isEmpty()) {
                                setSelectedPosition(ROW_NO_SUBSCRIPTION, false)
                            } else {
                                setSelectedPosition(DEFAULT_HEADER_INDEX, false)
                            }
                        }
                    }
                }
            }
        }
        viewModel.getUiState()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.vmEvents.collect { event ->
                when (event) {
                    SubscriptionsVmEvent.RefreshSubscriptions -> pagingDataAdapter.refresh()
                }
            }
        }
    }

    private fun showSortDialog() {
        val existingFragment = parentFragmentManager.findFragmentByTag("SortDialog") as? FollowingSortDialogFragment
        if (existingFragment == null) {
            sortDialogFragment = FollowingSortDialogFragment(
                sortTypeFlow = viewModel.sortType,
                onSortSelected = {
                    viewModel.onSortSelected(it)
                },
                onDismissed = {
                    setSelectedPosition(DEFAULT_HEADER_INDEX, false)
                    sortDialogFragment = null
                }
            )

            sortDialogFragment?.show(parentFragmentManager, "SortDialog")
        }
    }

    override fun isChildFragment() = true

    override fun getMainFragmentAdapter(): MainFragmentAdapter<SubscriptionsMainFragmentV4> = fragmentAdapter
    fun onBackInSubscriptions() {
        startHeadersTransitionInternal(true)
    }

    override fun onPause() {
        super.onPause()
        subscriptionsListHeadersSupportFragment.setOnHeaderViewSelectedListener(null)
    }

    override fun onResume() {
        super.onResume()

        subscriptionsListHeadersSupportFragment.setOnHeaderViewSelectedListener { _, row ->
            if (row !is AllSubscriptionsSort) {
                val position = headersSupportFragment.selectedPosition
                onRowSelected(position)
            }
        }

        subscriptionsListHeadersSupportFragment.setOnHeaderClickedListener { viewHolder, row ->
            if (row is AllSubscriptionsSort) {
                showSortDialog()
            } else {
                mainFragment.requireView().requestFocus()
            }
        }
    }
}