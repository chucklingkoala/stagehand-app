package com.chucklingkoala.stagehand.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chucklingkoala.stagehand.R
import com.chucklingkoala.stagehand.domain.model.Category
import com.chucklingkoala.stagehand.presentation.theme.StagehandColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPickerSheet(
    categories: List<Category>,
    currentCategoryId: Int?,
    onPick: (Int?) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var query by remember { mutableStateOf("") }
    val filtered = remember(categories, query) {
        if (query.isBlank()) categories
        else categories.filter { it.name.contains(query, ignoreCase = true) }
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
                text = stringResource(R.string.pick_category_title),
                style = MaterialTheme.typography.titleMedium,
                color = StagehandColors.TextPrimary
            )

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        stringResource(R.string.search_hint),
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
                        label = stringResource(R.string.uncategorized_label),
                        selected = currentCategoryId == null,
                        onClick = { onPick(null) }
                    )
                }
                items(filtered, key = { it.id }) { category ->
                    PickerRow(
                        label = category.name,
                        selected = category.id == currentCategoryId,
                        swatchColor = parseColor(category.color),
                        onClick = { onPick(category.id) }
                    )
                }
            }
        }
    }
}

@Composable
internal fun PickerRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    swatchColor: Color? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .background(
                if (selected) StagehandColors.AccentColor.copy(alpha = 0.25f)
                else Color.Transparent
            )
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (swatchColor != null) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(swatchColor)
            )
            Spacer(Modifier.width(10.dp))
        }
        Text(
            text = label,
            color = StagehandColors.TextPrimary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        if (selected) {
            Text(
                text = "\u2713",
                color = StagehandColors.AccentColor,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

internal fun parseColor(hex: String): Color = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (e: Exception) {
    Color.Gray
}
