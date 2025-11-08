package com.chucklingkoala.stagehand.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chucklingkoala.stagehand.data.repository.CategoryRepository
import com.chucklingkoala.stagehand.data.repository.UrlRepository
import com.chucklingkoala.stagehand.domain.model.UrlStatus
import com.chucklingkoala.stagehand.util.Constants
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class DashboardViewModel(
    private val urlRepository: UrlRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        // Load initial data
        loadCategories()
        loadUrls(refresh = true)

        // Set up search with debounce
        viewModelScope.launch {
            _searchQuery
                .debounce(Constants.SEARCH_DEBOUNCE_MS)
                .distinctUntilChanged()
                .collect { query ->
                    _state.update { it.copy(searchQuery = query, currentOffset = 0) }
                    loadUrls(refresh = true)
                }
        }
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.LoadUrls -> loadUrls(refresh = false)
            is DashboardEvent.LoadMore -> loadMore()
            is DashboardEvent.Refresh -> loadUrls(refresh = true)
            is DashboardEvent.Search -> {
                _searchQuery.value = event.query
            }
            is DashboardEvent.FilterByCategory -> {
                _state.update {
                    it.copy(
                        selectedCategoryId = event.categoryId,
                        selectedStatus = null,
                        showUncategorized = false,
                        currentOffset = 0
                    )
                }
                loadUrls(refresh = true)
            }
            is DashboardEvent.FilterByStatus -> {
                _state.update {
                    it.copy(
                        selectedStatus = event.status,
                        selectedCategoryId = null,
                        showUncategorized = false,
                        currentOffset = 0
                    )
                }
                loadUrls(refresh = true)
            }
            is DashboardEvent.FilterUncategorized -> {
                _state.update {
                    it.copy(
                        showUncategorized = !it.showUncategorized,
                        selectedCategoryId = null,
                        selectedStatus = null,
                        currentOffset = 0
                    )
                }
                loadUrls(refresh = true)
            }
            is DashboardEvent.ClearFilters -> {
                _state.update {
                    it.copy(
                        selectedCategoryId = null,
                        selectedStatus = null,
                        showUncategorized = false,
                        searchQuery = "",
                        currentOffset = 0
                    )
                }
                _searchQuery.value = ""
                loadUrls(refresh = true)
            }
            is DashboardEvent.ToggleUrlStatus -> {
                toggleUrlStatus(event.urlId, event.currentStatus)
            }
            is DashboardEvent.NavigateToUrl -> {
                // Navigation handled by UI
            }
            is DashboardEvent.NavigateToCategories -> {
                // Navigation handled by UI
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
                    // Silently fail for categories as it's not critical for initial load
                    println("Failed to load categories: ${error.message}")
                }
        }
    }

    private fun loadUrls(refresh: Boolean) {
        viewModelScope.launch {
            if (refresh) {
                _state.update { it.copy(isRefreshing = true, currentOffset = 0, urls = emptyList()) }
            } else {
                _state.update { it.copy(isLoading = true) }
            }

            val currentState = _state.value

            urlRepository.getUrls(
                limit = Constants.DEFAULT_PAGE_SIZE,
                offset = if (refresh) 0 else currentState.currentOffset,
                categoryId = if (currentState.showUncategorized) 0 else currentState.selectedCategoryId,
                status = currentState.selectedStatus?.value,
                search = currentState.searchQuery.takeIf { it.isNotBlank() }
            )
                .onSuccess { (urls, total) ->
                    _state.update {
                        it.copy(
                            urls = if (refresh) urls else it.urls + urls,
                            totalCount = total,
                            currentOffset = if (refresh) Constants.DEFAULT_PAGE_SIZE else it.currentOffset + Constants.DEFAULT_PAGE_SIZE,
                            hasMore = (if (refresh) Constants.DEFAULT_PAGE_SIZE else it.currentOffset + Constants.DEFAULT_PAGE_SIZE) < total,
                            isLoading = false,
                            isRefreshing = false,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = error.message ?: "Failed to load URLs"
                        )
                    }
                }
        }
    }

    private fun loadMore() {
        if (_state.value.isLoadingMore || !_state.value.hasMore) return

        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true) }

            val currentState = _state.value

            urlRepository.getUrls(
                limit = Constants.DEFAULT_PAGE_SIZE,
                offset = currentState.currentOffset,
                categoryId = if (currentState.showUncategorized) 0 else currentState.selectedCategoryId,
                status = currentState.selectedStatus?.value,
                search = currentState.searchQuery.takeIf { it.isNotBlank() }
            )
                .onSuccess { (urls, total) ->
                    _state.update {
                        it.copy(
                            urls = it.urls + urls,
                            currentOffset = it.currentOffset + Constants.DEFAULT_PAGE_SIZE,
                            hasMore = it.currentOffset + Constants.DEFAULT_PAGE_SIZE < total,
                            isLoadingMore = false,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoadingMore = false,
                            error = error.message ?: "Failed to load more URLs"
                        )
                    }
                }
        }
    }

    private fun toggleUrlStatus(urlId: Int, currentStatus: UrlStatus?) {
        viewModelScope.launch {
            val newStatus = when (currentStatus) {
                UrlStatus.ON_SHOW -> null // Clear status
                else -> UrlStatus.ON_SHOW // Set to On Show
            }

            urlRepository.updateUrl(
                id = urlId,
                status = newStatus?.value
            )
                .onSuccess { updatedUrl ->
                    // Update the URL in the list
                    _state.update { state ->
                        state.copy(
                            urls = state.urls.map { url ->
                                if (url.id == urlId) updatedUrl else url
                            }
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(error = "Failed to update status: ${error.message}")
                    }
                }
        }
    }
}
