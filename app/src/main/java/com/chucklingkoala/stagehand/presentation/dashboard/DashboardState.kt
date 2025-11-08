package com.chucklingkoala.stagehand.presentation.dashboard

import com.chucklingkoala.stagehand.domain.model.Category
import com.chucklingkoala.stagehand.domain.model.Url
import com.chucklingkoala.stagehand.domain.model.UrlStatus

data class DashboardState(
    val urls: List<Url> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedCategoryId: Int? = null,
    val selectedStatus: UrlStatus? = null,
    val showUncategorized: Boolean = false,
    val currentOffset: Int = 0,
    val hasMore: Boolean = true,
    val totalCount: Int = 0
)

sealed class DashboardEvent {
    object LoadUrls : DashboardEvent()
    object LoadMore : DashboardEvent()
    object Refresh : DashboardEvent()
    data class Search(val query: String) : DashboardEvent()
    data class FilterByCategory(val categoryId: Int?) : DashboardEvent()
    data class FilterByStatus(val status: UrlStatus?) : DashboardEvent()
    object FilterUncategorized : DashboardEvent()
    object ClearFilters : DashboardEvent()
    data class ToggleUrlStatus(val urlId: Int, val currentStatus: UrlStatus?) : DashboardEvent()
    data class NavigateToUrl(val urlId: Int) : DashboardEvent()
    object NavigateToCategories : DashboardEvent()
}
