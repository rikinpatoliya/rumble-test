package com.rumble.ui3.browse

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.rumble.R
import com.rumble.databinding.V4FragmentBrowseBinding
import com.rumble.domain.discover.domain.domainmodel.CategoryDisplayType
import com.rumble.domain.discover.domain.domainmodel.MainCategory
import com.rumble.leanback.BrowseSupportFragment
import com.rumble.ui3.category.CategoryDetailsActivityDirections
import com.rumble.util.Constant


class BrowseMainFragment : Fragment(), BrowseSupportFragment.MainFragmentAdapterProvider,
    View.OnClickListener, View.OnKeyListener, View.OnFocusChangeListener {

    /***/
    private val fragmentAdapter: BrowseSupportFragment.MainFragmentAdapter<BrowseMainFragment> =
        object : BrowseSupportFragment.MainFragmentAdapter<BrowseMainFragment>(this) {}

    /***/
    private var _binding: V4FragmentBrowseBinding? = null

    /** This property is only valid between onCreateView and onDestroyView. */
    private val binding get() = _binding!!

    /***/
    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<BrowseMainFragment> =
        fragmentAdapter

    /***/
    private var selectedFilterView: View? = null
    /***/
    private var currentFocusedFilterView: View? = null

    /***/
    private lateinit var browseDataGridFragment: BrowseDataGridFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = V4FragmentBrowseBinding.inflate(inflater)
        binding.actionClickHandler = this
        initViews()
        return binding.root
    }

    private fun initViews() {
        binding.btnGaming.text = getString(MainCategory.GAMING.label)
        binding.btnMusic.text = getString(MainCategory.MUSIC.label)
        binding.btnNews.text = getString(MainCategory.NEWS.label)
        binding.btnViral.text = getString(MainCategory.VIRAL.label)

        binding.imgGaming.setImageResource(MainCategory.GAMING.image)
        binding.imgMusic.setImageResource(MainCategory.MUSIC.image)
        binding.imgNews.setImageResource(MainCategory.NEWS.image)
        binding.imgViral.setImageResource(MainCategory.VIRAL.image)

        binding.radioButtonLivestream.setOnKeyListener(this)
        binding.radioButtonCategories.setOnKeyListener(this)
        binding.btnGaming.setOnKeyListener(this)
        binding.btnMusic.setOnKeyListener(this)
        binding.btnNews.setOnKeyListener(this)
        binding.btnViral.setOnKeyListener(this)

        binding.radioButtonLivestream.onFocusChangeListener = this
        binding.radioButtonCategories.onFocusChangeListener = this

        setBrowseGridFragment(CategoryDisplayType.CATEGORIES)

        selectedFilterView = binding.radioButtonCategories
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.radio_button_categories -> {
                selectedFilterView = v
                setBrowseGridFragment(CategoryDisplayType.CATEGORIES)
            }
            R.id.radio_button_livestream -> {
                selectedFilterView = v
                setBrowseGridFragment(CategoryDisplayType.LIVE_STREAM)
            }
            R.id.btn_gaming -> {
                Navigation.findNavController(requireView())
                    .navigate(CategoryDetailsActivityDirections.actionGlobalCategoryDetailsActivity(MainCategory.GAMING.path))
            }
            R.id.btn_viral -> {
                Navigation.findNavController(requireView())
                    .navigate(CategoryDetailsActivityDirections.actionGlobalCategoryDetailsActivity(MainCategory.VIRAL.path))
            }
            R.id.btn_music -> {
                Navigation.findNavController(requireView())
                    .navigate(CategoryDetailsActivityDirections.actionGlobalCategoryDetailsActivity(MainCategory.MUSIC.path))
            }
            R.id.btn_news -> {
                Navigation.findNavController(requireView())
                    .navigate(
                        CategoryDetailsActivityDirections.actionGlobalCategoryDetailsActivity(MainCategory.NEWS.path)
                    )
            }
        }
    }

    private fun setBrowseGridFragment(displayType: CategoryDisplayType) {
        browseDataGridFragment = BrowseDataGridFragment(displayType) {
            titleViewsIsFocusable(true)
            titleLayoutVisibility(true)
            browseCategoryFocusable(false)
        }
        childFragmentManager.beginTransaction()
            .replace(R.id.browseDataContainer, browseDataGridFragment)
            .disallowAddToBackStack()
            .commit()
    }

    private fun titleLayoutVisibility(show: Boolean) {
        val transition: Transition = Slide(Gravity.TOP)
        transition.duration = Constant.CATEGORY_TITLE_ANIMATION_DURATION
        transition.addTarget(binding.contentView)

        TransitionManager.beginDelayedTransition(binding.contentView, transition)
        binding.titleLayout.visibility = if (show) View.VISIBLE else View.GONE

        if (show.not()) {
            browseDataGridFragment.viewPadding(resources.getDimensionPixelSize(R.dimen.browse_grid_item_top_layout_padding_collapse))
        } else {
            browseDataGridFragment.viewPadding(resources.getDimensionPixelSize(R.dimen.browse_grid_item_top_layout_padding_expand))
            requestFocusForLastSelectedView()
        }
    }

    private fun requestFocusForLastSelectedView() {
        selectedFilterView?.requestFocus()
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && event?.action == KeyEvent.ACTION_DOWN) {

            when (v?.id) {
                R.id.radio_button_categories, R.id.radio_button_livestream -> {
                    if (this::browseDataGridFragment.isInitialized && browseDataGridFragment.getCurrentAdapterSize() > 0){
                        titleLayoutVisibility(false)

                        Handler(Looper.getMainLooper()).postDelayed({
                            titleViewsIsFocusable(false)
                        }, Constant.LIVE_CHANNEL_LOADING_DELAY)
                    } else{
                        Handler(Looper.getMainLooper()).post {
                            currentFocusedFilterView?.requestFocus()
                        }
                    }
                }
                R.id.btn_gaming, R.id.btn_viral, R.id.btn_music, R.id.btn_news -> {
                    Handler(Looper.getMainLooper()).post {
                        browseCategoryFocusable(false)
                        requestFocusForLastSelectedView()
                    }
                }
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event?.action == KeyEvent.ACTION_DOWN) {
            when (v?.id) {
                R.id.radio_button_categories, R.id.radio_button_livestream -> {
                    browseCategoryFocusable(true)
                }
            }
        }
        return false
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus){
            currentFocusedFilterView = v
        }
        if (hasFocus && binding.titleLayout.isVisible.not()) {
            titleLayoutVisibility(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun titleViewsIsFocusable(isFocusable: Boolean) {
        browseCategoryFocusable(isFocusable)
        binding.radioButtonLivestream.isFocusable = isFocusable
        binding.radioButtonCategories.isFocusable = isFocusable
    }

    private fun browseCategoryFocusable(isFocusable: Boolean) {
        binding.btnGaming.isFocusable = isFocusable
        binding.btnViral.isFocusable = isFocusable
        binding.btnMusic.isFocusable = isFocusable
        binding.btnNews.isFocusable = isFocusable
    }
}