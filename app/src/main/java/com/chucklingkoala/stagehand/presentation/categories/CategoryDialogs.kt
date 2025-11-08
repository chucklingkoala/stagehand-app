package com.chucklingkoala.stagehand.presentation.categories

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chucklingkoala.stagehand.R
import com.chucklingkoala.stagehand.domain.model.Category
import com.chucklingkoala.stagehand.presentation.components.CategoryChip
import com.chucklingkoala.stagehand.presentation.theme.StagehandColors
import com.chucklingkoala.stagehand.util.Constants

@Composable
fun CreateCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, color: String) -> Unit,
    isSaving: Boolean
) {
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(Constants.PRESET_COLORS[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.create_category),
                color = StagehandColors.TextPrimary
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Name input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = {
                        Text(
                            text = stringResource(R.string.category_name_hint),
                            color = StagehandColors.TextSecondary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = StagehandColors.TextPrimary,
                        unfocusedTextColor = StagehandColors.TextPrimary,
                        focusedBorderColor = StagehandColors.AccentColor,
                        unfocusedBorderColor = StagehandColors.BorderColor
                    )
                )

                // Color picker label
                Text(
                    text = stringResource(R.string.category_color_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = StagehandColors.TextSecondary
                )

                // Color grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(Constants.PRESET_COLORS) { colorHex ->
                        val color = try {
                            Color(android.graphics.Color.parseColor(colorHex))
                        } catch (e: Exception) {
                            Color.Gray
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable { selectedColor = colorHex },
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(if (selectedColor == colorHex) 40.dp else 32.dp)) {
                                drawCircle(color = color)
                            }
                            if (selectedColor == colorHex) {
                                Canvas(modifier = Modifier.size(36.dp)) {
                                    drawCircle(
                                        color = Color.White,
                                        radius = size.minDimension / 4
                                    )
                                }
                            }
                        }
                    }
                }

                // Preview
                Text(
                    text = stringResource(R.string.category_preview_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = StagehandColors.TextSecondary
                )

                CategoryChip(
                    name = name.ifBlank { "Category Name" },
                    color = selectedColor
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, selectedColor) },
                enabled = name.isNotBlank() && !isSaving,
                colors = ButtonDefaults.buttonColors(
                    containerColor = StagehandColors.ButtonSuccess
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = StagehandColors.TextPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.create))
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSaving
            ) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = StagehandColors.TextSecondary
                )
            }
        },
        containerColor = StagehandColors.CardBackground,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun EditCategoryDialog(
    category: Category,
    onDismiss: () -> Unit,
    onConfirm: (id: Int, name: String, color: String) -> Unit,
    onDelete: (id: Int) -> Unit,
    isSaving: Boolean
) {
    var name by remember { mutableStateOf(category.name) }
    var selectedColor by remember { mutableStateOf(category.color) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.edit_category),
                color = StagehandColors.TextPrimary
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Name input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = {
                        Text(
                            text = stringResource(R.string.category_name_hint),
                            color = StagehandColors.TextSecondary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = StagehandColors.TextPrimary,
                        unfocusedTextColor = StagehandColors.TextPrimary,
                        focusedBorderColor = StagehandColors.AccentColor,
                        unfocusedBorderColor = StagehandColors.BorderColor
                    )
                )

                // Color picker label
                Text(
                    text = stringResource(R.string.category_color_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = StagehandColors.TextSecondary
                )

                // Color grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(Constants.PRESET_COLORS) { colorHex ->
                        val color = try {
                            Color(android.graphics.Color.parseColor(colorHex))
                        } catch (e: Exception) {
                            Color.Gray
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable { selectedColor = colorHex },
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(if (selectedColor == colorHex) 40.dp else 32.dp)) {
                                drawCircle(color = color)
                            }
                            if (selectedColor == colorHex) {
                                Canvas(modifier = Modifier.size(36.dp)) {
                                    drawCircle(
                                        color = Color.White,
                                        radius = size.minDimension / 4
                                    )
                                }
                            }
                        }
                    }
                }

                // Preview
                Text(
                    text = stringResource(R.string.category_preview_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = StagehandColors.TextSecondary
                )

                CategoryChip(
                    name = name.ifBlank { "Category Name" },
                    color = selectedColor
                )

                Divider(color = StagehandColors.BorderColor)

                // Delete button
                TextButton(
                    onClick = { onDelete(category.id) },
                    enabled = !isSaving,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = StagehandColors.DumpRed
                    )
                ) {
                    Text(stringResource(R.string.delete_category))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(category.id, name, selectedColor) },
                enabled = name.isNotBlank() && !isSaving,
                colors = ButtonDefaults.buttonColors(
                    containerColor = StagehandColors.ButtonSuccess
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = StagehandColors.TextPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.save))
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSaving
            ) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = StagehandColors.TextSecondary
                )
            }
        },
        containerColor = StagehandColors.CardBackground,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun DeleteCategoryDialog(
    category: Category,
    onDismiss: () -> Unit,
    onConfirm: (id: Int) -> Unit,
    isDeleting: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.delete_confirmation_title),
                color = StagehandColors.TextPrimary
            )
        },
        text = {
            Text(
                text = stringResource(R.string.delete_confirmation_message),
                color = StagehandColors.TextSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(category.id) },
                enabled = !isDeleting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = StagehandColors.DumpRed
                )
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = StagehandColors.TextPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.delete_confirm))
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isDeleting
            ) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = StagehandColors.TextSecondary
                )
            }
        },
        containerColor = StagehandColors.CardBackground,
        shape = RoundedCornerShape(16.dp)
    )
}
