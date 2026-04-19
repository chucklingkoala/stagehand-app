package com.chucklingkoala.stagehand.presentation.dashboard

import com.chucklingkoala.stagehand.domain.model.UrlStatus

sealed class DashboardFilter {
    object All : DashboardFilter()
    object Inbox : DashboardFilter()              // episode_id = 0 (IS NULL)
    object Uncategorized : DashboardFilter()      // category_id = 0 (IS NULL)
    object Covered : DashboardFilter()            // covered = true
    data class Status(val status: UrlStatus) : DashboardFilter()
    data class Category(val id: Int) : DashboardFilter()
    data class Episode(val id: Int) : DashboardFilter()
}

data class FilterQuery(
    val categoryId: Int? = null,
    val episodeId: Int? = null,
    val status: String? = null,
    val covered: Boolean? = null
)

fun DashboardFilter.toQuery(): FilterQuery = when (this) {
    DashboardFilter.All -> FilterQuery()
    DashboardFilter.Inbox -> FilterQuery(episodeId = 0)
    DashboardFilter.Uncategorized -> FilterQuery(categoryId = 0)
    DashboardFilter.Covered -> FilterQuery(covered = true)
    is DashboardFilter.Status -> FilterQuery(status = this.status.value)
    is DashboardFilter.Category -> FilterQuery(categoryId = this.id)
    is DashboardFilter.Episode -> FilterQuery(episodeId = this.id)
}
