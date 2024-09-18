package com.rumble.domain.common.domain.usecase

import androidx.compose.ui.text.AnnotatedString
import javax.inject.Inject

class AnnotatedStringUseCase @Inject constructor() {
    operator fun invoke(
        annotatedTextWithActions: AnnotatedStringWithActionsList,
        offset: Int,
    ) {
        annotatedTextWithActions.actionList.forEach { annotatedTextAction ->
            annotatedTextWithActions.annotatedString.getStringAnnotations(
                tag = annotatedTextAction.tag,
                start = offset,
                end = offset
            ).firstOrNull()?.let { annotatedStringRange ->
                annotatedTextAction.action.invoke(annotatedStringRange.item)
            }
        }
    }
}

data class AnnotatedTextAction(
    val tag: String,
    val action: (String) -> Unit,
)

data class AnnotatedStringWithActionsList(
    val annotatedString: AnnotatedString,
    val actionList: List<AnnotatedTextAction>,
)