package com.rumble.ui3.web

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.rumble.databinding.V3FragmentWebViewBinding


class WebViewFragment : Fragment() {

    /***/
    private var _binding: V3FragmentWebViewBinding? = null
    /** This property is only valid between onCreateView and onDestroyView. */
    private val binding get() = _binding!!

    companion object {
        private const val BUNDLE_KEY_URL = "url"
        fun getInstance(url: String): WebViewFragment {
            val fragment = WebViewFragment()
            val bundle = Bundle()
            bundle.putString(BUNDLE_KEY_URL, url)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = V3FragmentWebViewBinding.inflate(inflater)

        binding.webView.webViewClient = WebViewClient()
        binding.webView.loadUrl(WebViewFragmentArgs.fromBundle(requireArguments()).url)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}