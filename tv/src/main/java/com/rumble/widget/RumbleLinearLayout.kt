package com.rumble.widget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

class RumbleLinearLayout : LinearLayout {

    companion object {
        private const val DEFAULT_FOCUS_INDEX = 0
    }

    public interface OnChildFocusedListener {
        fun onChildFocusedListener(child: View, focused: View)
    }

    var lastFocusIndex: Int = -1

    var onChildFocusedListener: OnChildFocusedListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )


    override fun onRequestFocusInDescendants(direction: Int, previouslyFocusedRect: Rect?): Boolean {
        if (childCount > 0) {
            val index: Int = if (lastFocusIndex in 0 until childCount) lastFocusIndex else DEFAULT_FOCUS_INDEX
            if (getChildAt(index).requestFocus(direction, previouslyFocusedRect)) {
                return true
            }
        }
        return super.onRequestFocusInDescendants(direction, previouslyFocusedRect)
    }

    override fun addFocusables(views: ArrayList<View>, direction: Int, focusableMode: Int) {
        if (direction == ViewGroup.FOCUS_UP ||
            direction == ViewGroup.FOCUS_DOWN
        ) {
            if (lastFocusIndex in 0 until childCount) {
                views.add(getChildAt(lastFocusIndex))
            } else if (childCount > 0) {
                views.add(getChildAt(DEFAULT_FOCUS_INDEX))
            }
        } else {
            super.addFocusables(views, direction, focusableMode)
        }
    }

    override fun requestChildFocus(child: View, focused: View) {
        super.requestChildFocus(child, focused)
        lastFocusIndex = indexOfChild(child)
        onChildFocusedListener?.onChildFocusedListener(child, focused)
    }
}