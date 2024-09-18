package com.rumble.ui3.settings

import androidx.fragment.app.Fragment
import com.rumble.leanback.BrowseSupportFragment
import com.rumble.ui3.settings.pages.CopyrightFragment
import com.rumble.ui3.settings.pages.CreditsFragment
import com.rumble.ui3.settings.pages.PrivacyPolicyFragment
import com.rumble.ui3.settings.pages.SettingsFragment
import com.rumble.ui3.settings.pages.TermsAndConditionsFragment
import javax.inject.Inject

class SettingsFragmentsFactory @Inject constructor() : BrowseSupportFragment.FragmentFactory<Fragment>() {

    override fun createFragment(row: Any?): Fragment {
        return when (row as ISettingsFragmentRow) {
            is TermsAndConditionsFragmentRow -> TermsAndConditionsFragment()
            is PrivacyPolicyFragmentRow -> PrivacyPolicyFragment()
            is CopyrightFragmentRow -> CopyrightFragment()
            is CreditsFragmentRow -> CreditsFragment()
            is SettingsFragmentRow -> SettingsFragment()
        }
    }
}