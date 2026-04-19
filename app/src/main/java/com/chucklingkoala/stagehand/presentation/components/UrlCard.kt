package com.chucklingkoala.stagehand.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.chucklingkoala.stagehand.R
import com.chucklingkoala.stagehand.domain.model.Category
import com.chucklingkoala.stagehand.domain.model.Url
import com.chucklingkoala.stagehand.domain.model.UrlStatus
import com.chucklingkoala.stagehand.presentation.theme.StagehandColors
import com.chucklingkoala.stagehand.util.DateFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UrlCard(
    url: Url,
    categories: List<Category>,
    selectionMode: Boolean,
    isSelected: Boolean,
    onCardTap: () -> Unit,
    onLongPress: () -> Unit,
    onStatusChange: (UrlStatus?) -> Unit,
    onCategoryClick: () -> Unit,
    onEpisodeClick: () -> Unit,
    onCoveredToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = categories.firstOrNull { it.id == url.categoryId }?.color ?: "#808080"
    val episodeLabel = url.episodeNumber?.let { stringResource(R.string.episode_label, it) }
        ?: stringResource(R.string.no_episode_label)
    val categoryLabel = url.categoryName ?: stringResource(R.string.uncategorized_label)

    val borderColor = when {
        isSelected -> StagehandColors.AccentColor
        url.isDuplicate -> StagehandColors.DuplicateOrange
        else -> StagehandColors.BorderColor
    }
    val borderWidth = when {
        isSelected -> 3.dp
        url.isDuplicate -> 4.dp
        else -> 1.dp
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .alpha(if (url.covered) 0.5f else 1f)
            .combinedClickable(
                onClick = onCardTap,
                onLongClick = onLongPress
            ),
        colors = CardDefaults.cardColors(
            containerColor = StagehandColors.CardBackground
        ),
        border = BorderStroke(width = borderWidth, color = borderColor),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = url.title ?: stringResource(R.string.no_title),
                style = MaterialTheme.typography.titleMedium,
                color = StagehandColors.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = url.url,
                style = MaterialTheme.typography.bodySmall,
                color = StagehandColors.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.posted_by, url.postedBy),
                    style = MaterialTheme.typography.bodySmall,
                    color = StagehandColors.TextSecondary
                )
                Spacer(Modifier.width(8.dp))
                if (url.categoryName != null && url.categoryId != null) {
                    CategoryChip(
                        name = url.categoryName,
                        color = categoryColor
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = DateFormatter.formatTimeAgo(url.postedAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = StagehandColors.TextSecondary
                )
            }

            if (url.isDuplicate) {
                Spacer(Modifier.height(8.dp))
                DuplicateBadge()
            }

            Spacer(Modifier.height(12.dp))

            StatusPills(
                current = url.status,
                onChange = onStatusChange
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InlinePickerButton(
                    label = categoryLabel,
                    onClick = onCategoryClick,
                    modifier = Modifier.weight(1f)
                )
                InlinePickerButton(
                    label = episodeLabel,
                    onClick = onEpisodeClick,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onCoveredToggle(!url.covered) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (url.covered) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (url.covered) StagehandColors.AccentColor else StagehandColors.TextSecondary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.covered_label),
                    color = if (url.covered) StagehandColors.TextPrimary else StagehandColors.TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.weight(1f))
                if (selectionMode) {
                    Text(
                        text = if (isSelected) "\u2713" else "",
                        color = StagehandColors.AccentColor,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun InlinePickerButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        border = BorderStroke(1.dp, StagehandColors.BorderColor),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = StagehandColors.TextPrimary,
            containerColor = StagehandColors.InputBackground
        ),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = StagehandColors.TextPrimary
        )
    }
}
