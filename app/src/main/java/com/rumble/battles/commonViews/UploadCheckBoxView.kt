package com.rumble.battles.commonViews

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.domain.common.domain.usecase.AnnotatedStringWithActionsList
import com.rumble.theme.*

@Composable
fun UploadCheckBoxView(
    modifier: Modifier = Modifier,
    text: String? = null,
    checked: Boolean = false,
    onToggleCheckedState: (Boolean) -> Unit = {},
    hasError: Boolean = false,
    errorMessage: String = "",
    errorMessageColor: Color = MaterialTheme.colors.secondary,
    annotatedTextWithActions: AnnotatedStringWithActionsList? = null,
    onAnnotatedTextClicked: ((annotatedTextWithActions: AnnotatedStringWithActionsList, offset: Int) -> Unit)? = null,
) {

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
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
            Spacer(
                Modifier
                    .width(paddingSmall)
            )
            text?.let { text ->
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = text,
                    style = RumbleTypography.h6Light,
                )
            }
            annotatedTextWithActions?.let { annotatedTextWithActions ->
                ClickableText(
                    text = annotatedTextWithActions.annotatedString,
                    style = RumbleTypography.h6Light,
                    onClick = { offset ->
                        onAnnotatedTextClicked?.let { it(annotatedTextWithActions, offset) }
                    }
                )
            }
        }

        if (hasError) {
            ErrorMessageView(
                modifier = Modifier
                    .padding(top = paddingXSmall)
                    .fillMaxWidth(),
                errorMessage = errorMessage,
                textColor = errorMessageColor
            )
        }
    }
}

@Composable
@Preview
private fun PreviewUploadCheckBoxView() {

    UploadCheckBoxView(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingMedium),
        hasError = true,
        errorMessage = "Some error message",
        annotatedTextWithActions = AnnotatedStringWithActionsList(buildAnnotatedString {
            append(stringResource(id = R.string.terms_and_conditions_part_1))
            append(" ")
            withStyle(style = SpanStyle(color = rumbleGreen)) {
                append(stringResource(id = R.string.rumble_terms_and_conditions))
            }
        }, emptyList()),
    )
}

@Composable
@Preview
private fun PreviewUploadCheckBoxViewChecked() {

    UploadCheckBoxView(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingMedium),
        text = stringResource(id = R.string.terms_and_conditions_part_1),
        checked = true,
        hasError = true,
        errorMessage = "Some error message"
    )
}
