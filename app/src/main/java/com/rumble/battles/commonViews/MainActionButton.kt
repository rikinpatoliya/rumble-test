package com.rumble.battles.commonViews

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.paddingMedium
import com.rumble.theme.radiusXLarge
import com.rumble.theme.rumbleGreen

@Composable
fun MainActionButton(
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier.padding(top = paddingMedium, bottom = paddingMedium),
    text: String,
    backgroundColor: Color = rumbleGreen,
    textColor: Color = MaterialTheme.colors.primary,
    onClick: () -> Unit = {},
    textStyle: TextStyle = h3,
) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(radiusXLarge),
        colors = ButtonDefaults.textButtonColors(
            backgroundColor = backgroundColor
        ),
        onClick = onClick
    ) {
        Text(
            modifier = textModifier,
            text = text,
            style = textStyle,
            color = textColor
        )
    }
}

@Composable
@Preview
fun PreviewSignInButton() {
    MainActionButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(id = R.string.sign_in),
    )
}