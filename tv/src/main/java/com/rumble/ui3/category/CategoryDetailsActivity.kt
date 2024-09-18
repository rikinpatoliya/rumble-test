package com.rumble.ui3.category

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.rumble.R
import com.rumble.databinding.ActivityCategoryDetailsBinding
import com.rumble.domain.discover.domain.domainmodel.CategoryDisplayType
import com.rumble.domain.discover.domain.domainmodel.CategoryEntity
import com.rumble.network.connection.InternetConnectionState
import com.rumble.ui3.main.InternetConnectionLostDialogFragment
import com.rumble.util.Constant
import com.rumble.util.isNetworkConnected
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryDetailsActivity : FragmentActivity(), View.OnClickListener, View.OnKeyListener, View.OnFocusChangeListener  {

    companion object {
        const val BUNDLE_KEY_CATEGORY_PATH = "path"
    }

    private var _binding: ActivityCategoryDetailsBinding? = null

    /** This property is only valid between onCreateView and onDestroyView. */
    private val binding get() = _binding!!
    private lateinit var categoryDetailsFragment: CategoryDetailsFragment
    private lateinit var categoryPath: String
    private lateinit var categoryDetails: CategoryEntity
    private var selectedFilterView: View? = null
    private val viewModel: CategoryDetailsViewModel by viewModels()
    private var relatedCategoryArrayList: List<CategoryEntity>? = null
    private var lastSelectedView: View? = null // This variable will be utilised on the right back button push.
    private var currentFocusedCategory: View? = null

    private lateinit var dialogInternet: InternetConnectionLostDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_category_details)
        binding.actionClickHandler = this
        initObservers()
        intent.extras?.let {
            categoryPath = CategoryDetailsActivityArgs.fromBundle(it).path

            if (isNetworkConnected) {
                progressBar(true)
                viewModel.fetchCategoryData(categoryPath)
            } else {
                progressBar(false)
            }
        }

        binding.radioButtonLivestream.performClick()

        binding.radioButtonLivestream.setOnKeyListener(this)
        binding.radioButtonRecordedStream.setOnKeyListener(this)
        binding.radioButtonVideos.setOnKeyListener(this)
        binding.radioButtonCategories.setOnKeyListener(this)

        binding.backButton.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event?.action == KeyEvent.ACTION_DOWN) {
                Handler(Looper.getMainLooper()).post {
                    lastSelectedView?.requestFocus()
                }
            }
            return@setOnKeyListener false
        }

        binding.radioButtonLivestream.onFocusChangeListener = this
        binding.radioButtonRecordedStream.onFocusChangeListener = this
        binding.radioButtonVideos.onFocusChangeListener = this
        binding.radioButtonCategories.onFocusChangeListener = this

        lifecycleScope.launch {
            viewModel.vmEvents.collect { event ->
                when (event) {
                    CategoryDetailsVmEvent.RefreshData -> {
                        viewModel.fetchCategoryData(categoryPath = categoryPath)
                    }
                }
            }
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

    }

    private fun progressBar(show: Boolean){
        binding.progressBar.isVisible = show
    }

    private fun initObservers(){
        viewModel.relatedCategoriesLiveData.observe(this){ categoryResult ->
            relatedCategoryArrayList = categoryResult.second
            categoryDetails = categoryResult.first
            binding.item = categoryDetails
            binding.radioGroup.isVisible = true
            binding.radioButtonLivestream.requestFocus()
            binding.backButton.isFocusable = true
            if (relatedCategoryArrayList.isNullOrEmpty().not()){
                binding.radioButtonCategories.isVisible = true
                binding.radioButtonVideos.nextFocusRightId = binding.radioButtonCategories.id
            } else {
                binding.radioButtonCategories.isVisible = false
                binding.radioButtonVideos.nextFocusRightId = binding.radioButtonVideos.id
            }
            setInitialData()
            progressBar(false)
        }

        viewModel.errorRelatedCategoriesLiveData.observe(this){
            relatedCategoryArrayList = emptyList()
            progressBar(false)
        }
    }

    private fun setInitialData(){
        setCategoryDetailsFragment(categoryDetails, CategoryDisplayType.LIVE_STREAM, emptyList())
        selectedFilterView = binding.radioButtonLivestream
        lastSelectedView = binding.radioButtonLivestream
    }

    private fun requestFocusForLastSelectedView(){
        selectedFilterView?.requestFocus()
    }

    override fun onClick(v: View?) {
        selectedFilterView = v

        when (v?.id) {
            R.id.radio_button_livestream -> {
                setCategoryDetailsFragment(categoryDetails, CategoryDisplayType.LIVE_STREAM, emptyList())
            }
            R.id.radio_button_recorded_stream -> {
                setCategoryDetailsFragment(categoryDetails, CategoryDisplayType.RECORDED_STREAM, emptyList())
            }
            R.id.radio_button_videos -> {
                setCategoryDetailsFragment(categoryDetails, CategoryDisplayType.VIDEOS, emptyList())
            }
            R.id.radio_button_categories -> {
                setCategoryDetailsFragment(categoryDetails, CategoryDisplayType.CATEGORIES, relatedCategoryArrayList)
            }
            R.id.back_button -> {
                finish()
            }
        }
    }

    private fun titleLayoutVisibility(show: Boolean) {
        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.duration = Constant.CATEGORY_TITLE_ANIMATION_DURATION
        transition.addTarget(binding.contentView)
        TransitionManager.beginDelayedTransition(binding.contentView, transition)
        binding.titleLayout.visibility = if (show) View.VISIBLE else View.GONE

        if (show.not()){
            categoryDetailsFragment.viewPadding(resources.getDimensionPixelSize(R.dimen.category_collapse_top_layout_padding))
        } else {
            categoryDetailsFragment.viewPadding(0)
            requestFocusForLastSelectedView()
        }
    }

    fun requestFocusOnBackButton(){
        binding.backButton.requestFocus()
    }

    private fun setCategoryDetailsFragment(category: CategoryEntity?, displayType: CategoryDisplayType, relatedCategoryArrayList: List<CategoryEntity>?){
        categoryDetailsFragment = CategoryDetailsFragment(
            category,
            displayType,
            relatedCategoryArrayList,
            gridUpClick = {
                titleLayoutVisibility(true)
                lastSelectedView = binding.radioButtonLivestream
            }
        )
        supportFragmentManager.beginTransaction()
            .replace(R.id.categoryDetailsContainer, categoryDetailsFragment)
            .disallowAddToBackStack()
            .commit()
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && event?.action == KeyEvent.ACTION_DOWN) {
            if (categoryDetailsFragment.isSelectedCategoriesHasData()){
                lastSelectedView = binding.categoryDetailsContainer
                titleLayoutVisibility(false)
            } else {
                Handler(Looper.getMainLooper()).post {
                    currentFocusedCategory?.requestFocus()
                }
            }
        }
        return false
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus){
            currentFocusedCategory = v
        }
    }
}