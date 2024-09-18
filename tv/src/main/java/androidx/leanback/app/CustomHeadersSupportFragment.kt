package androidx.leanback.app

import com.rumble.R

open class CustomHeadersSupportFragment : HeadersSupportFragment() {

    override fun getLayoutResourceId(): Int = R.layout.v3_headers_fragment

    override fun setAlignment(windowAlignOffsetTop: Int) {}

    override fun setOnHeaderViewSelectedListener(listener: OnHeaderViewSelectedListener?) {
        super.setOnHeaderViewSelectedListener(listener)
    }
}