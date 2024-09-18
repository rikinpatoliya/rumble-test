package com.rumble.battles.commonViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography
import com.rumble.theme.RumbleTypography.body1
import com.rumble.theme.enforcedGray900
import com.rumble.theme.enforcedWhite
import com.rumble.theme.imageXXSmall
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXXMedium
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusSmall
import com.rumble.utils.extension.clickableNoRipple

data class MenuSelectionItem(
    val text: String,
    val action: () -> Unit = {},
)

@Composable
fun RumbleDropDownMenu(
    modifier: Modifier = Modifier,
    itemsModifier: Modifier = Modifier,
    label: String = "",
    placeHolder: String,
    initialValue: MenuSelectionItem? = null,
    labelColor: Color = MaterialTheme.colors.onPrimary,
    backgroundColor: Color = MaterialTheme.colors.background,
    textColor: Color = MaterialTheme.colors.primary,
    iconTint: Color = MaterialTheme.colors.primary,
    items: List<MenuSelectionItem> = emptyList(),
    onClearSelection: () -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }
    var currentSelectedItem: MenuSelectionItem? by remember { mutableStateOf(initialValue) }

    Column(modifier = modifier) {

        Text(
            modifier = Modifier.padding(bottom = paddingXXXSmall),
            style = RumbleTypography.h6Heavy,
            text = label,
            color = labelColor,
            textAlign = TextAlign.Start
        )

        Box(
            modifier = Modifier
                .clickableNoRipple { expanded = true }
                .clip(RoundedCornerShape(radiusSmall))
                .background(backgroundColor)
                .padding(horizontal = paddingMedium, vertical = paddingXXMedium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                currentSelectedItem?.let {
                    Text(
                        text = it.text,
                        color = textColor,
                        style = body1
                    )
                } ?: run {
                    Text(
                        text = placeHolder,
                        color = textColor.copy(alpha = 0.5f),
                        style = body1
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                currentSelectedItem?.let {
                    Icon(
                        modifier = Modifier
                            .size(imageXXSmall)
                            .clickableNoRipple {
                                currentSelectedItem = null
                                onClearSelection()
                            },
                        painter = painterResource(id = R.drawable.ic_clear),
                        contentDescription = stringResource(id = R.string.clear_all),
                        tint = iconTint
                    )
                } ?: run {
                    Icon(
                        modifier = Modifier.size(imageXXSmall),
                        painter = painterResource(id = R.drawable.ic_chevron_down),
                        contentDescription = stringResource(id = R.string.select),
                        tint = iconTint
                    )
                }
            }

            DropdownMenu(
                modifier = itemsModifier
                    .background(backgroundColor),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEach {
                    DropdownMenuItem(
                        onClick = {
                            currentSelectedItem = it
                            expanded = false
                            it.action.invoke()
                        }
                    ) {
                        Text(
                            text = it.text,
                            color = textColor,
                            style = body1
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun Preview() {
    RumbleTheme {
        RumbleDropDownMenu(
            modifier = Modifier.fillMaxWidth(),
            placeHolder = "Test selection",
            label = "Test label",
            backgroundColor = enforcedGray900,
            textColor = enforcedWhite,
            iconTint = enforcedWhite
        )
    }
}