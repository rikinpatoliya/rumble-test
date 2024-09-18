package com.rumble.battles.commonViews

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rumble.battles.R
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.enforcedBone
import com.rumble.theme.paddingXSmall

@Composable
fun ErrorMessageView(
    modifier: Modifier = Modifier,
    errorMessage: String = "",
    textColor: Color,
) {

    ConstraintLayout(modifier = modifier) {
        val (icon, text) = createRefs()

        Image(
            modifier = Modifier
                .padding(end = paddingXSmall)
                .constrainAs(icon) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                },
            painter = painterResource(id = R.drawable.ic_alert_triangle_filled),
            contentDescription = errorMessage
        )

        Text(
            modifier = Modifier.constrainAs(text) {
                start.linkTo(icon.end)
                top.linkTo(parent.top)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            text = errorMessage,
            color = textColor,
            style = h6
        )
    }
}


@Composable
@Preview(showSystemUi = true)
private fun PreviewErrorMessageView() {
    ErrorMessageView(
        modifier = Modifier.fillMaxWidth(),
        errorMessage = "Error message",
        textColor = enforcedBone
    )
}