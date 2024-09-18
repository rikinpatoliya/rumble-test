package com.rumble.battles.discover.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.rumble.battles.R
import com.rumble.domain.discover.domain.domainmodel.CategoryEntity
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.paddingSmall
import com.rumble.theme.radiusSmall
import com.rumble.utils.RumbleConstants
import com.rumble.utils.extension.shortString

@Composable
fun SubcategoryView(
    modifier: Modifier = Modifier,
    subcategory: CategoryEntity
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(radiusSmall))
                .background(MaterialTheme.colors.primaryVariant)
                .aspectRatio(RumbleConstants.CATEGORY_CARD_ASPECT_RATIO)
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = subcategory.thumbnail,
                contentDescription = subcategory.title,
                contentScale = ContentScale.Crop
            )
        }

        Text(
            modifier = Modifier.padding(top = paddingSmall),
            text = subcategory.title,
            style = h4,
            color = MaterialTheme.colors.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (subcategory.viewersNumber >= RumbleConstants.CATEGORY_THRESHOLDER) {
            Text(
                text = "${subcategory.viewersNumber.shortString(withDecimal = true)} ${
                    pluralStringResource(
                        id = R.plurals.viewers,
                        count = subcategory.viewersNumber.toInt()
                    ).replaceFirstChar { it.uppercaseChar() }
                }",
                style = RumbleTypography.h6,
                color = MaterialTheme.colors.secondary
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    val category = CategoryEntity(
        title = "Test Subcategory",
        thumbnail = "",
        viewersNumber = 100,
        description = "Test description",
        path = ""
    )

    RumbleTheme {
        SubcategoryView(
            subcategory = category
        )
    }
}