package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.rumble.domain.feed.domain.domainmodel.ads.AdEntity
import com.rumble.domain.feed.domain.domainmodel.ads.AdsType
import com.rumble.theme.RumbleTypography
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXSmall
import com.rumble.theme.radiusMedium
import com.rumble.utils.RumbleConstants

@Composable
fun RevAdView(
    modifier: Modifier = Modifier,
    adEntity: AdEntity,
    onVisible: (AdEntity) -> Unit = {}
) {

    onVisible(adEntity)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.onSecondary)
    ) {
        ConstraintLayout(
            modifier = modifier
                .align(Alignment.Center)
                .padding(
                    top = paddingMedium,
                    bottom = paddingMedium,
                    start = paddingMedium,
                    end = paddingMedium
                )
        ) {
            val (thumb, tag, footer) = createRefs()
            Box(
                modifier = Modifier
                    .constrainAs(thumb) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .aspectRatio(RumbleConstants.VIDEO_CARD_THUMBNAIL_ASPECT_RATION)
                    .clip(RoundedCornerShape(radiusMedium))
                    .background(MaterialTheme.colors.primaryVariant)
            ) {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = adEntity.videoThumbnail,
                    contentDescription = "",
                    contentScale = ContentScale.FillWidth
                )
            }

            Text(
                modifier = Modifier
                    .constrainAs(footer) {
                        top.linkTo(thumb.bottom)
                    }
                    .padding(top = paddingXSmall),
                text = adEntity.title,
                style = RumbleTypography.h4,
                color = MaterialTheme.colors.primary
            )

            AdTagView(
                modifier = Modifier.constrainAs(tag) {
                    bottom.linkTo(thumb.bottom)
                    start.linkTo(thumb.start)
                }
            )
        }
    }
}

@Composable
@Preview
private fun PreviewVideoView() {

    val adEntity =
        AdEntity(
            videoThumbnail = "",
            title = "Quis malesuada interdum in enim ultricies vel in ullamcorper ",
            type = AdsType.SPONSORED,
            adUrl = ""
        )

    RevAdView(
        modifier = Modifier.fillMaxWidth(),
        adEntity = adEntity,
    )
}
