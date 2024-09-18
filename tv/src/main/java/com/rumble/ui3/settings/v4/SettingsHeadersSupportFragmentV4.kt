package com.rumble.ui3.settings.v4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.widget.ClassPresenterSelector
import androidx.leanback.widget.FocusHighlightHelper
import androidx.leanback.widget.PresenterSelector
import com.rumble.R
import com.rumble.leanback.CustomHeadersSupportFragmentV4
import com.rumble.ui3.common.RumbleRowHeaderPresenter
import com.rumble.ui3.settings.CopyrightFragmentRow
import com.rumble.ui3.settings.CreditsFragmentRow
import com.rumble.ui3.settings.PrivacyPolicyFragmentRow
import com.rumble.ui3.settings.SettingsFragmentRow
import com.rumble.ui3.settings.TermsAndConditionsFragmentRow

class SettingsHeadersSupportFragmentV4(val appVersionString: String) : CustomHeadersSupportFragmentV4() {

    private val presenter: PresenterSelector = ClassPresenterSelector().apply {
        addClassPresenter(TermsAndConditionsFragmentRow::class.java, RumbleRowHeaderPresenter())
        addClassPresenter(PrivacyPolicyFragmentRow::class.java, RumbleRowHeaderPresenter())
        addClassPresenter(CopyrightFragmentRow::class.java, RumbleRowHeaderPresenter())
        addClassPresenter(CreditsFragmentRow::class.java, RumbleRowHeaderPresenter())
        addClassPresenter(SettingsFragmentRow::class.java, RumbleRowHeaderPresenter())
    }

    init {
        FocusHighlightHelper.setupHeaderItemFocusHighlight(bridgeAdapter, false)
        presenterSelector = presenter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        verticalGridView.isScrollEnabled = false
    }

    override fun onTransitionStart() {}

    override fun onTransitionEnd() {}

    override fun getFooter(inflater: LayoutInflater, viewGroup: ViewGroup): View? {
        val view = inflater.inflate(R.layout.lb_footer, viewGroup, true)
        view.findViewById<TextView>(R.id.app_version).text =
            "${view.context.getString(R.string.app_name)} v${appVersionString}"
        return view
    }
}