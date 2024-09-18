package com.rumble

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.Display
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.hardware.display.DisplayManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.rumble.databinding.V4ActivityMainBinding
import com.rumble.domain.timerange.model.TimeRangeService
import com.rumble.network.connection.InternetConnectionState
import com.rumble.ui3.main.InternetConnectionLostDialogFragment
import com.rumble.ui3.main.MainViewModel
import com.rumble.ui3.search.SearchFragment
import com.rumble.ui3.search.v4.SearchFragmentV4
import com.rumble.ui3.subscriptions.v4.SubscriptionState
import com.rumble.ui3.subscriptions.v4.SubscriptionsMainFragmentV4
import com.rumble.util.Constant
import com.rumble.util.LeftMenuView
import com.rumble.util.dpToPx
import com.rumble.util.gone
import com.rumble.util.visible
import com.rumble.utils.RumbleConstants.TV_MAIN_MENU_FOCUS_DELAY_TIME
import com.rumble.utils.RumbleUIUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import kotlin.system.exitProcess

@AndroidEntryPoint
abstract class MainActivityNew : FragmentActivity(), LeftMenuView.MenuItemClickListener  {

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var rumbleUIUtil: RumbleUIUtil

    private var leftMenusShown: Boolean = false

    private var _binding: V4ActivityMainBinding? = null
    private val binding get() = _binding!!
    private var manuallyOpenFragment: Boolean = false
    lateinit var navController: NavController
    private var navOptions: NavOptions? = null

    private lateinit var dialogInternet: InternetConnectionLostDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initGeneralErrorHandler()
        _binding = DataBindingUtil.setContentView(this, R.layout.v4_activity_main)

        updateNavHostWidth(FragmentWidth.SPECIFIC_WIDTH)
        binding.rlLeftMenu.setupDefaultMenu(LeftMenuView.HOME_MENU)
        showLeftMenu(binding.rlLeftMenu)

        navOptions = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setPopUpTo(R.id.homeFragmentv4, false)
            .build()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navController.navigate(R.id.homeFragmentv4, null, navOptions)

