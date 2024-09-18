package com.rumble.battles.discover.presentation.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.domain.discover.domain.domainmodel.MainCategory
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.borderXXSmall
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusMedium

@Composable
fun MainCategoryView(
    modifier: Modifier = Modifier,
    mainCategory: MainCategory,
    onClick: (MainCategory) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(radiusMedium))
            .border(
                width = borderXXSmall,
                color = mainCategory.borderColor,
                shape = RoundedCornerShape(radiusMedium)
            )
            .background(
                color = MaterialTheme.colors.background,
                shape = RoundedCornerShape(radiusMedium)
            )
            .clickable { onClick(mainCategory) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(start = paddingMedium),
            text = stringResource(id = mainCategory.label),
            style = h4
        )

        Spacer(modifier = Modifier.weight(1f))

        Image(
            modifier = Modifier.padding(end = paddingSmall, top = paddingXXXSmall),
            painter = painterResource(id = mainCategory.image),
            contentDescription = stringResource(id = mainCategory.label)
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    RumbleTheme {
        MainCategoryView(mainCategory = MainCategory.MUSIC, onClick = {})
    }
}