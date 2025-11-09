package com.chucklingkoala.stagehand.presentation.urldetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chucklingkoala.stagehand.data.repository.CategoryRepository
import com.chucklingkoala.stagehand.data.repository.UrlRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UrlDetailViewModel(
    private val urlId: Int,
    private val urlRepository: UrlRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UrlDetailState())
    val state: StateFlow<UrlDetailState> = _state.asStateFlow()

    init {
        loadUrl()
        loadCategories()
    }

    fun onEvent(event: UrlDetailEvent) {
        when (event) {
            is UrlDetailEvent.LoadUrl -> loadUrl()
            is UrlDetailEvent.LoadLinkPreview -> loadLinkPreview()
            is UrlDetailEvent.SelectCategory -> {
                _state.update { it.copy(selectedCategoryId = event.categoryId) }
            }
            is UrlDetailEvent.SelectStatus -> {
                _state.update { it.copy(selectedStatus = event.status) }
            }
            is UrlDetailEvent.SaveChanges -> saveChanges()
            is UrlDetailEvent.OpenInBrowser,
            is UrlDetailEvent.CopyUrl,
            is UrlDetailEvent.ShareUrl,
            is UrlDetailEvent.ViewOnDiscord -> {
                // These are handled by the UI
            }
        }
    }

    private fun loadUrl() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            urlRepository.getUrl(urlId)
                .onSuccess { url ->
                    _state.update {
                        it.copy(
                            url = url,
                            selectedCategoryId = url.categoryId,
                            selectedStatus = url.status,
                            isLoading = false,
                            error = null
                        )
                    }
                    // Auto-load link preview
                    loadLinkPreview()
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load URL"
                        )
                    }
                }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getCategories()
                .onSuccess { categories ->
                    _state.update { it.copy(categories = categories) }
                }
                .onFailure { error ->
                    // Silently fail, not critical
                    println("Failed to load categories: ${error.message}")
                }
        }
    }

    private fun loadLinkPreview() {
        val url = _state.value.url?.url ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoadingPreview = true) }

            urlRepository.getLinkPreview(url)
                .onSuccess { preview ->
                    _state.update {
                        it.copy(
                            linkPreview = preview,
                            isLoadingPreview = false
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoadingPreview = false,
                            linkPreview = null
                        )
                    }
                    println("Failed to load link preview: ${error.message}")
                }
        }
    }

    private fun saveChanges() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            val currentState = _state.value

            urlRepository.updateUrl(
                id = urlId,
                categoryId = currentState.selectedCategoryId,
                status = currentState.selectedStatus?.value
            )
                .onSuccess { updatedUrl ->
                    _state.update {
                        it.copy(
                            url = updatedUrl,
                            isSaving = false,
                            savedSuccessfully = true,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            savedSuccessfully = false,
                            error = error.message ?: "Failed to save changes"
                        )
                    }
                }
        }
    }
}
