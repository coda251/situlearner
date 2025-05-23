package com.coda.situlearner.feature.player.word.navigation

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.navigation.bottomSheet
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.infra.Subtitle
import com.coda.situlearner.core.model.infra.Token
import com.coda.situlearner.feature.player.word.PlayerWordBottomSheet
import kotlinx.serialization.Serializable

@Serializable
internal data class PlayerWordRoute(
    val word: String,
    val language: Language,
    val mediaId: String,
    val subtitleStartTimeInMs: Long,
    val subtitleEndTimeInMs: Long,
    val subtitleSourceText: String,
    val subtitleTargetText: String?,
    val wordStartIndex: Int,
    val wordEndIndex: Int,
)

fun NavController.navigateToPlayerWord(
    token: Token,
    subtitle: Subtitle,
    language: Language,
    mediaId: String,
) {
    navigate(
        PlayerWordRoute(
            word = token.lemma,
            language = language,
            mediaId = mediaId,
            subtitleStartTimeInMs = subtitle.startTimeInMs,
            subtitleEndTimeInMs = subtitle.endTimeInMs,
            subtitleSourceText = subtitle.sourceText,
            subtitleTargetText = subtitle.targetText,
            wordStartIndex = token.startIndex,
            wordEndIndex = token.endIndex
        )
    )
}

fun NavGraphBuilder.playerWordBottomSheet(
    onBack: () -> Unit
) {
    bottomSheet<PlayerWordRoute> {
        PlayerWordBottomSheet(
            onDismiss = onBack
        )
    }
}