        setupFocusListener()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userName.collect {
                    binding.rlLeftMenu.bindUserName(it, rumbleUIUtil)
                }
            }

        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userAvatarUrl.collect {
                    binding.rlLeftMenu.bindUserAvatar(it)
                }
            }
        }

        viewModel.uiStateNew.observe(this) { screen ->
            openScreen(screen)
        }

        dialogInternet = supportFragmentManager
            .findFragmentByTag(InternetConnectionLostDialogFragment::class.java.simpleName)
                as? InternetConnectionLostDialogFragment ?: InternetConnectionLostDialogFragment()

        viewModel.connectionState.observe(this) {
            when {
                it == InternetConnectionState.LOST && dialogInternet.isVisible.not() -> {
                    dialogInternet.show(
                        supportFragmentManager,
                        InternetConnectionLostDialogFragment::class.java.simpleName
                    )
                }
                it == InternetConnectionState.CONNECTED && dialogInternet.isAdded -> {
                    dialogInternet.dismiss()
                }
            }
        }

        initializeTimeRangeService(savedInstanceState)
    }

    private fun initializeTimeRangeService(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            startService(Intent(this@MainActivityNew, TimeRangeService::class.java))
        }
    }

    private fun initGeneralErrorHandler() {
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            if (e is IOException) viewModel.onError(e)
            else exitProcess(1)
        }
    }

    override fun menuItemFocus(menuId: Int) {
        if (binding.rlLeftMenu.getCurrentSelected() == menuId) {
            return
        }
        showLogo(true)
        updateNavHostWidth(FragmentWidth.SPECIFIC_WIDTH)
        when (menuId) {
            LeftMenuView.SEARCH_MENU -> {
                binding.rlLeftMenu.setCurrentSelected(LeftMenuView.SEARCH_MENU)
                navController.navigate(R.id.searchFragment, null, navOptions)
                Handler(Looper.getMainLooper()).postDelayed({
                    hideLeftMenu(binding.rlLeftMenu)
                }, 300)
            }

            LeftMenuView.LIVE_MENU -> {
                binding.rlLeftMenu.setCurrentSelected(LeftMenuView.LIVE_MENU)
                navController.navigate(R.id.liveFragmentv4, null, navOptions)
            }

            LeftMenuView.BROWSE_MENU -> {
                binding.rlLeftMenu.setCurrentSelected(LeftMenuView.BROWSE_MENU)
                navController.navigate(R.id.browseFragmentv4, null, navOptions)
            }

            LeftMenuView.LIBRARY_MENU -> {
                updateNavHostWidth(FragmentWidth.FULL_WIDTH)
                binding.rlLeftMenu.setCurrentSelected(LeftMenuView.LIBRARY_MENU)
                navController.navigate(R.id.libraryFragment, null, navOptions)
            }

            LeftMenuView.SUBSCRIPTIONS_MENU -> {
                updateNavHostWidth(FragmentWidth.FULL_WIDTH)
                binding.rlLeftMenu.setCurrentSelected(LeftMenuView.SUBSCRIPTIONS_MENU)
                navController.navigate(R.id.subscriptionsFragmentV4, null, navOptions)
            }

            LeftMenuView.SETTING_MENU -> {
                binding.rlLeftMenu.setCurrentSelected(LeftMenuView.SETTING_MENU)
                navController.navigate(R.id.settingsFragmentV4, null, navOptions)
            }

            LeftMenuView.LOGIN_MENU -> {
                updateNavHostWidth(FragmentWidth.FULL_WIDTH)
                binding.rlLeftMenu.setCurrentSelected(LeftMenuView.LOGIN_MENU)
                navController.navigate(R.id.loginFragment, null, navOptions)
            }
            else -> {
                binding.rlLeftMenu.setCurrentSelected(LeftMenuView.HOME_MENU)
                navController.navigate(R.id.homeFragmentv4, null, navOptions)
            }
        }
    }

    override fun menuItemClick(menuId: Int) {
        if (binding.rlLeftMenu.getCurrentSelected() == menuId) {
            if (binding.rlLeftMenu.getCurrentSelected() == LeftMenuView.SEARCH_MENU){
                val fragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                fragment?.childFragmentManager?.fragments?.forEach {
                    if (it is SearchFragmentV4) {
                        it.requestFocusToLastPosition()
                    } else if (it is SearchFragment) {
                        it.setComposeViewFocusable()
                    }
                }
            }
            resetLeftMenuUI()
            return
        }
        showLogo(true)
        when (menuId) {
            LeftMenuView.SEARCH_MENU -> {
                binding.rlLeftMenu.setCurrentSelected(LeftMenuView.SEARCH_MENU)
                navController.navigate(R.id.searchFragment, null, navOptions)
            }

            LeftMenuView.LIVE_MENU -> {
                binding.rlLeftMenu.setCurrentSelected(LeftMenuView.LIVE_MENU)
                navController.navigate(R.id.liveFragmentv4, null, navOptions)
            }

            LeftMenuView.BROWSE_MENU -> {
                binding.rlLeftMenu.setCurrentSelected(LeftMenuView.BROWSE_MENU)
                navController.navigate(R.id.browseFragmentv4, null, navOptions)
            }
            
            LeftMenuView.SUBSCRIPTIONS_MENU -> {
                binding.rlLeftMenu.setCurrentSelected(LeftMenuView.SUBSCRIPTIONS_MENU)
                navController.navigate(R.id.subscriptionsFragmentV4, null, navOptions)
            }

            LeftMenuView.SETTING_MENU -> {
                binding.rlLeftMenu.setCurrentSelected(LeftMenuView.SETTING_MENU)
                navController.navigate(R.id.settingsFragmentV4, null, navOptions)
            }

            LeftMenuView.LOGIN_MENU -> {
                binding.rlLeftMenu.setCurrentSelected(LeftMenuView.LOGIN_MENU)
                navController.navigate(R.id.loginFragment, null, navOptions)
            }

            LeftMenuView.LIBRARY_MENU -> {
                binding.rlLeftMenu.setCurrentSelected(LeftMenuView.LIBRARY_MENU)
                navController.navigate(R.id.libraryFragment, null, navOptions)
            }

            else -> {
                binding.rlLeftMenu.setCurrentSelected(LeftMenuView.HOME_MENU)
                navController.navigate(R.id.homeFragmentv4, null, navOptions)
            }
        }

        hideLeftMenu(binding.rlLeftMenu)
        Handler(Looper.getMainLooper()).postDelayed({
            setupFocusListener()
        }, TV_MAIN_MENU_FOCUS_DELAY_TIME)
    }

    private fun setupFocusListener() {
        binding.rlLeftMenu.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                if (!manuallyOpenFragment){
                    binding.rlLeftMenu.onFocusChangeListener = null
                    showLeftMenu(view)
                }
            } else {
                hideLeftMenu(view)
            }
        }
    }

    private fun showLeftMenu(view: View) {
        val width = binding.rlLeftMenu.measuredWidth
        val valueAnimator = ValueAnimator.ofInt(width, dpToPx(resources.getInteger(R.integer.menu_headers_opened_width)))
        binding.rlLeftMenu.setupMenuExpandedUI()
        leftMenusShown = true
        LeftMenuView.isAnimating = true // set flag to true before starting animation
        LeftMenuView.animateView(view, valueAnimator)
    }

    fun showLeftMenu(){
        showLeftMenu(binding.rlLeftMenu)
        Handler(Looper.getMainLooper()).postDelayed({
            binding.rlLeftMenu.highlightCurrentSelectedMenu(false)
        }, Constant.OPEN_SIDEBAR_HIGHLIGHT_TINE_DELAY)
    }

    private fun hideLeftMenu(view: View) {
        val width = binding.rlLeftMenu.measuredWidth
        val valueAnimator = ValueAnimator.ofInt(width, dpToPx(resources.getInteger(R.integer.menu_headers_closed_width)))
        binding.rlLeftMenu.setupMenuClosedUI()
        leftMenusShown = false
        LeftMenuView.isAnimating = true // set flag to true before starting animation
        LeftMenuView.animateView(view, valueAnimator)
        binding.navHostFragment.requestFocus()
    }

    private fun openScreen(menuId: Int) {
        manuallyOpenFragment = true
        menuItemFocus(menuId)
        binding.rlLeftMenu.changeFocusTo(menuId)
        Handler(Looper.getMainLooper()).postDelayed({
            manuallyOpenFragment = false
            binding.navHostFragment.requestFocus()
        }, Constant.REQUEST_FOCUS_DELAY)
    }

    private fun refreshSubscriptionScreen(menuId: Int){
        manuallyOpenFragment = true
        menuItemFocus(menuId)
        binding.rlLeftMenu.changeFocusTo(menuId)

        binding.rlLeftMenu.setCurrentSelected(LeftMenuView.SUBSCRIPTIONS_MENU)
        navController.navigate(R.id.subscriptionsFragmentV4, null, navOptions)

        Handler(Looper.getMainLooper()).postDelayed({
            manuallyOpenFragment = false
            binding.navHostFragment.requestFocus()
        }, Constant.REQUEST_FOCUS_DELAY)
    }

    override fun onResume() {
        super.onResume()
        if(binding.rlLeftMenu.getCurrentSelected() == LeftMenuView.SUBSCRIPTIONS_MENU && leftMenusShown){
            binding.rlLeftMenu.focusCurrentSelectedMenu()
        }
    }

    private fun updateNavHostWidth(fragmentWidth: FragmentWidth) {

        val navHostWidth = if (fragmentWidth == FragmentWidth.FULL_WIDTH){ 0 } else { calculateNavHostWidth() }

        val navHost = findViewById<View>(R.id.nav_host_fragment)
        val params = navHost.layoutParams
        params.width = navHostWidth
        navHost.layoutParams = params

        // Set constraint to end of parent
        val constraintLayout = findViewById<ConstraintLayout>(R.id.mainConstraintLayout)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        if (fragmentWidth == FragmentWidth.SPECIFIC_WIDTH){
            constraintSet.clear(navHost.id, ConstraintSet.END)
        } else {
            constraintSet.connect(navHost.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        }
        constraintSet.connect(navHost.id, ConstraintSet.START, R.id.rlLeftMenu, ConstraintSet.END)
        constraintSet.applyTo(constraintLayout)
    }

    private fun calculateNavHostWidth(): Int {
        val closedMenuWidth = resources.getDimensionPixelSize(R.dimen.menu_headers_closed_width)
        return getDisplayMetrics().widthPixels - closedMenuWidth
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (leftMenusShown){
                finishAffinity()
                exitProcess(0)
            } else {
                if (binding.rlLeftMenu.getCurrentSelected() == LeftMenuView.SUBSCRIPTIONS_MENU && SubscriptionState.showingHeaders.not()) {
                    val fragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                    fragment?.childFragmentManager?.fragments?.forEach {
                        if (it is SubscriptionsMainFragmentV4) {
                            it.onBackInSubscriptions()
                        }
                    }
                } else if (binding.rlLeftMenu.getCurrentSelected() == LeftMenuView.SEARCH_MENU) {
                    showLeftMenu(binding.rlLeftMenu)
                    Handler(Looper.getMainLooper()).postDelayed({ // Because of the search bar's water drop view and the user's press-back, focus was lost.
                        binding.rlLeftMenu.focusCurrentSelectedMenu()
                    }, Constant.SEARCH_ON_BACK_PRESS_SIDE_BAR_FOCUS_DURATION)
                } else {
                    showLeftMenu(binding.rlLeftMenu)
                }
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && leftMenusShown) {
            if (LeftMenuView.isAnimating.not()) {
                if (binding.rlLeftMenu.getCurrentSelected() == LeftMenuView.SEARCH_MENU){
                    val fragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                    fragment?.childFragmentManager?.fragments?.forEach {
                        if (it is SearchFragmentV4) {
                            it.requestFocusToLastPosition()
                        } else if (it is SearchFragment) {
                            it.setComposeViewFocusable()
                        }
                    }
                }
                return resetLeftMenuUI()
            }

        }
        return false
    }

    fun resetLeftMenuUI(): Boolean {
        binding.navHostFragment.requestFocus()
        hideLeftMenu(binding.rlLeftMenu)
        setupFocusListener()
        return true
    }

    fun showLogo(show: Boolean){
        if (show){
            binding.logo.visible()
        } else {
            binding.logo.gone()
        }
    }

    override fun onDestroy() {
        binding.rlLeftMenu.onFocusChangeListener = null
        super.onDestroy()
    }
    enum class FragmentWidth {
        FULL_WIDTH, SPECIFIC_WIDTH
    }

    private fun getDisplayMetrics(): DisplayMetrics {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val defaultDisplay = DisplayManagerCompat.getInstance(this).getDisplay(Display.DEFAULT_DISPLAY)
            val displayContext = createDisplayContext(defaultDisplay!!)
            displayContext.resources.displayMetrics
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics
        }
    }
}