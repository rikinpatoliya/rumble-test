package com.rumble.ui3.channel.details.more

import android.app.Activity
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import com.rumble.R
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UpdateChannelSubscriptionAction
import com.rumble.network.session.SessionManager
import com.rumble.ui3.channel.details.v4.ChannelDetailsViewModelV4
import com.rumble.util.Constant.TAG_CHANNEL
import com.rumble.util.isNetworkConnected
import com.rumble.util.showAlert
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * Activity that showcases different aspects of GuidedStepFragments.
 */
@AndroidEntryPoint
class GuidedStepActivity : FragmentActivity() {

    /***/
    private val viewModel: ChannelDetailsViewModelV4 by viewModels()

    inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let { intent ->
            viewModel.channelObject = intent.parcelable(TAG_CHANNEL)
            viewModel.channelObject?.channelId?.let {
                viewModel.channelId = it
            }
        }

        if (null == savedInstanceState) {
            GuidedStepSupportFragment.addAsRoot(this, FirstStepFragment(), android.R.id.content)
        }
    }

    @AndroidEntryPoint
    class FirstStepFragment : GuidedStepSupportFragment() {

        /***/
        private val viewModel: ChannelDetailsViewModelV4 by activityViewModels()

        @Inject
        lateinit var sessionManager: SessionManager

        private val errorHandler = CoroutineExceptionHandler { _, throwable ->
            viewModel.onError(throwable)
        }

        override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
            val title = getString(R.string.channel_details_more_title)
            return Guidance(title, null, null, null)
        }

        override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
            if (viewModel.channelObject?.blocked == true){
                actions.add(
                    GuidedAction.Builder(context)
                        .id(UNBLOCK).title(R.string.unblock).build())
            }else{
                actions.add(
                    GuidedAction.Builder(context)
                        .id(BLOCK).title(R.string.channel_details_more_block_title).build())
            }
            actions.add(
                GuidedAction.Builder(context)
                    .id(REPORT).title(R.string.channel_details_more_report_title).build()
            )
            actions.add(
                GuidedAction.Builder(context)
                    .id(CANCEL).title(R.string.channel_details_more_cancel_title).build()
            )
        }

        override fun onGuidedActionClicked(action: GuidedAction) {
            val isCookieExist = runBlocking(errorHandler) {
                sessionManager.cookiesFlow.first().isNotEmpty()
            }

            when (action.id) {
                BLOCK -> {
                    if (requireContext().isNetworkConnected) {
                        if (isCookieExist) {
                            val item = viewModel.blockAndUnblock(UpdateChannelSubscriptionAction.BLOCK)
                            item?.let { channelState?.updateChannelState(it) }
                            finishGuidedStepSupportFragments()
                        } else {
                            fragmentManager?.showAlert(
                                getString(R.string.channel_details_user_not_logged_in_block_channel),
                                false
                            )
                        }
                    } else {
                        fragmentManager?.showAlert(getString(R.string.no_internet), true)
                    }
                }
                UNBLOCK -> {
                    if (requireContext().isNetworkConnected){
                        if (isCookieExist) {
                            val item = viewModel.blockAndUnblock(UpdateChannelSubscriptionAction.UNBLOCK)
                            item?.let { channelState?.updateChannelState(it) }
                            finishGuidedStepSupportFragments()
                        } else {
                            fragmentManager?.showAlert(
                                getString(R.string.channel_details_user_not_logged_in_block_channel),
                                false
                            )
                        }
                    } else {
                        fragmentManager?.showAlert(getString(R.string.no_internet), true)
                    }
                }

                REPORT -> {
                    add(requireFragmentManager(), SecondStepFragment())
                }

                else -> {
                    finishGuidedStepSupportFragments()
                }
            }
        }
    }
    @AndroidEntryPoint
    class SecondStepFragment : GuidedStepSupportFragment() {

        /***/
        private val viewModel: ChannelDetailsViewModelV4 by activityViewModels()

        @Inject
        lateinit var sessionManager: SessionManager

        private val errorHandler = CoroutineExceptionHandler { _, throwable ->
            viewModel.onError(throwable)
        }

        override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
            val title = getString(R.string.channel_details_more_report_screen_title)
            val description = getString(R.string.channel_details_more_report_screen_subtitle)
            return Guidance(title, description, null, null)
        }

        override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
            actions.add(
                GuidedAction.Builder(context)
                    .id(SPAM).title(R.string.channel_details_more_report_screen_spam)
                    .build()
            )
            actions.add(
                GuidedAction.Builder(context)
                    .id(INAPPROPRIATE)
                    .title(R.string.channel_details_more_report_screen_inappropriate).build()
            )
            actions.add(
                GuidedAction.Builder(context)
                    .id(VIOLATES_COPYRIGHT.toLong())
                    .title(R.string.channel_details_more_report_screen_copyright).build()
            )
            actions.add(
                GuidedAction.Builder(context)
                    .id(VIOLATES_TERMS)
                    .title(R.string.channel_details_more_report_screen_terms).build()
            )
            actions.add(
                GuidedAction.Builder(context)
                    .id(CANCEL).title(R.string.channel_details_more_cancel_title).build()
            )
        }

        override fun onGuidedActionClicked(action: GuidedAction) {
            if (requireContext().isNetworkConnected.not() && action.id != CANCEL) {
                fragmentManager?.showAlert(getString(R.string.no_internet), true)
                return
            }

            val isCookieExist = runBlocking(errorHandler) {
                sessionManager.cookiesFlow.first().isNotEmpty()
            }

            if (isCookieExist.not() && action.id != CANCEL) {
                fragmentManager?.showAlert(
                    getString(R.string.channel_details_user_not_logged_in_report_channel),
                    false
                )
                return
            }

            when (action.id) {
                SPAM -> {
                    viewModel.reportAsSpam()
                    channelState?.channelReported()
                    finishGuidedStepSupportFragments()
                }

                INAPPROPRIATE -> {
                    viewModel.reportAsInappropriate()
                    channelState?.channelReported()
                    finishGuidedStepSupportFragments()
                }
                VIOLATES_COPYRIGHT -> {
                    viewModel.reportAsViolatingCopyright()
                    channelState?.channelReported()
                    finishGuidedStepSupportFragments()
                }
                VIOLATES_TERMS -> {
                    viewModel.reportAsViolatingTerms()
                    channelState?.channelReported()
                    finishGuidedStepSupportFragments()
                }

                CANCEL -> {
                    fragmentManager?.popBackStack()
                }
            }

        }
    }

    companion object {

        private const val BLOCK     = 1L
        private const val REPORT    = 2L
        private const val CANCEL    = 3L
        private const val UNBLOCK   = 4L

        private const val SPAM                  = 10L
        private const val INAPPROPRIATE         = 11L
        private const val VIOLATES_COPYRIGHT    = 12L
        private const val VIOLATES_TERMS        = 13L

        var channelState: BlockStateListener? = null
        fun launchActivity(
            activity: Activity,
            channel: CreatorEntity?,
            blockState: BlockStateListener
        ) {
            channelState = blockState
            val intent = Intent(activity, GuidedStepActivity::class.java)
            intent.putExtra(TAG_CHANNEL, channel)
            ActivityCompat.startActivity(
                activity,
                intent,
                null
            )
        }
    }
}