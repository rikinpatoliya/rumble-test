package com.rumble.ui3.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rumble.theme.RumbleTypography.h1
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.borderWidth
import com.rumble.theme.borderXSmall
import com.rumble.theme.enforcedCloud
import com.rumble.theme.enforcedWhite
import com.rumble.theme.fierceRed
import com.rumble.theme.getPlaceholderColor
import com.rumble.theme.imageHeightLarge
import com.rumble.theme.imageHeightXXLarge
import com.rumble.theme.imageLarge
import com.rumble.theme.imageMedium
import com.rumble.theme.imageSmall
import com.rumble.theme.imageWidthLarge
import com.rumble.theme.imageWidthXXLarge
import com.rumble.theme.imageXLarge
import com.rumble.theme.imageXMedium
import com.rumble.theme.imageXSmall
import com.rumble.theme.imageXXLarge
import com.rumble.theme.imageXXXMedium
import com.rumble.theme.radiusXLarge
import com.rumble.theme.rumbleGreen
import com.rumble.utils.extension.conditional

sealed class ProfileImageComponentStyle(
    open val modifier: Modifier,
    open val textStyle: TextStyle,
    open val borderColor: Color?,
) {
    data class CircleImageNavBarIconSelectedStyle(
        override val modifier: Modifier = Modifier
            .size(imageSmall)
            .clip(CircleShape),
        override val textStyle: TextStyle = h4,
        override val borderColor: Color = rumbleGreen,
    ) : ProfileImageComponentStyle(modifier, textStyle, borderColor)

    data class CircleImageXSmallStyle(
        override val modifier: Modifier = Modifier
            .size(imageXSmall)
            .clip(CircleShape),
        override val textStyle: TextStyle = h6,
        override val borderColor: Color? = null,
    ) : ProfileImageComponentStyle(modifier, textStyle, borderColor)

    data class CircleImageSmallStyle(
        override val modifier: Modifier = Modifier
            .size(imageSmall)
            .clip(CircleShape),
        override val textStyle: TextStyle = h4,
        override val borderColor: Color? = null,
    ) : ProfileImageComponentStyle(modifier, textStyle, borderColor)

    data class CircleImageMediumStyle(
        override val modifier: Modifier = Modifier
            .size(imageMedium)
            .clip(CircleShape),
        override val textStyle: TextStyle = h3,
        override val borderColor: Color? = null,
    ) : ProfileImageComponentStyle(modifier, textStyle, borderColor)

    data class CircleImageXMediumStyle(
        override val modifier: Modifier = Modifier
            .size(imageXMedium)
            .clip(CircleShape),
        override val textStyle: TextStyle = h3,
        override val borderColor: Color? = null,
    ) : ProfileImageComponentStyle(modifier, textStyle, borderColor)

    data class CircleImageXXMediumStyle(
        override val modifier: Modifier = Modifier
            .size(imageXXXMedium)
            .clip(CircleShape),
        override val textStyle: TextStyle = h3,
        override val borderColor: Color? = null,
    ) : ProfileImageComponentStyle(modifier, textStyle, borderColor)

    data class CircleImageLargeStyle(
        override val modifier: Modifier = Modifier
            .size(imageLarge)
            .clip(CircleShape),
        override val textStyle: TextStyle = h1,
        override val borderColor: Color? = null,
    ) : ProfileImageComponentStyle(modifier, textStyle, borderColor)

    data class CircleImageXLargeStyle(
        override val modifier: Modifier = Modifier
            .size(imageXLarge)
            .clip(CircleShape),
        override val textStyle: TextStyle = h1,
        override val borderColor: Color? = null,
    ) : ProfileImageComponentStyle(modifier, textStyle, borderColor)

    data class CircleImageXXLargeStyle(
        override val modifier: Modifier = Modifier
            .size(imageXXLarge)
            .clip(CircleShape),
        override val textStyle: TextStyle = h1,
        override val borderColor: Color? = null,
    ) : ProfileImageComponentStyle(modifier, textStyle, borderColor)

    data class OvalImageLargeStyle(
        override val modifier: Modifier = Modifier
            .height(
                imageHeightLarge
            )
            .width(imageWidthLarge)
            .clip(RoundedCornerShape(radiusXLarge)),
        override val textStyle: TextStyle = h1,
        override val borderColor: Color? = null,
    ) : ProfileImageComponentStyle(modifier, textStyle, borderColor)

    data class OvalImageXXLargeStyle(
        override val modifier: Modifier = Modifier
            .height(
                imageHeightXXLarge
            )
            .width(imageWidthXXLarge)
            .clip(RoundedCornerShape(radiusXLarge)),
        override val textStyle: TextStyle = h1,
        override val borderColor: Color? = null,
    ) : ProfileImageComponentStyle(modifier, textStyle, borderColor)
}

