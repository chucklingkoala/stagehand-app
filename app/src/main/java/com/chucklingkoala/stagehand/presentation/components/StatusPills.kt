package com.chucklingkoala.stagehand.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chucklingkoala.stagehand.R
import com.chucklingkoala.stagehand.domain.model.UrlStatus
import com.chucklingkoala.stagehand.presentation.theme.StagehandColors

@Composable
fun StatusPills(
    current: UrlStatus?,
    onChange: (UrlStatus?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        StatusPill(
            label = stringResource(R.string.status_no_status),
            selected = current == null,
            activeBg = StagehandColors.ButtonSecondary,
            activeFg = Color.White,
            onClick = {
                if (current != null) onChange(null)
            }
        )
        StatusPill(
            label = stringResource(R.string.status_on_show),
            selected = current == UrlStatus.ON_SHOW,
            activeBg = StagehandColors.OnShowGreen,
            activeFg = Color.Black,
            onClick = {
                onChange(if (current == UrlStatus.ON_SHOW) null else UrlStatus.ON_SHOW)
            }
        )
        StatusPill(
            label = stringResource(R.string.status_dumped),
            selected = current == UrlStatus.DUMP,
            activeBg = StagehandColors.DumpRed,
            activeFg = Color.White,
            onClick = {
                onChange(if (current == UrlStatus.DUMP) null else UrlStatus.DUMP)
            }
        )
    }
}

@Composable
private fun StatusPill(
    label: String,
    selected: Boolean,
    activeBg: Color,
    activeFg: Color,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = StagehandColors.CardBackground,
            labelColor = StagehandColors.TextSecondary,
            selectedContainerColor = activeBg,
            selectedLabelColor = activeFg
        )
    )
}

@Composable
fun DuplicateBadge(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = StagehandColors.DuplicateOrange,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = stringResource(R.string.duplicate_badge),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}
