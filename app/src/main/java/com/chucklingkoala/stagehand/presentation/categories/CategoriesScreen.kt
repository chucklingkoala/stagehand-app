package com.chucklingkoala.stagehand.presentation.categories

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chucklingkoala.stagehand.R
import com.chucklingkoala.stagehand.presentation.theme.StagehandColors
import org.koin.androidx.compose.get

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    viewModel: CategoriesViewModel = get(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.categories_title),
                        color = StagehandColors.TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = StagehandColors.TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(CategoriesEvent.ShowCreateDialog) }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Category",
                            tint = StagehandColors.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = StagehandColors.BackgroundSecondary,
                    titleContentColor = StagehandColors.TextPrimary
                )
            )
        },
        containerColor = StagehandColors.BackgroundPrimary,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(CategoriesEvent.ShowCreateDialog) },
                containerColor = StagehandColors.ButtonPrimary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Category",
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading && state.categories.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = StagehandColors.AccentColor)
                }
            } else if (state.categories.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No categories yet",
                        color = StagehandColors.TextSecondary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.categories, key = { it.id }) { category ->
                        CategoryCard(
                            category = category,
                            onEditClick = { viewModel.onEvent(CategoriesEvent.ShowEditDialog(category)) }
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    if (state.showCreateDialog) {
        CreateCategoryDialog(
            onDismiss = { viewModel.onEvent(CategoriesEvent.HideCreateDialog) },
            onConfirm = { name, color ->
                viewModel.onEvent(CategoriesEvent.CreateCategory(name, color))
            },
            isSaving = state.isSaving
        )
    }

    state.editingCategory?.let { category ->
        if (state.showEditDialog) {
            EditCategoryDialog(
                category = category,
                onDismiss = { viewModel.onEvent(CategoriesEvent.HideEditDialog) },
                onConfirm = { id, name, color ->
                    viewModel.onEvent(CategoriesEvent.UpdateCategory(id, name, color))
                },
                onDelete = { id ->
                    viewModel.onEvent(CategoriesEvent.HideEditDialog)
                    viewModel.onEvent(CategoriesEvent.ShowDeleteDialog(category))
                },
                isSaving = state.isSaving
            )
        }
    }

    state.deletingCategory?.let { category ->
        if (state.showDeleteDialog) {
            DeleteCategoryDialog(
                category = category,
                onDismiss = { viewModel.onEvent(CategoriesEvent.HideDeleteDialog) },
                onConfirm = { id ->
                    viewModel.onEvent(CategoriesEvent.DeleteCategory(id))
                },
                isDeleting = state.isDeleting
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryCard(
    category: com.chucklingkoala.stagehand.domain.model.Category,
    onEditClick: () -> Unit
) {
    val categoryColor = try {
        Color(android.graphics.Color.parseColor(category.color))
    } catch (e: Exception) {
        Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = StagehandColors.CardBackground
        ),
        shape = RoundedCornerShape(8.dp),
        onClick = onEditClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Color indicator
                Canvas(modifier = Modifier.size(24.dp)) {
                    drawCircle(color = categoryColor)
                }

                Column {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = StagehandColors.TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(R.string.category_count, category.urlCount),
                        style = MaterialTheme.typography.bodySmall,
                        color = StagehandColors.TextSecondary
                    )
                }
            }

            TextButton(onClick = onEditClick) {
                Text(
                    text = stringResource(R.string.edit),
                    color = StagehandColors.AccentColor
                )
            }
        }
    }
}