@Composable
fun ProfileImageComponent(
    modifier: Modifier = Modifier,
    profileImageComponentStyle: ProfileImageComponentStyle,
    userName: String = "",
    userPicture: String = "",
) {
    Box(modifier = modifier
        .conditional(profileImageComponentStyle.borderColor != null) {
            border(
                width = borderXSmall,
                color = profileImageComponentStyle.borderColor as Color,
                shape = RoundedCornerShape(radiusXLarge)
            )
        }
    ) {
        val borderColor = MaterialTheme.colorScheme.background
        UserNamePlaceholderView(profileImageComponentStyle, userName)
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(userPicture)
                .crossfade(true)
                .build(),
            contentDescription = userName,
            modifier = profileImageComponentStyle.modifier
                .conditional(profileImageComponentStyle.borderColor != null) {
                    border(
                        width = borderWidth,
                        color = borderColor,
                        shape = RoundedCornerShape(radiusXLarge)
                    )
                },
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun UserNamePlaceholderView(
    profileImageComponentStyle: ProfileImageComponentStyle,
    userName: String,
) {
    if (userName.isNotBlank()) {
        Box(
            modifier = profileImageComponentStyle.modifier
                .background(getPlaceholderColor(userName))
                .conditional(profileImageComponentStyle.borderColor != null) {
                    border(
                        width = borderWidth,
                        color = profileImageComponentStyle.borderColor as Color,
                        shape = RoundedCornerShape(radiusXLarge)
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = userName.first().toString().uppercase(),
                color = enforcedWhite,
                style = profileImageComponentStyle.textStyle
            )
        }
    } else {
        DefaultColorBox(profileImageComponentStyle.modifier)
    }
}

@Composable
private fun DefaultColorBox(modifier: Modifier) {
    Box(
        modifier = modifier
            .background(enforcedCloud)
    )
}

@Composable
@Preview(group = "Default")
private fun PreviewXSmallCircleIcon() {
    ProfileImageComponent(
        profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXSmallStyle(),
    )
}

@Composable
@Preview(group = "Default")
private fun PreviewSmallCircleIcon() {
    ProfileImageComponent(
        profileImageComponentStyle = ProfileImageComponentStyle.CircleImageSmallStyle(),
    )
}

@Composable
@Preview(group = "Default")
private fun PreviewBottomBarIcon() {
    ProfileImageComponent(
        profileImageComponentStyle = ProfileImageComponentStyle.CircleImageMediumStyle(),
    )
}

@Composable
@Preview(group = "Default")
private fun PreviewLargeCircleIcon() {
    ProfileImageComponent(
        profileImageComponentStyle = ProfileImageComponentStyle.CircleImageLargeStyle(),
    )
}

@Composable
@Preview(group = "Default")
private fun PreviewXLargeCircleIcon() {
    ProfileImageComponent(
        profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXLargeStyle(),
    )
}

@Composable
@Preview(group = "Default")
private fun PreviewXXLargeCircleIcon() {
    ProfileImageComponent(
        profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXXLargeStyle(),
    )
}

@Composable
@Preview(group = "Default")
private fun PreviewLargeOvalIcon() {
    ProfileImageComponent(
        profileImageComponentStyle = ProfileImageComponentStyle.OvalImageLargeStyle(),
    )
}

@Composable
@Preview(group = "Default")
private fun PreviewXXLargeOvalIcon() {
    ProfileImageComponent(
        profileImageComponentStyle = ProfileImageComponentStyle.OvalImageXXLargeStyle(),
    )
}

@Composable
@Preview(group = "UserName")
private fun PreviewXSmallCircleIconUserNameK() {
    ProfileImageComponent(
        profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXSmallStyle(),
        userName = "Kos"
    )
}

@Composable
@Preview(group = "UserName")
private fun PreviewSmallCircleIconUserNameI() {
    ProfileImageComponent(
        profileImageComponentStyle = ProfileImageComponentStyle.CircleImageSmallStyle(),
        userName = "Igor"
    )
}

@Composable
@Preview(group = "UserName")
private fun PreviewBottomBarIconUserNameJ() {
    ProfileImageComponent(
        profileImageComponentStyle = ProfileImageComponentStyle.CircleImageMediumStyle(),
        userName = "Jay"
    )
}

@Composable
@Preview(group = "UserName")
private fun PreviewLargeCircleIconUserNameD() {
    ProfileImageComponent(
        profileImageComponentStyle = ProfileImageComponentStyle.CircleImageLargeStyle(),
        userName = "Don"
    )
}

@Composable
@Preview(group = "UserName")
private fun PreviewXLargeCircleIconUserNameM() {
    ProfileImageComponent(
        profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXLargeStyle(),
        userName = "Mike"
    )
}

@Composable
@Preview(group = "UserName")
private fun PreviewXXLargeCircleIconUserNameA() {
    ProfileImageComponent(
        profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXXLargeStyle(),
        userName = "Anna"
    )
}

@Composable
@Preview(group = "UserName")
private fun PreviewLargeOvalIconUserNameX() {
    ProfileImageComponent(
        profileImageComponentStyle = ProfileImageComponentStyle.OvalImageLargeStyle(),
        userName = "Xenos"
    )
}

@Composable
@Preview(group = "UserName")
private fun PreviewXXLargeOvalIconUserNameL() {
    ProfileImageComponent(
        profileImageComponentStyle = ProfileImageComponentStyle.OvalImageXXLargeStyle(),
        userName = "Lara"
    )
}

@Composable
@Preview(group = "Border")
private fun PreviewXXLargeCircleIconUserNameABorder() {
    ProfileImageComponent(
        profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXXLargeStyle(
            borderColor = fierceRed
        ),
        userName = "Anna"
    )
}

@Composable
@Preview(group = "Border")
private fun PreviewLargeOvalIconUserNameXBorder() {
    ProfileImageComponent(
        profileImageComponentStyle = ProfileImageComponentStyle.OvalImageLargeStyle(
            borderColor = fierceRed
        ),
        userName = "Xenos"
    )
}

@Composable
@Preview(group = "Border")
private fun PreviewSmallCircleIconUserNameIBorder() {
    ProfileImageComponent(
        profileImageComponentStyle = ProfileImageComponentStyle.CircleImageSmallStyle(),
        userName = "Igor"
    )
}

