package com.rumble.battles.rumbleads.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.AsyncImage
import com.rumble.battles.R
import com.rumble.battles.feed.presentation.views.AdTagView
import com.rumble.domain.feed.domain.domainmodel.ads.RumbleAdEntity
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.RumbleTypography.h6Light
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusMedium
import com.rumble.utils.RumbleConstants
import com.rumble.utils.extension.clickableNoRipple
import java.time.LocalDateTime

@Composable
fun RumbleAdView(
    modifier: Modifier = Modifier,
    rumbleAdEntity: RumbleAdEntity,
    onClick: (RumbleAdEntity) -> Unit = {},
    onLaunch: (RumbleAdEntity) -> Unit = {},
    onResumed: (RumbleAdEntity) -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            onResumed(rumbleAdEntity)
        }
    }

    LaunchedEffect(LocalContext.current) {
        onLaunch(rumbleAdEntity)
    }

    DisposableEffect(Unit) {
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = modifier
            .clickableNoRipple { onClick(rumbleAdEntity) }
            .fillMaxWidth(),
    ) {
        rumbleAdEntity.brand?.let { brand ->
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(bottom = paddingXSmall),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f, false)
                        .padding(end = paddingXSmall),
                    text = brand,
                    style = h6,
                )

                Text(
                    modifier = Modifier.padding(end = paddingXSmall),
                    text = stringResource(id = R.string.ad_sponsored),
                    style = h6Light,
                    color = MaterialTheme.colors.secondary
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(RumbleConstants.RUMBLE_AD_CARD_ASPECT_RATION)
                .clip(RoundedCornerShape(radiusMedium))
                .background(MaterialTheme.colors.onSecondary)
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = rumbleAdEntity.assetUrl,
                contentDescription = "",
                contentScale = ContentScale.FillWidth
            )

            if (rumbleAdEntity.title == null && rumbleAdEntity.brand == null) {
                AdTagView(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(
                            bottom = paddingXXXSmall,
                            start = paddingXXXSmall,
                        )
                )
            }
        }

        rumbleAdEntity.title?.let { title ->
            Row(
                modifier = Modifier.padding(top = paddingXSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = paddingXSmall),
                    text = title,
                    style = h4
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_link_out),
                    contentDescription = title
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    val adEntity = RumbleAdEntity(
        impressionUrl = "",
        clickUrl = "",
        assetUrl = "",
        expirationLocal = LocalDateTime.now()
    )
    RumbleTheme {
        RumbleAdView(
            modifier = Modifier.padding(
                paddingMedium
            ),
            rumbleAdEntity = adEntity
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewNative() {
    val adEntity = RumbleAdEntity(
        impressionUrl = "",
        clickUrl = "",
        assetUrl = "",
        expirationLocal = LocalDateTime.now(),
        brand = "BMW",
        title = "Best car ever"
    )
    RumbleTheme {
        RumbleAdView(
            modifier = Modifier
                .width(400.dp)
                .padding(
                    paddingMedium
                ),
            rumbleAdEntity = adEntity
        )
    }
}