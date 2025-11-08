package com.chucklingkoala.stagehand.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.chucklingkoala.stagehand.R
import com.chucklingkoala.stagehand.domain.model.Url
import com.chucklingkoala.stagehand.domain.model.UrlStatus
import com.chucklingkoala.stagehand.presentation.theme.StagehandColors
import com.chucklingkoala.stagehand.util.DateFormatter

@Composable
fun UrlCard(
    url: Url,
    onCardClick: (Int) -> Unit,
    onStatusToggle: (Int, UrlStatus?) -> Unit,
    onCategorizeClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onCardClick(url.id) },
        colors = CardDefaults.cardColors(
            containerColor = StagehandColors.CardBackground
        ),
        border = BorderStroke(
            width = if (url.isDuplicate) 4.dp else 1.dp,
            color = if (url.isDuplicate) {
                StagehandColors.DuplicateOrange
            } else {
                StagehandColors.BorderColor
            }
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Title
            Text(
                text = url.title ?: stringResource(R.string.no_title),
                style = MaterialTheme.typography.titleMedium,
                color = StagehandColors.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // URL
            Text(
                text = url.url,
                style = MaterialTheme.typography.bodySmall,
                color = StagehandColors.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Metadata Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Posted by
                Text(
                    text = stringResource(R.string.posted_by, url.postedBy),
                    style = MaterialTheme.typography.bodySmall,
                    color = StagehandColors.TextSecondary
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Category chip
                url.categoryName?.let { categoryName ->
                    url.categoryId?.let { _ ->
                        CategoryChip(
                            name = categoryName,
                            color = "#808080" // Default gray, actual color would come from category list
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Time ago
                Text(
                    text = DateFormatter.formatTimeAgo(url.postedAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = StagehandColors.TextSecondary
                )
            }

            // Status & Duplicate Badges
            if (url.status != null || url.isDuplicate) {
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    url.status?.let { status ->
                        StatusBadge(status = status)
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    if (url.isDuplicate) {
                        DuplicateBadge()
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onStatusToggle(url.id, url.status) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (url.status == UrlStatus.ON_SHOW) {
                            StagehandColors.OnShowGreen
                        } else {
                            StagehandColors.ButtonSecondary
                        }
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (url.status == UrlStatus.ON_SHOW) {
                            stringResource(R.string.on_show_active)
                        } else {
                            stringResource(R.string.on_show)
                        },
                        color = if (url.status == UrlStatus.ON_SHOW) Color.Black else Color.White
                    )
                }

                OutlinedButton(
                    onClick = { onCategorizeClick(url.id) },
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, StagehandColors.AccentColor)
                ) {
                    Text(
                        text = stringResource(R.string.categorize),
                        color = StagehandColors.AccentColor
                    )
                }
            }
        }
    }
}
