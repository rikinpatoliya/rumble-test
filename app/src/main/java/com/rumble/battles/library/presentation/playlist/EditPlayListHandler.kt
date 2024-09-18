package com.rumble.battles.library.presentation.playlist

import com.rumble.domain.channels.channeldetails.domain.domainmodel.UserUploadChannelEntity
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.domain.library.domain.model.PlayListVisibility
import kotlinx.coroutines.flow.StateFlow

interface EditPlayListHandler {
    val editPlayListState: StateFlow<EditPlayListScreenUIState>
    val playListSettingsState: StateFlow<PlayListSettingsBottomSheetDialog>

    fun onTitleChanged(value: String)
    fun onDescriptionChanged(value: String)
    fun onCancelPlayListSettings()
    fun onOpenChannelSelectionBottomSheet()
    fun onOpenPlayListVisibilitySelectionBottomSheet()
    fun onSavePlayListSettings(playListAction: PlayListAction, videoId: Long? = null)
    fun onPlayListVisibilityChanged(playListVisibility: PlayListVisibility)
    fun onPlayListOwnerChanged(ownerId: String)
}

data class EditPlayListScreenUIState(
    val editPlayListEntity: PlayListEntity? = null,
    val userChannel: UserUploadChannelEntity = UserUploadChannelEntity(),
    val userUploadChannels: List<UserUploadChannelEntity> = emptyList(),
    val titleError: Boolean = false,
    val descriptionError: Boolean = false,
)

sealed class PlayListSettingsBottomSheetDialog {
    data class PlayListVisibilitySelection(val playListEntity: PlayListEntity) :
        PlayListSettingsBottomSheetDialog()

    data class PlayListChannelSelection(
        val playListEntity: PlayListEntity,
        val userUploadChannels: List<UserUploadChannelEntity>
    ) : PlayListSettingsBottomSheetDialog()

    object DefaultPopupState : PlayListSettingsBottomSheetDialog()
}