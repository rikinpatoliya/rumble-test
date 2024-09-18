package com.rumble.ui3.web

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.rumble.R
import com.rumble.databinding.ActivityWebviewBinding

class WebviewActivity : FragmentActivity() {

    private var _binding: ActivityWebviewBinding? = null
    /** This property is only valid between onCreateView and onDestroyView. */
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_webview)

        intent.extras?.let {
            val url = WebviewActivityArgs.fromBundle(it).url
            val webviewFragment = WebViewFragment.getInstance(url)
            supportFragmentManager.beginTransaction()
                .replace(R.id.webviewContainer, webviewFragment)
                .disallowAddToBackStack()
                .commit()

        }
    }
}