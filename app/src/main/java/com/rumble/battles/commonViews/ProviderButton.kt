package com.rumble.battles.commonViews

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import com.rumble.battles.R
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography
import com.rumble.theme.borderXSmall
import com.rumble.theme.imageSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.radiusXLarge

@Composable
fun ProviderButton(
    modifier: Modifier = Modifier,
    text: String = "",
    providerIcon: Painter? = null,
    backgroundColor: Color = MaterialTheme.colors.background,
    textColor: Color = MaterialTheme.colors.primary,
    borderColor: Color? = null,
    iconTint: Color? = null,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = modifier.border(
            width = borderXSmall,
            color = borderColor ?: backgroundColor,
            shape = RoundedCornerShape(radiusXLarge)
        ),
        shape = RoundedCornerShape(radiusXLarge),
        colors = ButtonDefaults.textButtonColors(
            backgroundColor = backgroundColor
        ),
        onClick = onClick
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (icon, label) = createRefs()
            providerIcon?.let { providerIcon ->
                iconTint?.let {
                    Icon(
                        modifier = Modifier
                            .size(imageSmall)
                            .constrainAs(icon) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                            },
                        painter = providerIcon,
                        contentDescription = text,
                        tint = iconTint
                    )
                } ?: run {
                    Image(
                        modifier = Modifier
                            .size(imageSmall)
                            .constrainAs(icon) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                            },
                        painter = providerIcon,
                        contentDescription = text
                    )
                }
            }

            Text(
                modifier = Modifier
                    .constrainAs(label) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(vertical = paddingXSmall),
                text = text,
                style = RumbleTypography.h3,
                color = textColor
            )
        }
    }
}

@Composable
@Preview
private fun PreviewGoogle() {
    RumbleTheme {
        ProviderButton(
            text = stringResource(id = R.string.continue_with_google),
            providerIcon = painterResource(id = R.drawable.ic_provider_google),
            backgroundColor = Color.Black,
            borderColor = Color.White,
            textColor = Color.White
        )
    }
}

@Composable
@Preview
private fun PreviewEmail() {
    RumbleTheme {
        ProviderButton(
            text = stringResource(id = R.string.continue_with_email),
            providerIcon = painterResource(id = R.drawable.ic_login_email),
            backgroundColor = Color.Black,
            borderColor = Color.White,
            textColor = Color.White,
            iconTint = Color.White
        )
    }
}