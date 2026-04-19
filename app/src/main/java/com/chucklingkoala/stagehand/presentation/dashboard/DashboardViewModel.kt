package com.chucklingkoala.stagehand.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chucklingkoala.stagehand.data.repository.CategoryRepository
import com.chucklingkoala.stagehand.data.repository.EpisodeRepository
import com.chucklingkoala.stagehand.data.repository.UrlRepository
import com.chucklingkoala.stagehand.domain.model.Url
import com.chucklingkoala.stagehand.domain.model.UrlStatus
import com.chucklingkoala.stagehand.util.Constants
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class DashboardViewModel(
    private val urlRepository: UrlRepository,
    private val categoryRepository: CategoryRepository,
    private val episodeRepository: EpisodeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        loadCategories()
        loadEpisodes()
        loadUrls(refresh = true)

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
            is DashboardEvent.SelectFilter -> {
                _state.update {
                    it.copy(
                        selectedFilter = event.filter,
                        currentOffset = 0,
                        selectedUrlIds = emptySet()
                    )
                }
                loadUrls(refresh = true)
            }

            is DashboardEvent.ChangeStatus ->
                patchUrl(event.urlId, patch = { it.copy(status = event.status) }) {
                    urlRepository.updateStatus(event.urlId, event.status?.value)
                }
            is DashboardEvent.ChangeCategory ->
                patchUrl(event.urlId, patch = { it.copy(categoryId = event.categoryId) }) {
                    urlRepository.updateCategory(event.urlId, event.categoryId)
                }
            is DashboardEvent.ChangeEpisode ->
                patchUrl(event.urlId, patch = { it.copy(episodeId = event.episodeId) }) {
                    urlRepository.updateEpisode(event.urlId, event.episodeId)
                }
            is DashboardEvent.ToggleCovered ->
                patchUrl(event.urlId, patch = { it.copy(covered = event.covered) }) {
                    urlRepository.updateCovered(event.urlId, event.covered)
                }

            is DashboardEvent.ToggleSelection -> {
                _state.update {
                    val next = it.selectedUrlIds.toMutableSet()
                    if (!next.add(event.urlId)) next.remove(event.urlId)
                    it.copy(selectedUrlIds = next)
                }
            }
            is DashboardEvent.ClearSelection -> {
                _state.update { it.copy(selectedUrlIds = emptySet()) }
            }
            is DashboardEvent.BulkCategorize -> runBulk {
                urlRepository.bulkCategorize(it, event.categoryId)
            }
            is DashboardEvent.BulkAssignEpisode -> runBulk {
                urlRepository.bulkAssignEpisode(it, event.episodeId)
            }
            is DashboardEvent.BulkFlag -> runBulk {
                urlRepository.bulkFlag(it, event.status.value)
            }
            is DashboardEvent.BulkMarkCovered -> runBulk {
                urlRepository.bulkMarkCovered(it, event.covered)
            }
            is DashboardEvent.BulkDelete -> runBulk { urlRepository.bulkDelete(it) }

            is DashboardEvent.SubmitUrl -> submitUrl(event.url, event.title)
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

    private fun loadUrls(refresh: Boolean) {
        viewModelScope.launch {
            if (refresh) {
                _state.update { it.copy(isRefreshing = true, currentOffset = 0, urls = emptyList()) }
            } else {
                _state.update { it.copy(isLoading = true) }
            }

            val currentState = _state.value
            val query = currentState.selectedFilter.toQuery()

            urlRepository.getUrls(
                limit = Constants.DEFAULT_PAGE_SIZE,
                offset = if (refresh) 0 else currentState.currentOffset,
                categoryId = query.categoryId,
                episodeId = query.episodeId,
                status = query.status,
                covered = query.covered,
                search = currentState.searchQuery.takeIf { it.isNotBlank() }
            )
                .onSuccess { (urls, total) ->
                    _state.update {
                        val offsetAdvance = if (refresh) Constants.DEFAULT_PAGE_SIZE else it.currentOffset + Constants.DEFAULT_PAGE_SIZE
                        it.copy(
                            urls = if (refresh) urls else it.urls + urls,
                            totalCount = total,
                            currentOffset = offsetAdvance,
                            hasMore = offsetAdvance < total,
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
            val query = currentState.selectedFilter.toQuery()

            urlRepository.getUrls(
                limit = Constants.DEFAULT_PAGE_SIZE,
                offset = currentState.currentOffset,
                categoryId = query.categoryId,
                episodeId = query.episodeId,
                status = query.status,
                covered = query.covered,
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

    // Optimistic: apply `patch` to the local list immediately, fire the API call,
    // replace with server response on success, revert to prior value on failure.
    private fun patchUrl(
        urlId: Int,
        patch: (Url) -> Url,
        apiCall: suspend () -> Result<Url>
    ) {
        val previous = _state.value.urls.firstOrNull { it.id == urlId } ?: return

        _state.update { state ->
            state.copy(urls = state.urls.map { if (it.id == urlId) patch(it) else it })
        }

        viewModelScope.launch {
            apiCall()
                .onSuccess { updated ->
                    _state.update { state ->
                        state.copy(urls = state.urls.map { if (it.id == urlId) updated else it })
                    }
                }
                .onFailure { error ->
                    _state.update { state ->
                        state.copy(
                            urls = state.urls.map { if (it.id == urlId) previous else it },
                            error = "Failed to update: ${error.message}"
                        )
                    }
                }
        }
    }

    private fun runBulk(action: suspend (List<Int>) -> Result<Int>) {
        val ids = _state.value.selectedUrlIds.toList()
        if (ids.isEmpty()) return

        viewModelScope.launch {
            action(ids)
                .onSuccess {
                    _state.update { it.copy(selectedUrlIds = emptySet()) }
                    loadUrls(refresh = true)
                }
                .onFailure { error ->
                    _state.update { it.copy(error = "Bulk action failed: ${error.message}") }
                }
        }
    }

    private fun submitUrl(url: String, title: String?) {
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }
            urlRepository.submitUrl(url, title)
                .onSuccess {
                    _state.update { it.copy(isSubmitting = false) }
                    loadUrls(refresh = true)
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isSubmitting = false,
                            error = "Failed to submit URL: ${error.message}"
                        )
                    }
                }
        }
    }
}
