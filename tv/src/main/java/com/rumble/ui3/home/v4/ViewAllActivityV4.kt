package com.rumble.ui3.home.v4

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.rumble.R
import com.rumble.databinding.V4ActivityViewallBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class ViewAllActivityV4 : FragmentActivity() {

    private var _binding: V4ActivityViewallBinding? = null
    /** This property is only valid between onCreateView and onDestroyView. */
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.v4_activity_viewall)

        intent.extras?.let {
            val feedId = ViewAllActivityV4Args.fromBundle(it).feedId
            val feedTitle = ViewAllActivityV4Args.fromBundle(it).feedTitle

            val viewAllFragment = ViewAllFragmentV4.getInstance(feedId, feedTitle)
            supportFragmentManager.beginTransaction()
                .replace(R.id.viewAllContainer, viewAllFragment)
                .disallowAddToBackStack()
                .commit()
        }
    }
}