package com.chucklingkoala.stagehand.presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chucklingkoala.stagehand.R
import com.chucklingkoala.stagehand.presentation.theme.StagehandColors

@Composable
fun BulkActionBar(
    selectedCount: Int,
    onClear: () -> Unit,
    onAssignCategory: () -> Unit,
    onAssignEpisode: () -> Unit,
    onFlagOnShow: () -> Unit,
    onFlagDumped: () -> Unit,
    onMarkCovered: () -> Unit,
    onUncover: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        color = StagehandColors.BackgroundSecondary,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClear) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.clear_selection),
                        tint = StagehandColors.TextPrimary
                    )
                }
                Text(
                    text = stringResource(R.string.selected_count, selectedCount),
                    color = StagehandColors.TextPrimary,
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BulkButton(
                    label = stringResource(R.string.bulk_assign_category),
                    onClick = onAssignCategory
                )
                BulkButton(
                    label = stringResource(R.string.bulk_assign_episode),
                    onClick = onAssignEpisode
                )
                BulkButton(
                    label = stringResource(R.string.bulk_on_show),
                    onClick = onFlagOnShow,
                    bg = StagehandColors.OnShowGreen,
                    fg = Color.Black
                )
                BulkButton(
                    label = stringResource(R.string.bulk_dumped),
                    onClick = onFlagDumped,
                    bg = StagehandColors.DumpRed,
                    fg = Color.White
                )
                BulkButton(
                    label = stringResource(R.string.bulk_mark_covered),
                    onClick = onMarkCovered
                )
                BulkButton(
                    label = stringResource(R.string.bulk_uncover),
                    onClick = onUncover
                )
                BulkButton(
                    label = stringResource(R.string.bulk_delete),
                    onClick = onDelete,
                    bg = StagehandColors.ButtonDanger,
                    fg = Color.White
                )
            }
        }
    }
}

@Composable
private fun BulkButton(
    label: String,
    onClick: () -> Unit,
    bg: Color = StagehandColors.ButtonSecondary,
    fg: Color = StagehandColors.TextPrimary
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = bg,
            contentColor = fg
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}
