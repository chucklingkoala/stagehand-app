package com.chucklingkoala.stagehand.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chucklingkoala.stagehand.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CategoriesState())
    val state: StateFlow<CategoriesState> = _state.asStateFlow()

    init {
        loadCategories()
    }

    fun onEvent(event: CategoriesEvent) {
        when (event) {
            is CategoriesEvent.LoadCategories -> loadCategories()
            is CategoriesEvent.ShowCreateDialog -> {
                _state.update { it.copy(showCreateDialog = true) }
            }
            is CategoriesEvent.HideCreateDialog -> {
                _state.update { it.copy(showCreateDialog = false) }
            }
            is CategoriesEvent.ShowEditDialog -> {
                _state.update {
                    it.copy(
                        showEditDialog = true,
                        editingCategory = event.category
                    )
                }
            }
            is CategoriesEvent.HideEditDialog -> {
                _state.update {
                    it.copy(
                        showEditDialog = false,
                        editingCategory = null
                    )
                }
            }
            is CategoriesEvent.ShowDeleteDialog -> {
                _state.update {
                    it.copy(
                        showDeleteDialog = true,
                        deletingCategory = event.category
                    )
                }
            }
            is CategoriesEvent.HideDeleteDialog -> {
                _state.update {
                    it.copy(
                        showDeleteDialog = false,
                        deletingCategory = null
                    )
                }
            }
            is CategoriesEvent.CreateCategory -> {
                createCategory(event.name, event.color)
            }
            is CategoriesEvent.UpdateCategory -> {
                updateCategory(event.id, event.name, event.color)
            }
            is CategoriesEvent.DeleteCategory -> {
                deleteCategory(event.id)
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            categoryRepository.getCategories()
                .onSuccess { categories ->
                    _state.update {
                        it.copy(
                            categories = categories.sortedBy { cat -> cat.name },
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load categories"
                        )
                    }
                }
        }
    }

    private fun createCategory(name: String, color: String) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            categoryRepository.createCategory(name, color)
                .onSuccess {
                    _state.update { it.copy(isSaving = false, showCreateDialog = false) }
                    loadCategories()
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            error = error.message ?: "Failed to create category"
                        )
                    }
                }
        }
    }

    private fun updateCategory(id: Int, name: String, color: String) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            categoryRepository.updateCategory(id, name, color)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            showEditDialog = false,
                            editingCategory = null
                        )
                    }
                    loadCategories()
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            error = error.message ?: "Failed to update category"
                        )
                    }
                }
        }
    }

    private fun deleteCategory(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }

            categoryRepository.deleteCategory(id)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isDeleting = false,
                            showDeleteDialog = false,
                            deletingCategory = null
                        )
                    }
                    loadCategories()
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isDeleting = false,
                            error = error.message ?: "Failed to delete category"
                        )
                    }
                }
        }
    }
}
