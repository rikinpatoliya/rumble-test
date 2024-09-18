@file:OptIn(ExperimentalTvMaterial3Api::class)

package com.rumble.ui3.subscriptions

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.rumble.R
import com.rumble.domain.sort.SortFollowingType
import com.rumble.theme.RumbleTvTypography
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXMedium
import com.rumble.theme.radiusSmall
import com.rumble.theme.rumbleGreen
import com.rumble.theme.sortDialogRowHeight
import com.rumble.theme.tvCheckboxSize
import com.rumble.theme.tvPlayerModalWidth
import kotlinx.coroutines.flow.StateFlow

private sealed class Focusable {
    object Back : Focusable()
    data class List(val type: SortFollowingType) : Focusable()
}

class FollowingSortDialogFragment(
    val sortTypeFlow: StateFlow<SortFollowingType>,
    val onDismissed: () -> Unit,
    val onSortSelected: (SortFollowingType) -> Unit,
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            setContentView(ComposeView(requireContext()).apply {
                setContent {
                    val state = sortTypeFlow.collectAsStateWithLifecycle()

                    FollowingSortDialog(
                        state.value,
                        onDismiss = {
                            dismiss()
                            onDismissed()
                        },
                        onSortSelected
                    )
                }
            })
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalTvMaterial3Api::class)
@Composable
fun FollowingSortDialog(
    selectedSortType: SortFollowingType,
    onDismiss: () -> Unit,
    onSortSelected: (SortFollowingType) -> Unit,
) {
    var focusedElement: Focusable by remember { mutableStateOf(Focusable.List(SortFollowingType.DEFAULT)) }

    val (backFocusRequester) = remember { FocusRequester.createRefs() }

    val listFocusRequesters = remember { mutableMapOf<SortFollowingType, FocusRequester>() }

    Dialog(
        onDismissRequest = {
            onDismiss()
        }) {
        val dialogWindowProvider = LocalView.current.parent as DialogWindowProvider
        dialogWindowProvider.window.setGravity(Gravity.END)

        ConstraintLayout(
            modifier =
            Modifier
                .padding(paddingSmall)
                .fillMaxHeight()
                .wrapContentWidth()
        ) {
            val (back, window) = createRefs()

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(tvPlayerModalWidth)
                    .constrainAs(window) {
                        start.linkTo(back.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .background(
                        color = enforcedDarkmo,
                        shape = RoundedCornerShape(radiusSmall)
                    )
            ) {


                Text(
                    modifier = Modifier.padding(
                        top = paddingSmall,
                        start = paddingSmall,
                        end = paddingSmall,
                        bottom = paddingXXMedium
                    ),
                    text = stringResource(id = R.string.sort_following),
                    style = RumbleTvTypography.h3Tv,
                    color = enforcedWhite
                )

                TvLazyColumn(
                    modifier = Modifier
                        .focusProperties {
                            exit = { focusDirection ->
                                if (focusDirection == FocusDirection.Left || focusDirection == FocusDirection.Up) backFocusRequester else FocusRequester.Default
                            }
                        }
                        .padding(horizontal = paddingMedium),
                    verticalArrangement = Arrangement.spacedBy(paddingSmall),
                ) {

                    items(SortFollowingType.values().toList()) { sortType ->
                        val title = stringResource(id = sortType.nameId)

                        val focusRequester = listFocusRequesters.getOrPut(sortType) { FocusRequester() }

                        FilterItem(
                            title = title,
                            sortType,
                            focusRequester = focusRequester,
                            focusedElement = focusedElement,
                            selected = sortType == selectedSortType,
                            onFocused = { focusedElement = Focusable.List(sortType) },
                            onSortSelected = onSortSelected
                        )

                        LaunchedEffect(Unit) {
                            if (focusedElement == Focusable.List(SortFollowingType.DEFAULT) && sortType == SortFollowingType.DEFAULT) {
                                focusRequester.requestFocus()
                            }
                        }

                    }
                }
            }

            Icon(
                modifier = Modifier
                    .focusTarget()
                    .focusRequester(backFocusRequester)
                    .onFocusEvent { if (it.isFocused) focusedElement = Focusable.Back }
                    .constrainAs(back) {
                        end.linkTo(window.start)
                        top.linkTo(window.top)
                    }
                    .padding(paddingMedium)
                    .clickable {
                        onDismiss()
                    },
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = stringResource(id = R.string.back),
                tint = if (focusedElement == Focusable.Back) rumbleGreen else enforcedWhite
            )
        }
    }
}

@Composable
private fun FilterItem(
    title: String,
    sortType: SortFollowingType,
    focusedElement: Focusable,
    focusRequester: FocusRequester,
    selected: Boolean,
    onFocused: (Focusable) -> Unit,
    onSortSelected: (SortFollowingType) -> Unit,
) {
    Row(
        modifier = Modifier
            .background(
                color = if (focusedElement == Focusable.List(sortType)) enforcedWhite.copy(
                    alpha = .1f
                ) else Color.Transparent,
                shape = RoundedCornerShape(radiusSmall)
            )
            .height(sortDialogRowHeight)
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusEvent {
                if (it.isFocused || it.hasFocus) {
                    onFocused(Focusable.List(sortType))
                }
            }
            .clickable {
                onSortSelected(sortType)
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Spacer(modifier = Modifier.width(paddingSmall))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = RumbleTvTypography.h5Tv,
                color = if (selected) rumbleGreen else enforcedWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(modifier = Modifier.width(paddingSmall))

        if (selected) {
            Image(
                modifier = Modifier
                    .padding(horizontal = paddingSmall, vertical = paddingXSmall)
                    .size(tvCheckboxSize),
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = stringResource(id = R.string.check)
            )
        }
    }
}