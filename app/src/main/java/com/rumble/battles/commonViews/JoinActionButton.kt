package com.rumble.battles.commonViews

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.domain.channels.channeldetails.domain.domainmodel.LocalsCommunityEntity
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.brandedLocalsRed
import com.rumble.theme.enforcedWhite

@Composable
fun JoinActionButton(
    joinActionData: JoinActionData,
    onJoin: (localsCommunityEntity: LocalsCommunityEntity) -> Unit,
) {
    val localsCommunityEntity = joinActionData.localsCommunityEntity
    when (joinActionData.joinActionType) {
        JoinActionType.WITH_TEXT -> {
            ActionButton(
                text = joinActionData.localsCommunityEntity.joinButtonText.takeIf { it.isNotBlank() }
                    ?: stringResource(id = R.string.join),
                backgroundColor = brandedLocalsRed,
                borderColor = brandedLocalsRed,
                textColor = enforcedWhite
            ) { onJoin(localsCommunityEntity) }
        }

        JoinActionType.SHOW_AS_STAR -> {
            RoundIconButton(
                painter = painterResource(id = R.drawable.ic_star),
                backgroundColor = RumbleCustomTheme.colors.backgroundHighlight,
                tintColor = RumbleCustomTheme.colors.primary,
                contentDescription = stringResource(id = R.string.join),
                onClick = { onJoin(localsCommunityEntity) }
            )
        }
    }
}

data class JoinActionData(
    val localsCommunityEntity: LocalsCommunityEntity,
    val joinActionType: JoinActionType
)

enum class JoinActionType {
    SHOW_AS_STAR,
    WITH_TEXT
}
