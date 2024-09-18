package com.rumble.ui3.settings

import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.PageRow


sealed interface ISettingsFragmentRow

class TermsAndConditionsFragmentRow(headerItem: HeaderItem) : PageRow(headerItem), ISettingsFragmentRow
class PrivacyPolicyFragmentRow(headerItem: HeaderItem) : PageRow(headerItem), ISettingsFragmentRow
class CopyrightFragmentRow(headerItem: HeaderItem) : PageRow(headerItem), ISettingsFragmentRow
class CreditsFragmentRow(headerItem: HeaderItem) : PageRow(headerItem), ISettingsFragmentRow
class SettingsFragmentRow(headerItem: HeaderItem) : PageRow(headerItem), ISettingsFragmentRow