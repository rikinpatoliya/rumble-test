package com.rumble.ui3.settings.pages

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.leanback.widget.*
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.rumble.R
import com.rumble.domain.license.domain.domainmodel.Dependency
import com.rumble.domain.license.domain.domainmodel.LicenseReport
import com.rumble.ui3.common.v4.RumbleVerticalGridSupportFragmentV4
import com.rumble.ui3.web.WebviewActivityDirections


class CreditsFragment : RumbleVerticalGridSupportFragmentV4() {

    companion object {
        private const val NUMBER_OF_COLUMNS = 1
    }

    override val numberOfColumns: Int
        get() = NUMBER_OF_COLUMNS

    init {
        onItemViewClickedListener =
            OnItemViewClickedListener { _, item, _, _ ->
                item as Dependency
                item.moduleLicenseUrl?.let {
                     Navigation.findNavController(requireView()).navigate(WebviewActivityDirections.actionGlobalWebviewActivity(it))
                }
            }
    }

    override fun getPresenter() = CreditsPresenter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arrayObjectAdapter.clear()
        val objectArrayString: String = requireContext().resources.openRawResource(R.raw.credits).bufferedReader().use { it.readText() }
        val objectArray = Gson().fromJson(objectArrayString, LicenseReport::class.java)
        arrayObjectAdapter.addAll(0, objectArray.dependencies)
    }

    override fun leftMargin() : Int = resources.getDimensionPixelSize(R.dimen.credits_fragment_left_margin)

    override fun topMargin() : Int = resources.getDimensionPixelSize(R.dimen.credits_fragment_top_margin)
}