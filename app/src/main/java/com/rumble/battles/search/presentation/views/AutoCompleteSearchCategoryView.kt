package com.rumble.battles.search.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.rumble.battles.R
import com.rumble.battles.common.buildDelimiterHighlightedAnnotatedString
import com.rumble.domain.discover.domain.domainmodel.CategoryEntity
import com.rumble.theme.RumbleTypography.h5
import com.rumble.theme.enforcedCloud
import com.rumble.theme.imageMedium
import com.rumble.theme.imageWidthLarge
import com.rumble.theme.imageXSmall
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.radiusXXSmall

@Composable
fun AutoCompleteSearchCategoryView(
    modifier: Modifier = Modifier,
    categoryEntity: CategoryEntity,
    query: String,
    onBrowseCategory: (String) -> Unit,
) {
    val annotatedText = buildDelimiterHighlightedAnnotatedString(
        input = categoryEntity.title,
        delimiter = query,
        regularStyle = SpanStyle(
            fontFamily = h5.fontFamily,
            fontWeight = h5.fontWeight,
            fontSize = h5.fontSize,
        ),
        highlightedStyle = SpanStyle(
            fontFamily = h5.fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = h5.fontSize,
        ),
    )
    Box(modifier = modifier
        .clickable {
            onBrowseCategory(categoryEntity.path)
        }
    ) {
        Row(
            modifier = modifier
                .padding(start = paddingMedium, end = paddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(
                        top = paddingXXSmall,
                        bottom = paddingXXSmall,
                        end = paddingSmall,
                    )
                    .clip(RoundedCornerShape(radiusXXSmall))
                    .background(MaterialTheme.colors.primaryVariant)
                    .width(imageMedium)
                    .height(imageWidthLarge)
            ) {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = categoryEntity.thumbnail,
                    contentDescription = categoryEntity.title,
                    contentScale = ContentScale.Crop
                )
            }
            Text(
                modifier = Modifier
                    .weight(1f),
                text = annotatedText,
                color = MaterialTheme.colors.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.weight(1F))
            Icon(
                painter = painterResource(id = R.drawable.ic_search_log),
                contentDescription = "",
                modifier = Modifier.size(imageXSmall),
                tint = enforcedCloud
            )
        }
    }
}