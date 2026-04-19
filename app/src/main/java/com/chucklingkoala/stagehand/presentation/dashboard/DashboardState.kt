package com.chucklingkoala.stagehand.presentation.dashboard

import com.chucklingkoala.stagehand.domain.model.Category
import com.chucklingkoala.stagehand.domain.model.Episode
import com.chucklingkoala.stagehand.domain.model.Url
import com.chucklingkoala.stagehand.domain.model.UrlStatus

data class DashboardState(
    val urls: List<Url> = emptyList(),
    val categories: List<Category> = emptyList(),
    val episodes: List<Episode> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedFilter: DashboardFilter = DashboardFilter.All,
    val selectedUrlIds: Set<Int> = emptySet(),
    val currentOffset: Int = 0,
    val hasMore: Boolean = true,
    val totalCount: Int = 0
) {
    val isSelectionMode: Boolean get() = selectedUrlIds.isNotEmpty()
}

sealed class DashboardEvent {
    object LoadUrls : DashboardEvent()
    object LoadMore : DashboardEvent()
    object Refresh : DashboardEvent()
    data class Search(val query: String) : DashboardEvent()
    data class SelectFilter(val filter: DashboardFilter) : DashboardEvent()

    // Per-card mutations (optimistic)
    data class ChangeStatus(val urlId: Int, val status: UrlStatus?) : DashboardEvent()
    data class ChangeCategory(val urlId: Int, val categoryId: Int?) : DashboardEvent()
    data class ChangeEpisode(val urlId: Int, val episodeId: Int?) : DashboardEvent()
    data class ToggleCovered(val urlId: Int, val covered: Boolean) : DashboardEvent()

    // Multi-select / bulk
    data class ToggleSelection(val urlId: Int) : DashboardEvent()
    object ClearSelection : DashboardEvent()
    data class BulkCategorize(val categoryId: Int?) : DashboardEvent()
    data class BulkAssignEpisode(val episodeId: Int?) : DashboardEvent()
    data class BulkFlag(val status: UrlStatus) : DashboardEvent()
    data class BulkMarkCovered(val covered: Boolean) : DashboardEvent()
    object BulkDelete : DashboardEvent()

    // Quick submit
    data class SubmitUrl(val url: String, val title: String?) : DashboardEvent()
}
