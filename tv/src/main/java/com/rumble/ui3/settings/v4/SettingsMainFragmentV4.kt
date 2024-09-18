package com.rumble.ui3.settings.v4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.fragment.app.viewModels
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRowPresenter
import androidx.lifecycle.lifecycleScope
import com.rumble.R
import com.rumble.leanback.BrowseSupportFragment
import com.rumble.leanback.HeadersSupportFragment
import com.rumble.network.di.AppVersion
import com.rumble.ui3.settings.CopyrightFragmentRow
import com.rumble.ui3.settings.CreditsFragmentRow
import com.rumble.ui3.settings.ISettingsFragmentRow
import com.rumble.ui3.settings.PrivacyPolicyFragmentRow
import com.rumble.ui3.settings.SettingsFragmentRow
import com.rumble.ui3.settings.SettingsFragmentsFactory
import com.rumble.ui3.settings.TermsAndConditionsFragmentRow
import com.rumble.ui3.settings.pages.SettingsHandler
import com.rumble.ui3.settings.pages.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SettingsMainFragmentV4 : BrowseSupportFragment(), BrowseSupportFragment.MainFragmentAdapterProvider {
    private val viewModel: SettingsHandler by viewModels<SettingsViewModel>()

    @Inject
    @AppVersion
    lateinit var appVersion: String

    @Inject
    lateinit var settingsFragmentsFactory: SettingsFragmentsFactory

    private val fragmentAdapter: MainFragmentAdapter<SettingsMainFragmentV4> =
        object : MainFragmentAdapter<SettingsMainFragmentV4>(this) {}

    private val settingsHeadersSupportFragment by lazy { SettingsHeadersSupportFragmentV4(appVersion) }

    private lateinit var settingsFragmentRow: ISettingsFragmentRow

    override fun onCreateHeadersSupportFragment(): HeadersSupportFragment = settingsHeadersSupportFragment

    override fun getContainerListClosingType() = ContainerListClosingType.MARGIN

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
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createHeaders()

        brandColor = ContextCompat.getColor(requireContext(), R.color.gray_950_60_percent)

        mainFragmentRegistry.registerFragment(TermsAndConditionsFragmentRow::class.java, settingsFragmentsFactory)
        mainFragmentRegistry.registerFragment(PrivacyPolicyFragmentRow::class.java, settingsFragmentsFactory)
        mainFragmentRegistry.registerFragment(CopyrightFragmentRow::class.java, settingsFragmentsFactory)
        mainFragmentRegistry.registerFragment(CreditsFragmentRow::class.java, settingsFragmentsFactory)
        mainFragmentRegistry.registerFragment(SettingsFragmentRow::class.java, settingsFragmentsFactory)

        headersSupportFragment.setOnHeaderViewSelectedListener { viewHolder, row ->
            onRowSelected(headersSupportFragment.selectedPosition)
        }
    }

    override fun onStart() {
        super.onStart()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                val adapter = adapter as ArrayObjectAdapter
                val settingsAdded = adapter.indexOf(settingsFragmentRow) != -1
                if (state.settingsVisible && settingsAdded.not()) {
                    adapter.add(settingsFragmentRow)
                } else if (state.settingsVisible.not() && settingsAdded) {
                    adapter.remove(settingsFragmentRow)
                }
            }
        }
    }

    override fun isChildFragment() = true

    override fun getMainFragmentAdapter(): MainFragmentAdapter<SettingsMainFragmentV4> = fragmentAdapter

    private fun createHeaders() {

        val headersAdapter = ArrayObjectAdapter(ListRowPresenter())

        headersAdapter.add(
            TermsAndConditionsFragmentRow(
                HeaderItem(
                    getString(R.string.settings_fragment_terms_conditions_label)
                )
            )
        )
        headersAdapter.add(
            PrivacyPolicyFragmentRow(
                HeaderItem(
                    getString(R.string.settings_fragment_privacy_policy_label)
                )
            )
        )

        headersAdapter.add(
            CopyrightFragmentRow(
                HeaderItem(
                    getString(R.string.settings_fragment_copyright_label)
                )
            )
        )

        headersAdapter.add(
            CreditsFragmentRow(
                HeaderItem(
                    getString(R.string.settings_fragment_credits_label)
                )
            )
        )

        settingsFragmentRow = SettingsFragmentRow(
            HeaderItem(
                getString(R.string.settings_fragment_settings_label)
            )
        )

        adapter = headersAdapter
    }

}