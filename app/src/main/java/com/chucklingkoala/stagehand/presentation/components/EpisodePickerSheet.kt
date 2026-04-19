package com.chucklingkoala.stagehand.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chucklingkoala.stagehand.R
import com.chucklingkoala.stagehand.domain.model.Episode
import com.chucklingkoala.stagehand.presentation.theme.StagehandColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodePickerSheet(
    episodes: List<Episode>,
    currentEpisodeId: Int?,
    onPick: (Int?) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var query by remember { mutableStateOf("") }
    val filtered = remember(episodes, query) {
        if (query.isBlank()) episodes
        else {
            val q = query.trim()
            episodes.filter {
                it.episodeNumber.toString().contains(q, ignoreCase = true) ||
                    (it.showNotes?.contains(q, ignoreCase = true) == true)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = StagehandColors.BackgroundSecondary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.pick_episode_title),
                style = MaterialTheme.typography.titleMedium,
                color = StagehandColors.TextPrimary
            )

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        stringResource(R.string.search_episode_hint),
                        color = StagehandColors.TextSecondary
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = StagehandColors.InputBackground,
                    unfocusedContainerColor = StagehandColors.InputBackground,
                    focusedTextColor = StagehandColors.TextPrimary,
                    unfocusedTextColor = StagehandColors.TextPrimary,
                    focusedBorderColor = StagehandColors.AccentColor,
                    unfocusedBorderColor = StagehandColors.BorderColor
                ),
                shape = RoundedCornerShape(8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item {
                    PickerRow(
                        label = stringResource(R.string.no_episode_label),
                        selected = currentEpisodeId == null,
                        onClick = { onPick(null) }
                    )
                }
                items(filtered, key = { it.id }) { episode ->
                    PickerRow(
                        label = stringResource(R.string.episode_label, episode.episodeNumber),
                        selected = episode.id == currentEpisodeId,
                        onClick = { onPick(episode.id) }
                    )
                }
            }
        }
    }
}
