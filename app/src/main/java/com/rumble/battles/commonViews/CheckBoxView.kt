package com.rumble.battles.commonViews

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.theme.radiusXSmall
import com.rumble.theme.rumbleGreen

@Composable
fun CheckBoxView(
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    onToggleCheckedState: (Boolean) -> Unit = {},
) {

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radiusXSmall))
            .clickable {
                onToggleCheckedState(!checked)
            }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_rectangle_box),
            contentDescription = if (checked) stringResource(id = R.string.checked) else stringResource(
                id = R.string.unchecked
            ),
            tint = if (checked) MaterialTheme.colors.secondary else MaterialTheme.colors.secondaryVariant,
        )
        if (checked) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = stringResource(id = R.string.checked),
                tint = rumbleGreen
            )
        }
    }
}

@Composable
@Preview
private fun PreviewCheckBoxView() {

    CheckBoxView()
}

@Composable
@Preview
private fun PreviewCheckBoxViewChecked() {

    CheckBoxView(checked = true)
}
