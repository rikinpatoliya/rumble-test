package com.rumble.battles.livechat.presentation.rant

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumble.battles.PaidRantTag
import com.rumble.battles.R
import com.rumble.battles.commonViews.ActionButton
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.battles.commonViews.keyboardAsState
import com.rumble.battles.feed.presentation.videodetails.VideoDetailsHandler
import com.rumble.battles.landing.RumbleActivityHandler
import com.rumble.battles.livechat.presentation.LiveChatHandler
import com.rumble.battles.livechat.presentation.content.LiveChatUserNameView
import com.rumble.domain.livechat.domain.domainmodel.LiveChatConfig
import com.rumble.domain.livechat.domain.domainmodel.RantConfig
import com.rumble.domain.livechat.domain.domainmodel.RantLevel
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography
import com.rumble.theme.RumbleTypography.body1
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.fierceRed
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.radiusMedium
import com.rumble.theme.radiusRant
import com.rumble.theme.radiusSmall
import com.rumble.theme.radiusXMedium
import com.rumble.theme.radiusXSmall
import com.rumble.theme.radiusXXXXSmall
import com.rumble.theme.rantStartPadding
import com.rumble.theme.rantTopPadding
import com.rumble.theme.wokeGreen
import com.rumble.utils.RumbleConstants
import com.rumble.utils.extension.getComposeColor
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@Composable
fun BuyRantSheet(
    modifier: Modifier = Modifier,
    handler: VideoDetailsHandler,
    liveChatHandler: LiveChatHandler,
    activityHandler: RumbleActivityHandler,
    expanded: Boolean
) {
    val state by remember { handler.state }
    val liveChatState by remember { liveChatHandler.state }
    val userName by handler.userNameFlow.collectAsStateWithLifecycle(initialValue = "")
    val userThumbnail by handler.userPictureFlow.collectAsStateWithLifecycle(initialValue = "")
    val rantMessage = state.currentComment
    val rantLevelList = liveChatState.liveChatConfig?.rantConfig?.levelList
    val maxCharCount =
        liveChatState.liveChatConfig?.messageMaxLength
            ?: RumbleConstants.LIVE_MESSAGE_MAX_CHARACTERS
    val sendEnabled = rantMessage.isNotBlank() && rantMessage.count() <= maxCharCount
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(expanded) {
        if (expanded) focusRequester.requestFocus()
        else keyboardController?.hide()
    }

    Box(
        modifier
            .testTag(PaidRantTag)
            .focusRequester(focusRequester)
            .systemBarsPadding()
            .imePadding()
            .clip(RoundedCornerShape(topStart = radiusXMedium, topEnd = radiusXMedium))
            .background(MaterialTheme.colors.onPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            HeaderView(
                onTerms = {
                    liveChatHandler.onReportRantTermsEvent()
                    activityHandler.onOpenWebView(it)
                },
                onClose = {
                    liveChatHandler.onClearRantSelection()
                    handler.onCloseBuyRant(rantMessage)
                })
            RantMessageView(
                rantLevelList = liveChatState.liveChatConfig?.rantConfig?.levelList ?: emptyList(),
                message = rantMessage,
                userName = if (state.selectedLiveChatAuthor == null) userName else state.selectedLiveChatAuthor?.title ?: "",
                userThumbnail = if (state.selectedLiveChatAuthor == null) userThumbnail else state.selectedLiveChatAuthor?.thumbnail ?: "",
                rantSelection = liveChatState.rantSelected,
                liveChatConfig = liveChatState.liveChatConfig,
                onChange = {
                    handler.onCommentChanged(comment = it)
                }
            )
            RantDescriptionView(
                rantLevelList = liveChatState.liveChatConfig?.rantConfig?.levelList ?: emptyList(),
                maxCharCount = maxCharCount,
                currentCharCount = rantMessage.count(),
                rantSelection = liveChatState.rantSelected
            )
            RantSelectionView(
                rantLevelList = liveChatState.liveChatConfig?.rantConfig?.levelList ?: emptyList(),
                rantSelection = liveChatState.rantSelected,
                onRantSelected = { selection ->
                    rantLevelList?.let {
                        liveChatHandler.onRantLevelSelected(rantLevelList[selection])
                    }
                }
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = paddingMedium, end = paddingMedium),
                color = MaterialTheme.colors.background
            )
            ActionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingMedium),
                text = stringResource(id = R.string.buy_send),
                trailingIconPainter = painterResource(id = R.drawable.ic_send),
                textStyle = h4,
                textColor = enforcedDarkmo,
                enabled = sendEnabled,
                showBorder = false,
                onClick = {
                    liveChatState.rantSelected?.let { rantLevel ->
                        liveChatState.liveChatConfig?.chatId?.let { chatId ->
                            handler.onPostLiveChatMessage(chatId, rantLevel)
                        }
                    }
                },
                textModifier = Modifier.padding(paddingXSmall)
            )
        }
    }
}

