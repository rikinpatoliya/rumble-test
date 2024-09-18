package com.rumble.analytics

import android.os.Bundle
import androidx.core.os.bundleOf

data class DiscoverCategoryClickEvent(
    val categoryName: String
) : AnalyticEvent {
    override val eventName: String = "Discover_CategoryCard_Tap"
    override val firebaseOps: Bundle = bundleOf(CATEGORY to categoryName)
    override val appsFlyOps: Map<String, String> = mapOf(CATEGORY to categoryName)
}

object DiscoverCategoryViewAllEvent : AnalyticEvent {
    override val eventName: String = "Discover_CategoriesViewAllButton_Tap"
    override val firebaseOps: Bundle = Bundle()
    override val appsFlyOps: Map<String, String> = emptyMap()
}

object BrowseCategoriesBackButtonEvent : AnalyticEvent {
    override val eventName: String = "CategoriesBrowse_BackButton_Tap"
    override val firebaseOps: Bundle = Bundle()
    override val appsFlyOps: Map<String, String> = emptyMap()
}

object BrowseCategoriesTabTapEvent : AnalyticEvent {
    override val eventName: String = "CategoriesBrowse_CategoriesTab_Tap"
    override val firebaseOps: Bundle = Bundle()
    override val appsFlyOps: Map<String, String> = emptyMap()
}

object BrowseCategoriesLiveStreamTabTapEvent : AnalyticEvent {
    override val eventName: String = "CategoriesBrowse_LiveStreamsTab_Tap"
    override val firebaseOps: Bundle = Bundle()
    override val appsFlyOps: Map<String, String> = emptyMap()
}

data class BrowseCategoriesCategoryButtonEvent(
    val categoryName: String
) : AnalyticEvent {
    override val eventName: String = "CategoriesBrowse_CategoryButton_Tap"
    override val firebaseOps: Bundle = bundleOf(CATEGORY to categoryName)
    override val appsFlyOps: Map<String, String> = mapOf(CATEGORY to categoryName)
}

data class BrowseCategoriesCategoryCardEvent(
    val categoryName: String,
    val index: Int
) : AnalyticEvent {
    override val eventName: String = "CategoriesBrowse_CategoryCard_Tap"
    override val firebaseOps: Bundle = bundleOf(CATEGORY to categoryName, INDEX to index)
    override val appsFlyOps: Map<String, String> =
        mapOf(CATEGORY to categoryName, INDEX to index.toString())
}

data class BrowseCategoriesVideoCardEvent(
    val destination: String,
    val index: Int
) : AnalyticEvent {
    override val eventName: String = "CategoriesBrowse_VideoCard_Tap"
    override val firebaseOps: Bundle = bundleOf(DESTINATION to destination, INDEX to index)
    override val appsFlyOps: Map<String, String> =
        mapOf(DESTINATION to destination, INDEX to index.toString())
}

object BrowseCategoriesSearchEvent : AnalyticEvent {
    override val eventName: String = "CategoriesBrowse_SearchButton_Tap"
    override val firebaseOps: Bundle = Bundle()
    override val appsFlyOps: Map<String, String> = emptyMap()
}

object CategoryBackButtonEvent : AnalyticEvent {
    override val eventName: String = "Category_BackButton_Tap"
    override val firebaseOps: Bundle = Bundle()
    override val appsFlyOps: Map<String, String> = emptyMap()
}

object CategoryCategoriesTabTapEvent : AnalyticEvent {
    override val eventName: String = "Category_CategoriesTab_Tap"
    override val firebaseOps: Bundle = Bundle()
    override val appsFlyOps: Map<String, String> = emptyMap()
}

object CategoryLiveStreamTabTapEvent : AnalyticEvent {
    override val eventName: String = "Category_LiveStreamsTab_Tap"
    override val firebaseOps: Bundle = Bundle()
    override val appsFlyOps: Map<String, String> = emptyMap()
}

object CategoryRecordedTabTapEvent : AnalyticEvent {
    override val eventName: String = "Category_RecordedStreamsTab_Tap"
    override val firebaseOps: Bundle = Bundle()
    override val appsFlyOps: Map<String, String> = emptyMap()
}

object CategoryVideosTabTapEvent : AnalyticEvent {
    override val eventName: String = "Category_VideosTab_Tap"
    override val firebaseOps: Bundle = Bundle()
    override val appsFlyOps: Map<String, String> = emptyMap()
}

data class CategoryCardEvent(
    val categoryName: String,
    val index: Int,
    val tabName: String
) : AnalyticEvent {
    override val eventName: String = "Category_CategoryCard_Tap"
    override val firebaseOps: Bundle =
        bundleOf(CATEGORY to categoryName, INDEX to index, TAB to tabName)
    override val appsFlyOps: Map<String, String> =
        mapOf(CATEGORY to categoryName, INDEX to index.toString(), TAB to tabName)
}

object CategorySearchEvent : AnalyticEvent {
    override val eventName: String = "Category_SearchButton_Tap"
    override val firebaseOps: Bundle = Bundle()
    override val appsFlyOps: Map<String, String> = emptyMap()
}

data class CategoryVideoCardEvent(
    val destination: String,
    val index: Int,
    val tabName: String
) : AnalyticEvent {
    override val eventName: String = "Category_VideoCard_Tap"
    override val firebaseOps: Bundle =
        bundleOf(DESTINATION to destination, INDEX to index, TAB to tabName)
    override val appsFlyOps: Map<String, String> =
        mapOf(DESTINATION to destination, INDEX to index.toString(), TAB to tabName)
}