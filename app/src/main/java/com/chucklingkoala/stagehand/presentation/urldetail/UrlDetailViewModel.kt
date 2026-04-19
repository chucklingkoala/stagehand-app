package com.chucklingkoala.stagehand.presentation.urldetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chucklingkoala.stagehand.data.repository.CategoryRepository
import com.chucklingkoala.stagehand.data.repository.EpisodeRepository
import com.chucklingkoala.stagehand.data.repository.UrlRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UrlDetailViewModel(
    private val urlId: Int,
    private val urlRepository: UrlRepository,
    private val categoryRepository: CategoryRepository,
    private val episodeRepository: EpisodeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UrlDetailState())
    val state: StateFlow<UrlDetailState> = _state.asStateFlow()

    init {
        loadUrl()
        loadCategories()
        loadEpisodes()
    }

    fun onEvent(event: UrlDetailEvent) {
        when (event) {
            is UrlDetailEvent.LoadUrl -> loadUrl()
            is UrlDetailEvent.LoadLinkPreview -> loadLinkPreview()
            is UrlDetailEvent.SelectCategory -> {
                _state.update { it.copy(selectedCategoryId = event.categoryId) }
            }
            is UrlDetailEvent.SelectEpisode -> {
                _state.update { it.copy(selectedEpisodeId = event.episodeId) }
            }
            is UrlDetailEvent.SelectStatus -> {
                _state.update { it.copy(selectedStatus = event.status) }
            }
            is UrlDetailEvent.SetCovered -> {
                _state.update { it.copy(selectedCovered = event.covered) }
            }
            is UrlDetailEvent.SaveChanges -> saveChanges()
            is UrlDetailEvent.OpenInBrowser,
            is UrlDetailEvent.CopyUrl,
            is UrlDetailEvent.ShareUrl,
            is UrlDetailEvent.ViewOnDiscord -> Unit
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
                            selectedEpisodeId = url.episodeId,
                            selectedStatus = url.status,
                            selectedCovered = url.covered,
                            isLoading = false,
                            error = null
                        )
                    }
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
                    println("Failed to load categories: ${error.message}")
                }
        }
    }

    private fun loadEpisodes() {
        viewModelScope.launch {
            episodeRepository.getEpisodes()
                .onSuccess { episodes ->
                    _state.update { it.copy(episodes = episodes) }
                }
                .onFailure { error ->
                    println("Failed to load episodes: ${error.message}")
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

    // The detail screen aggregates several user choices and commits them all on Save.
    // We fire the specific per-field repo methods only for fields that actually changed,
    // so we don't ship a blanket PUT that might clobber server-side state.
    private fun saveChanges() {
        viewModelScope.launch {
            val initial = _state.value.url ?: return@launch
            val current = _state.value
            _state.update { it.copy(isSaving = true) }

            var lastError: Throwable? = null
            var latest = initial

            if (current.selectedCategoryId != initial.categoryId) {
                urlRepository.updateCategory(urlId, current.selectedCategoryId)
                    .onSuccess { latest = it }
                    .onFailure { lastError = it }
            }
            if (lastError == null && current.selectedEpisodeId != initial.episodeId) {
                urlRepository.updateEpisode(urlId, current.selectedEpisodeId)
                    .onSuccess { latest = it }
                    .onFailure { lastError = it }
            }
            if (lastError == null && current.selectedStatus != initial.status) {
                urlRepository.updateStatus(urlId, current.selectedStatus?.value)
                    .onSuccess { latest = it }
                    .onFailure { lastError = it }
            }
            if (lastError == null && current.selectedCovered != initial.covered) {
                urlRepository.updateCovered(urlId, current.selectedCovered)
                    .onSuccess { latest = it }
                    .onFailure { lastError = it }
            }

            if (lastError == null) {
                _state.update {
                    it.copy(
                        url = latest,
                        isSaving = false,
                        savedSuccessfully = true,
                        error = null
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isSaving = false,
                        savedSuccessfully = false,
                        error = lastError?.message ?: "Failed to save changes"
                    )
                }
            }
        }
    }
}