@Composable
private fun HeaderView(
    onTerms: (String) -> Unit,
    onClose: () -> Unit
) {
    val terms = stringResource(id = R.string.terms)
    val termsAnnotatedText = buildAnnotatedString {
        withStyle(SpanStyle(color = MaterialTheme.colors.primary)) {
            append(stringResource(id = R.string.by_continuing))
            append(" ")
        }
        withStyle(SpanStyle(color = wokeGreen)) {
            pushStringAnnotation(
                stringResource(id = R.string.rumble_mobile_terms),
                terms
            )
            append(terms)
        }
    }
    val keyboardVisible by keyboardAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = paddingMedium, top = paddingSmall),
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.send_a_rant),
                style = h3,
                color = MaterialTheme.colors.primary
            )
            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = onClose) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = stringResource(id = R.string.info),
                    tint =  MaterialTheme.colors.primary
                )
            }
        }

        if (keyboardVisible.not()) {
            ClickableText(
                modifier = Modifier.padding(end = paddingMedium),
                text = termsAnnotatedText,
                style = RumbleTypography.tinyBodyNormal,
                onClick = {
                    termsAnnotatedText.getStringAnnotations(it, it).firstOrNull()?.let { span ->
                        onTerms(span.tag)
                    }
                }
            )
        }
    }
}

@Composable
private fun RantMessageView(
    rantLevelList: List<RantLevel>,
    message: String,
    userName: String,
    userThumbnail: String,
    rantSelection: RantLevel?,
    liveChatConfig: LiveChatConfig?,
    onChange: (String) -> Unit
) {
    val initSelection = rantSelection ?: rantLevelList.firstOrNull()
    val titleBackgroundColor =
        initSelection?.backgroundColor?.getComposeColor() ?: MaterialTheme.colors.background
    val backgroundColor =
        initSelection?.mainColor?.getComposeColor() ?: MaterialTheme.colors.background
    val textColor =
        initSelection?.foregroundColor?.getComposeColor() ?: MaterialTheme.colors.primary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingMedium)
            .clip(RoundedCornerShape(radiusSmall))
            .background(backgroundColor)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(titleBackgroundColor)
                    .padding(
                        start = paddingXSmall,
                        end = paddingXSmall,
                        top = paddingSmall,
                        bottom = paddingSmall
                    ),
                verticalAlignment = Alignment.Top
            ) {
                ProfileImageComponent(
                    profileImageComponentStyle = ProfileImageComponentStyle.CircleImageSmallStyle(),
                    userName = userName,
                    userPicture = userThumbnail
                )

                LiveChatUserNameView(
                    modifier = Modifier.padding(start = paddingXSmall),
                    userName = userName,
                    userBadges = liveChatConfig?.currentUserBadges ?: emptyList(),
                    badges = liveChatConfig?.badges ?: emptyMap(),
                    rantPrice = initSelection?.rantPrice,
                    currencySymbol = liveChatConfig?.currencySymbol
                        ?: stringResource(id = R.string.default_currency_symbol),
                    textColor = textColor,
                    textStyle = h4
                )
            }

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor),
                value = message,
                onValueChange = { onChange(it) },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    cursorColor = textColor,
                    textColor = textColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                textStyle = body1,
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.add_message),
                        style = body1,
                        color = textColor.copy(0.5f)
                    )
                }
            )
        }
    }
}

