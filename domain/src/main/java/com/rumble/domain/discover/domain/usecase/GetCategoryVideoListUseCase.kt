package com.rumble.domain.discover.domain.usecase

import androidx.paging.PagingData
import androidx.paging.insertSeparators
import com.rumble.domain.common.domain.usecase.GetVideoPageSizeUseCase
import com.rumble.domain.discover.domain.domainmodel.CategoryDisplayType
import com.rumble.domain.discover.domain.domainmodel.CategoryEntity
import com.rumble.domain.discover.domain.domainmodel.CategoryListEntity
import com.rumble.domain.discover.model.repository.DiscoverRepository
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.network.queryHelpers.CategoryVideoType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCategoryVideoListUseCase @Inject constructor(
    private val discoverRepository: DiscoverRepository,
    private val getVideoPageSizeUseCase: GetVideoPageSizeUseCase,
) {
    operator fun invoke(
        category: CategoryEntity,
        displayType: CategoryDisplayType,
        showLiveCategoryList: Boolean,
        subcategoryList: List<CategoryEntity>
    ): Flow<PagingData<Feed>> {
        val pageSize = getVideoPageSizeUseCase()

        return when (displayType) {
            CategoryDisplayType.LIVE_STREAM -> discoverRepository.fetchCategoryVideoList(
                categoryName = category.path.lowercase(),
                videoType = CategoryVideoType.LIVE,
                pageSize = pageSize
            ).map { pagingData ->
                handleLiveCategoryList(
                    showLiveCategoryList,
                    pagingData,
                    subcategoryList
                )
            }

            CategoryDisplayType.RECORDED_STREAM -> discoverRepository.fetchCategoryVideoList(
                categoryName = category.path.lowercase(),
                videoType = CategoryVideoType.STREAMED,
                pageSize = pageSize
            ).map { pagingData ->
                handleLiveCategoryList(
                    showLiveCategoryList,
                    pagingData,
                    subcategoryList
                )
            }

            else -> discoverRepository.fetchCategoryVideoList(
                categoryName = category.path.lowercase(),
                videoType = CategoryVideoType.REGULAR,
                pageSize = pageSize
            ).map { pagingData ->
                handleLiveCategoryList(
                    showLiveCategoryList,
                    pagingData,
                    subcategoryList
                )
            }
        }
    }

    private fun handleLiveCategoryList(
        showLiveCategoryList: Boolean,
        pagingData: PagingData<Feed>,
        subcategoryList: List<CategoryEntity>
    ): PagingData<Feed> =
        if (showLiveCategoryList) insertCategoryList(pagingData, subcategoryList)
        else pagingData

    private fun insertCategoryList(
        pagingData: PagingData<Feed>,
        subcategoryList: List<CategoryEntity>
    ): PagingData<Feed> =
        pagingData.insertSeparators { before, _ ->
            if (before?.index == 0 && subcategoryList.isNotEmpty()) {
                CategoryListEntity(
                    categoryList = subcategoryList,
                    index = 0
                )
            } else {
                null
            }
        }
}