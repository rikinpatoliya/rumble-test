package com.rumble.battles.sort

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.battles.SearchFiltersTag
import com.rumble.battles.commonViews.BottomNavigationBarScreenSpacer
import com.rumble.battles.commonViews.CheckMarkItem
import com.rumble.battles.commonViews.RumbleTextActionButton
import com.rumble.domain.sort.DurationType
import com.rumble.domain.sort.FilterType
import com.rumble.domain.sort.SortType
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXLarge
import com.rumble.theme.paddingXXLarge
import com.rumble.theme.radiusXMedium
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class SortFilterSelection(
    val sortSelection: SortType,
    val filterSelection: FilterType,
    val durationSelection: DurationType
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SortFilterBottomSheet(
    modifier: Modifier = Modifier,
    bottomSheetState: ModalBottomSheetState,
    coroutineScope: CoroutineScope,
    selection: SortFilterSelection,
    onApply: (SortFilterSelection) -> Unit,
) {
    var sortSelection by remember { mutableStateOf(selection.sortSelection) }
    var filterSelection by remember { mutableStateOf(selection.filterSelection) }
    var durationSelection by remember { mutableStateOf(selection.durationSelection) }
    var newSelection by remember { mutableStateOf(selection) }

    LaunchedEffect(bottomSheetState.currentValue) {
        sortSelection = newSelection.sortSelection
        filterSelection = newSelection.filterSelection
        durationSelection = newSelection.durationSelection
    }

    val sortTypes = SortType.values()
    val filterTypes = FilterType.values()
    val durationTypes = DurationType.values()

    Box(
        modifier = modifier
            .testTag(SearchFiltersTag)
            .clip(RoundedCornerShape(topStart = radiusXMedium, topEnd = radiusXMedium))
            .background(MaterialTheme.colors.background)
            .navigationBarsPadding()
            .statusBarsPadding()
            .systemBarsPadding()
    ) {

        Column(
            modifier = Modifier
                .padding(bottom = paddingXXLarge)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = paddingMedium,
                        end = paddingMedium,
                        bottom = paddingMedium,
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RumbleTextActionButton(text = stringResource(id = R.string.cancel)) {
                    sortSelection = selection.sortSelection
                    filterSelection = selection.filterSelection
                    durationSelection = selection.durationSelection
                    coroutineScope.launch { bottomSheetState.hide() }
                }
                Spacer(modifier = Modifier.weight(1f))
                RumbleTextActionButton(text = stringResource(id = R.string.apply)) {
                    newSelection = SortFilterSelection(
                        sortSelection,
                        filterSelection,
                        durationSelection
                    )
                    onApply(newSelection)
                    coroutineScope.launch { bottomSheetState.hide() }
                }
            }

            Text(
                modifier = Modifier.padding(
                    start = paddingMedium,
                    top = paddingLarge,
                    bottom = paddingMedium
                ),
                text = stringResource(id = R.string.sort_by),
                style = h3
            )

            sortTypes.forEach { sort ->
                CheckMarkItem(
                    sortFilter = sort,
                    selected = sort == sortSelection,
                    addSeparator = sortTypes.last() != sort
                ) { sortSelection = sort }
            }

            Text(
                modifier = Modifier.padding(
                    start = paddingMedium,
                    top = paddingXLarge,
                    bottom = paddingMedium
                ),
                text = stringResource(id = R.string.upload_date),
                style = h3
            )

            filterTypes.forEach { filter ->
                CheckMarkItem(
                    sortFilter = filter,
                    selected = filter == filterSelection,
                    addSeparator = filterTypes.last() != filter
                ) { filterSelection = filter }
            }

            Text(
                modifier = Modifier.padding(
                    start = paddingMedium,
                    top = paddingXLarge,
                    bottom = paddingMedium
                ),
                text = stringResource(id = R.string.duration),
                style = h3
            )

            durationTypes.forEach { duration ->
                CheckMarkItem(
                    sortFilter = duration,
                    selected = duration == durationSelection,
                    addSeparator = durationTypes.last() != duration
                ) { durationSelection = duration }
            }
            BottomNavigationBarScreenSpacer()
        }
    }
}