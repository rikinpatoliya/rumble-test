package com.rumble.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnFocusChangeListener
import android.view.animation.AccelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.rumble.R
import com.rumble.databinding.V4MenuLayoutBinding
import com.rumble.utils.RumbleUIUtil
import com.rumble.widget.TextDrawable
import timber.log.Timber

class LeftMenuView : ConstraintLayout {
    companion object {
        const val SEARCH_MENU = 1
        const val HOME_MENU = 2
        const val LIVE_MENU = 3
        const val BROWSE_MENU = 4
        const val SUBSCRIPTIONS_MENU = 6
        const val SETTING_MENU = 7
        const val LOGIN_MENU = 8
        const val LIBRARY_MENU = 9
        var isAnimating = false

        fun animateView(view: View, valueAnimator: ValueAnimator) {
            valueAnimator.addUpdateListener { animation ->
                view.layoutParams.width = animation.animatedValue as Int
                view.requestLayout()
            }

            valueAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    isAnimating = false // reset flag to false after animation completes
                }

                override fun onAnimationStart(animation: Animator, isReverse: Boolean) {
                    super.onAnimationStart(animation, isReverse)
                    isAnimating = true
                }
            })

            valueAnimator.interpolator = AccelerateInterpolator()
            valueAnimator.duration = view.context.resources.getInteger(R.integer.menu_headers_animation_duration).toLong()
            valueAnimator.start()
        }
    }

    private var menuItemClick: MenuItemClickListener? = null
    private var currentSelected: Int = HOME_MENU
    private var mContext: Context? = null

    private var _binding: V4MenuLayoutBinding? = null
    private val binding get() = _binding!!

    interface MenuItemClickListener {
        fun menuItemClick(menuId: Int)
        fun menuItemFocus(menuId: Int)
    }

    constructor(context: Context) : super(context) {
        setupMenuUI(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setupMenuUI(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setupMenuUI(context)
    }

    private fun setupMenuUI(context: Context) {
        this.mContext = context
        this.menuItemClick = context as MenuItemClickListener
        setBackgroundColor(ContextCompat.getColor(this.context, R.color.gray_950_80_percent))
        _binding = V4MenuLayoutBinding.inflate(LayoutInflater.from(context), this, true)

        val clickListener = OnClickListener { view ->
            when (view) {
                binding.menuSearchLayout -> menuItemClick?.menuItemClick(SEARCH_MENU)
                binding.menuHomeLayout -> menuItemClick?.menuItemClick(HOME_MENU)
                binding.menuLiveLayout -> menuItemClick?.menuItemClick(LIVE_MENU)
                binding.menuBrowseLayout -> menuItemClick?.menuItemClick(BROWSE_MENU)
                binding.menuLibraryLayout -> menuItemClick?.menuItemClick(LIBRARY_MENU)
                binding.menuSubscriptionsLayout -> menuItemClick?.menuItemClick(SUBSCRIPTIONS_MENU)
                binding.menuSettingsLayout -> menuItemClick?.menuItemClick(SETTING_MENU)
                binding.menuLoginLayout -> menuItemClick?.menuItemClick(LOGIN_MENU)
            }
        }

        val onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            binding.menuHomeImage.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.white))
            binding.menuSearchImage.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.white))
            binding.menuLiveImage.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.white))
            binding.menuBrowseImage.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.white))
            binding.menuLibraryLayoutImage.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.white))
            binding.menuSubscriptionsImage.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.white))
            binding.menuSettingsImage.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.white))
            binding.menuHomeLayout.isSelected = false
            binding.menuSearchLayout.isSelected = false
            binding.menuLiveLayout.isSelected = false
            binding.menuBrowseLayout.isSelected = false
            binding.menuSubscriptionsLayout.isSelected = false
            binding.menuLibraryLayout.isSelected = false
            binding.menuSettingsLayout.isSelected = false
            binding.menuLoginLayout.isSelected = false
            when (view) {
                binding.menuSearchLayout -> {
                    binding.menuSearchImage.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this.context,
                            R.color.rumble_green
                        )
                    )
                    menuItemClick?.menuItemFocus(SEARCH_MENU)
                    view.isSelected = true
                }

                binding.menuHomeLayout -> {
                    binding.menuHomeImage.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this.context,
                            R.color.rumble_green
                        )
                    )
                    menuItemClick?.menuItemFocus(HOME_MENU)
                    view.isSelected = true
                }

                binding.menuLiveLayout -> {
                    binding.menuLiveImage.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this.context,
                            R.color.rumble_green
                        )
                    )
                    menuItemClick?.menuItemFocus(LIVE_MENU)
                    view.isSelected = true
                }

                binding.menuBrowseLayout -> {
                    binding.menuBrowseImage.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this.context,
                            R.color.rumble_green
                        )
                    )
                    menuItemClick?.menuItemFocus(BROWSE_MENU)
                    view.isSelected = true
                }

                binding.menuLibraryLayout -> {
                    binding.menuLibraryLayoutImage.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this.context,
                            R.color.rumble_green
                        )
                    )
                    menuItemClick?.menuItemFocus(LIBRARY_MENU)
                    view.isSelected = true
                }

                binding.menuSubscriptionsLayout -> {
                    binding.menuSubscriptionsImage.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this.context,
                            R.color.rumble_green
                        )
                    )
                    menuItemClick?.menuItemFocus(SUBSCRIPTIONS_MENU)
                    view.isSelected = true
                }

                binding.menuSettingsLayout -> {
                    binding.menuSettingsImage.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this.context,
                            R.color.rumble_green
                        )
                    )
                    menuItemClick?.menuItemFocus(SETTING_MENU)
                    view.isSelected = true
                }

                binding.menuLoginLayout -> {
                    menuItemClick?.menuItemFocus(LOGIN_MENU)
                    view.isSelected = true
                }
            }
        }

        binding.onClickListener = clickListener
        binding.onFocusChangeListener = onFocusChangeListener
    }

    fun focusCurrentSelectedMenu() {
        when (currentSelected) {
            SEARCH_MENU -> {
                binding.menuSearchLayout.apply {
                    requestFocus()
                }
            }

            HOME_MENU -> {
                binding.menuHomeLayout.apply {
                    requestFocus()
                }
            }

            LIVE_MENU -> {
                binding.menuLiveLayout.apply {
                    requestFocus()
                }
            }

            BROWSE_MENU -> {
                binding.menuBrowseLayout.apply {
                    requestFocus()
                }
            }

            LIBRARY_MENU -> {
                binding.menuLibraryLayout.apply {
                    requestFocus()
                }
            }

            SUBSCRIPTIONS_MENU -> {
                binding.menuSubscriptionsLayout.apply {
                    requestFocus()
                }
            }

            SETTING_MENU -> {
                binding.menuSettingsLayout.apply {
                    requestFocus()
                }
            }

            LOGIN_MENU -> {
                binding.menuLoginLayout.apply {
                    requestFocus()
                }
            }
        }
    }

    fun setCurrentSelected(currentSelected: Int) {
        this.currentSelected = currentSelected
    }

    fun getCurrentSelected(): Int {
        return currentSelected
    }

    fun highlightCurrentSelectedMenu(setLayoutBg: Boolean) {
        when (currentSelected) {
            SEARCH_MENU -> {
                if (setLayoutBg){
                    binding.menuSearchLayout.background = ContextCompat.getDrawable(this.context, R.drawable.v4_menu_bg_selected)
                }
                binding.menuSearchImage.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this.context,
                        R.color.rumble_green
                    )
                )
            }

            HOME_MENU -> {
                if (setLayoutBg){
                    binding.menuHomeLayout.background = ContextCompat.getDrawable(this.context, R.drawable.v4_menu_bg_selected)
                }
                binding.menuHomeImage.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this.context,
                        R.color.rumble_green
                    )
                )
            }

            LIVE_MENU -> {
                if (setLayoutBg){
                    binding.menuLiveLayout.background = ContextCompat.getDrawable(this.context, R.drawable.v4_menu_bg_selected)
                }
                binding.menuLiveImage.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this.context,
                        R.color.rumble_green
                    )
                )
            }

            BROWSE_MENU -> {
                if (setLayoutBg){
                    binding.menuBrowseLayout.background = ContextCompat.getDrawable(this.context, R.drawable.v4_menu_bg_selected)
                }
                binding.menuBrowseImage.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this.context,
                        R.color.rumble_green
                    )
                )
            }

            SUBSCRIPTIONS_MENU -> {
                if (setLayoutBg){
                    binding.menuSubscriptionsLayout.background = ContextCompat.getDrawable(this.context, R.drawable.v4_menu_bg_selected)
                }
                binding.menuSubscriptionsImage.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this.context,
                        R.color.rumble_green
                    )
                )
            }

            SETTING_MENU -> {
                if (setLayoutBg){
                    binding.menuSettingsLayout.background = ContextCompat.getDrawable(this.context, R.drawable.v4_menu_bg_selected)
                }
                binding.menuSettingsImage.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this.context,
                        R.color.rumble_green
                    )
                )
            }

            LOGIN_MENU -> {
                binding.menuLoginLayout.background =
                    ContextCompat.getDrawable(this.context, R.drawable.v4_menu_bg_selected)
                binding.menuLoginImage.isSelected = true
            }
        }
    }

    private fun updateMenusText(showMenusText: Boolean) {
        binding.menuSearchText.apply { if (showMenusText) visible(true) else invisible(true) }
        binding.menuHomeText.apply { if (showMenusText) visible(true) else invisible(true) }
        binding.menuLiveText.apply { if (showMenusText) visible(true) else invisible(true) }
        binding.menuBrowseText.apply { if (showMenusText) visible(true) else invisible(true) }
        binding.menuLibraryText.apply { if (showMenusText) visible(true) else invisible(true) }
        binding.menuSubscriptionsText.apply { if (showMenusText) visible(true) else invisible(true) }
        binding.menuSettingsText.apply { if (showMenusText) visible(true) else invisible(true) }
        binding.menuLoginText.apply { if (showMenusText) visible(true) else invisible(true) }
    }

    fun setupMenuExpandedUI() {
        Handler(Looper.getMainLooper()).postDelayed({
            updateMenusText(true)
            changeMenuFocusStatus(true)
            focusCurrentSelectedMenu()
        }, 300)
    }

    fun setupMenuClosedUI() {
        updateMenusText(false)
        changeMenuFocusStatus(false)
    }

    private fun changeMenuFocusStatus(status: Boolean) {
        val count = childCount
        for (i in 0 until count) {
            val childView = getChildAt(i)
            childView.apply {
                isFocusable = status
                isFocusableInTouchMode = status
                if (!status) {
                    clearFocus()
                    highlightCurrentSelectedMenu(true)
                } else {
                    setBackgroundColor(0)
                    background = ContextCompat.getDrawable(
                        this.context,
                        R.drawable.v3_selector_main_menu_user_item_background
                    )
                    changeMenuColor()
                }
            }
        }
    }

    private fun changeMenuColor() {
        when (currentSelected) {
            SEARCH_MENU -> {
                binding.menuSearchImage.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.white))
            }

            HOME_MENU -> {
                binding.menuHomeImage.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.white))
            }

            LIVE_MENU -> {
                binding.menuLiveImage.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.white))
            }

            BROWSE_MENU -> {
                binding.menuBrowseImage.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.white))
            }

            SUBSCRIPTIONS_MENU -> {
                binding.menuSubscriptionsImage.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.white))
            }

            SETTING_MENU -> {
                binding.menuSettingsImage.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.white))
            }

            LIBRARY_MENU -> {
                binding.menuLiveImage.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.white))
            }
        }
    }

    fun setupDefaultMenu(menuId: Int) {
        currentSelected = menuId
        changeMenuFocusStatus(false)
    }

    fun changeFocusTo(menuId: Int) {
        resetMenus()
        currentSelected = menuId
        highlightCurrentSelectedMenu(true)
    }

    fun bindUserName(userName: String, rumbleUIUtil: RumbleUIUtil) {
        Timber.d("bindUserName: $userName")
        if (userName.isNotEmpty()) {
            binding.menuLoginText.text = userName
            binding.menuLoginImage.setImageDrawable(
                TextDrawable.builder()
                    .beginConfig()
                    .width(context.resources.getDimensionPixelSize(R.dimen.icons_sub_size))
                    .height(context.resources.getDimensionPixelSize(R.dimen.icons_sub_size))
                    .useFont(Typeface.DEFAULT)
                    .fontSize(context.resources.getDimensionPixelSize(R.dimen.icons_sub_text_size))
                    .bold()
                    .toUpperCase()
                    .endConfig()
                    .buildRound(
                        binding.menuLoginText.text.first().toString(),
                        ContextCompat.getColor(
                            context,
                            rumbleUIUtil.getPlaceholderColorResId(binding.menuLoginText.text.toString())
                        )
                    )
            )
        } else {
            binding.menuLoginText.text = context.getString(R.string.headers_fragment_login_label)
            binding.menuLoginImage.setImageResource(R.drawable.v3_ic_login)
        }
    }

    fun bindUserAvatar(avatarUrl: String) {
        Timber.d("bindUserAvatar: $avatarUrl")
        if (avatarUrl.isNotEmpty()) {
            Glide.with(context)
                .load(avatarUrl)
                .circleCrop()
                .into(binding.menuLoginImage)
        }
    }

    private fun resetMenus() {
        val color = ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.white))
        binding.menuSearchImage.imageTintList = color
        binding.menuSearchLayout.setBackgroundColor(0)

        binding.menuHomeImage.imageTintList = color
        binding.menuHomeLayout.setBackgroundColor(0)

        binding.menuLiveImage.imageTintList = color
        binding.menuLiveLayout.setBackgroundColor(0)

        binding.menuBrowseImage.imageTintList = color
        binding.menuBrowseLayout.setBackgroundColor(0)

        binding.menuLibraryLayoutImage.imageTintList = color
        binding.menuLibraryLayout.setBackgroundColor(0)

        binding.menuSubscriptionsImage.imageTintList = color
        binding.menuSubscriptionsLayout.setBackgroundColor(0)

        binding.menuSettingsImage.imageTintList = color
        binding.menuSettingsLayout.setBackgroundColor(0)

        binding.menuLoginLayout.setBackgroundColor(0)
    }
}