@Composable
private fun RantDescriptionView(
    rantLevelList: List<RantLevel>,
    maxCharCount: Int,
    currentCharCount: Int,
    rantSelection: RantLevel?
) {
    val initSelection = rantSelection ?: rantLevelList.firstOrNull()
    val minutes = TimeUnit.SECONDS.toMinutes(initSelection?.duration?.toLong() ?: 0)
    val durationString = pluralStringResource(R.plurals.minutes_duration, minutes.toInt(), minutes)
    val rantText = buildAnnotatedString {
        withStyle(SpanStyle(fontWeight = FontWeight.Normal)) {
            append(stringResource(id = R.string.rant_will_stick))
        }
        append(" ")
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append(durationString)
        }
    }
    val textColor =
        if (currentCharCount in 1..maxCharCount) MaterialTheme.colors.primaryVariant else fierceRed

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = paddingMedium, end = paddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = rantText,
            style = h4,
            color = MaterialTheme.colors.primary
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "$currentCharCount/$maxCharCount",
            style = h4,
            color = textColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RantSelectionView(
    rantLevelList: List<RantLevel>,
    rantSelection: RantLevel?,
    onRantSelected: (Int) -> Unit
) {
    val steps = rantLevelList.size - 2
    var position by remember { mutableFloatStateOf(0f) }
    val initSelection = rantSelection ?: rantLevelList.firstOrNull()
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingMedium)
            .clip(RoundedCornerShape(radiusSmall))
            .background(MaterialTheme.colors.onSurface),
        contentAlignment = Alignment.Center
    ) {
        if (steps >= 0) {
            StepsView(
                modifier = Modifier
                    .padding(
                        start = paddingSmall,
                        end = paddingSmall
                    )
                    .align(Alignment.Center),
                rantLevelList = rantLevelList
            )
            Slider(
                modifier = Modifier.padding(
                    start = rantStartPadding,
                    end = paddingXXSmall,
                    top = rantTopPadding
                ),
                valueRange = 0f..(rantLevelList.size - 1).toFloat(),
                value = position,
                steps = steps,
                onValueChange = { position = it },
                onValueChangeFinished = { onRantSelected(position.roundToInt()) },
                thumb = {
                    SliderDefaults.Thumb(
                        interactionSource = interactionSource,
                        thumbSize = DpSize(radiusMedium, radiusMedium),
                        colors = SliderDefaults.colors(
                            thumbColor = initSelection?.mainColor?.getComposeColor()
                                ?: MaterialTheme.colors.primary,
                        )
                    )
                },
                colors = SliderDefaults.colors(
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent,
                    activeTickColor = Color.Transparent,
                    inactiveTickColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
private fun StepsView(
    modifier: Modifier,
    rantLevelList: List<RantLevel>
) {
    val lineColor = Color(MaterialTheme.colors.secondaryVariant.toArgb())
    val doteColor = Color(MaterialTheme.colors.primaryVariant.toArgb())
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(radiusSmall)
    ) {
        val drawPadding: Float = with(LocalDensity.current) { radiusXSmall.toPx() }
        val lineHeight: Float = with(LocalDensity.current) { radiusXXXXSmall.toPx() }
        val doteRadius: Float = with(LocalDensity.current) { radiusRant.toPx() }
        Canvas(modifier = Modifier.fillMaxSize()) {
            val yStart = 0f
            val distance: Float =
                (size.width.minus(2 * drawPadding)).div(rantLevelList.size.minus(1))
            drawLine(
                color = lineColor,
                strokeWidth = lineHeight,
                start = Offset(x = drawPadding, y = drawPadding.div(2)),
                end = Offset(x = size.width.minus(drawPadding), y = drawPadding.div(2))
            )
            rantLevelList.forEachIndexed { index, _ ->
                drawCircle(
                    color = doteColor,
                    radius = doteRadius,
                    center = Offset(
                        x = drawPadding.plus(index.times(distance)),
                        y = yStart.plus(drawPadding.div(2))
                    )
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun HeaderViewPreview() {
    RumbleTheme {
        HeaderView(
            onTerms = {},
            onClose = {}
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun MessagePreview() {
    RumbleTheme {
        RantMessageView(
            rantLevelList = emptyList(),
            message = "",
            userName = "",
            userThumbnail = "",
            rantSelection = RantLevel(
                BigDecimal.TEN,
                600,
                "",
                "",
                "green",
                rantId = ""
            ),
            liveChatConfig = LiveChatConfig(
                0,
                RantConfig(emptyList(), true),
                emptyMap(),
                100,
                emptyList(),
                "",
                channels = emptyList()
            ),
            onChange = {}
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    RumbleTheme {
        Column {
            RantDescriptionView(
                rantLevelList = emptyList(),
                maxCharCount = 200,
                currentCharCount = 150,
                rantSelection = RantLevel(
                    BigDecimal.TEN,
                    600,
                    "",
                    "",
                    "green",
                    rantId = ""
                )
            )

            RantSelectionView(
                listOf(
                    RantLevel(
                        BigDecimal(1),
                        60,
                        "",
                        "",
                        "green",
                        rantId = ""
                    ),
                    RantLevel(
                        BigDecimal(2),
                        120,
                        "",
                        "",
                        "green",
                        rantId = ""
                    ),
                    RantLevel(
                        BigDecimal(3),
                        180,
                        "",
                        "",
                        "green",
                        rantId = ""
                    )
                ),
                null
            ) {}

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = paddingMedium, end = paddingMedium),
                color = MaterialTheme.colors.secondaryVariant
            )

            ActionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingMedium),
                text = stringResource(id = R.string.buy_send),
                trailingIconPainter = painterResource(id = R.drawable.ic_send),
                textStyle = h4,
                textColor = enforcedDarkmo,
            )
        }
    }
}


