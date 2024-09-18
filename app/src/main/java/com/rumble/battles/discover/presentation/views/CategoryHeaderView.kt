package com.rumble.battles.discover.presentation.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import coil.compose.AsyncImage
import com.rumble.battles.R
import com.rumble.domain.discover.domain.domainmodel.CategoryEntity
import com.rumble.theme.RumbleTypography
import com.rumble.theme.categoryHeight
import com.rumble.theme.categoryWidth
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusMedium
import com.rumble.utils.RumbleConstants
import com.rumble.utils.extension.shortString

@Composable
fun CategoryHeaderView(
    modifier: Modifier,
    category: CategoryEntity?,
    isLoading: Boolean
) {
    if (isLoading) {
        Image(
            modifier = modifier.padding(horizontal = paddingMedium),
            painter = painterResource(id = R.drawable.header_ghost),
            contentDescription = "",
            contentScale = ContentScale.FillWidth,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.secondaryVariant)
        )
    } else {
        category?.let {
            Column(modifier = modifier) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = paddingMedium,
                            end = paddingMedium,
                            bottom = paddingMedium
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .padding(end = paddingSmall)
                            .clip(RoundedCornerShape(radiusMedium))
                            .background(MaterialTheme.colors.primaryVariant)
                            .width(categoryWidth)
                            .height(categoryHeight)
                    ) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = category.thumbnail,
                            contentDescription = category.title,
                            contentScale = ContentScale.Crop
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(paddingXXXSmall)) {
                        Text(
                            text = category.title,
                            style = RumbleTypography.h1,
                            color = MaterialTheme.colors.primary
                        )

                        category.description?.let {
                            Text(
                                text = it,
                                style = RumbleTypography.h6Light,
                                color = MaterialTheme.colors.secondary
                            )
                        }

                        if (category.viewersNumber >= RumbleConstants.CATEGORY_THRESHOLDER) {
                            Text(
                                text = "${category.viewersNumber.shortString(withDecimal = true)} ${
                                    pluralStringResource(
                                        id = R.plurals.viewers,
                                        count = category.viewersNumber.toInt()
                                    ).replaceFirstChar { it.uppercaseChar() }
                                }",
                                style = RumbleTypography.h6,
                                color = MaterialTheme.colors.primaryVariant
                            )
                        }
                    }
                }

                Divider(color = MaterialTheme.colors.secondaryVariant)
            }
        }
    }
}