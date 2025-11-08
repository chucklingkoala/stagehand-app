package com.chucklingkoala.stagehand.presentation.categories

import com.chucklingkoala.stagehand.domain.model.Category

data class CategoriesState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val error: String? = null,
    val showCreateDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val editingCategory: Category? = null,
    val deletingCategory: Category? = null
)

sealed class CategoriesEvent {
    object LoadCategories : CategoriesEvent()
    object ShowCreateDialog : CategoriesEvent()
    object HideCreateDialog : CategoriesEvent()
    data class ShowEditDialog(val category: Category) : CategoriesEvent()
    object HideEditDialog : CategoriesEvent()
    data class ShowDeleteDialog(val category: Category) : CategoriesEvent()
    object HideDeleteDialog : CategoriesEvent()
    data class CreateCategory(val name: String, val color: String) : CategoriesEvent()
    data class UpdateCategory(val id: Int, val name: String, val color: String) : CategoriesEvent()
    data class DeleteCategory(val id: Int) : CategoriesEvent()
}
