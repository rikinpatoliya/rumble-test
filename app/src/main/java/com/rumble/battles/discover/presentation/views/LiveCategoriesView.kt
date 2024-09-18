package com.rumble.battles.discover.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import com.rumble.battles.R
import com.rumble.battles.commonViews.RumbleTextActionButton
import com.rumble.domain.discover.domain.domainmodel.CategoryEntity
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.RumbleTypography.tinyBody
import com.rumble.theme.defaultDiscoverContentLoadingHeight
import com.rumble.theme.liveCategoryHeight
import com.rumble.theme.liveCategoryWidth
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.radiusSmall
import com.rumble.utils.RumbleConstants.CATEGORY_THRESHOLDER
import com.rumble.utils.extension.shortString

@Composable
fun LiveCategoriesView(
    modifier: Modifier = Modifier,
    title: String,
    titlePadding: Dp,
    isLoading: Boolean = false,
    error: Boolean = false,
    viewAll: Boolean = false,
    categoryList: List<CategoryEntity>,
    onViewCategory: (CategoryEntity, Int) -> Unit,
    onRefresh: () -> Unit = {},
    onViewAll: () -> Unit = {},
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .padding(horizontal = titlePadding)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = h4
            )
            Spacer(modifier = Modifier.weight(1f))
            if (viewAll) {
                RumbleTextActionButton(
                    text = stringResource(id = R.string.view_all),
                ) {
                    onViewAll()
                }
            }
        }

        if (isLoading) {
            LoadingView(
                modifier = Modifier
                    .padding(horizontal = titlePadding)
                    .height(defaultDiscoverContentLoadingHeight)
                    .fillMaxWidth()
            )
        } else if (error) {
            ErrorView(
                modifier = Modifier
                    .padding(horizontal = titlePadding)
                    .height(defaultDiscoverContentLoadingHeight)
                    .fillMaxWidth(),
                onRetry = onRefresh
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(paddingSmall),
                contentPadding = PaddingValues(horizontal = titlePadding)
            ) {
                itemsIndexed(categoryList) { index, item ->
                    LiveCategoryView(
                        modifier = Modifier.clickable { onViewCategory(item, index) },
                        category = item
                    )
                }
            }
        }
    }
}

@Composable
private fun LiveCategoryView(
    modifier: Modifier = Modifier,
    category: CategoryEntity
) {

    ConstraintLayout(modifier = modifier) {
        val (thumbnail, title, viewers) = createRefs()

        Box(
            modifier = Modifier
                .constrainAs(thumbnail) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .clip(RoundedCornerShape(radiusSmall))
                .background(MaterialTheme.colors.primaryVariant)
                .width(liveCategoryWidth)
                .height(liveCategoryHeight)
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = category.thumbnail,
                contentDescription = category.title,
                contentScale = ContentScale.Crop
            )
        }

        Text(
            modifier = Modifier
                .constrainAs(title) {
                    top.linkTo(thumbnail.bottom)
                    start.linkTo(thumbnail.start)
                    end.linkTo(thumbnail.end)
                    width = Dimension.fillToConstraints
                }
                .padding(top = paddingXSmall),
            text = category.title,
            style = h6,
            color = MaterialTheme.colors.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (category.viewersNumber >= CATEGORY_THRESHOLDER) {
            Text(
                modifier = Modifier
                    .constrainAs(viewers) {
                        top.linkTo(title.bottom)
                        start.linkTo(thumbnail.start)
                    },
                text = "${category.viewersNumber.shortString(withDecimal = true)} ${
                    pluralStringResource(
                        id = R.plurals.viewers,
                        count = category.viewersNumber.toInt()
                    )
                }",
                style = tinyBody,
                color = MaterialTheme.colors.secondary
            )
        }
    }
}



