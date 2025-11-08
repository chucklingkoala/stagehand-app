package com.chucklingkoala.stagehand.presentation.urldetail

import com.chucklingkoala.stagehand.domain.model.Category
import com.chucklingkoala.stagehand.domain.model.LinkPreview
import com.chucklingkoala.stagehand.domain.model.Url
import com.chucklingkoala.stagehand.domain.model.UrlStatus

data class UrlDetailState(
    val url: Url? = null,
    val linkPreview: LinkPreview? = null,
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: Int? = null,
    val selectedStatus: UrlStatus? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isLoadingPreview: Boolean = false,
    val error: String? = null
)

sealed class UrlDetailEvent {
    object LoadUrl : UrlDetailEvent()
    object LoadLinkPreview : UrlDetailEvent()
    data class SelectCategory(val categoryId: Int?) : UrlDetailEvent()
    data class SelectStatus(val status: UrlStatus?) : UrlDetailEvent()
    object SaveChanges : UrlDetailEvent()
    object OpenInBrowser : UrlDetailEvent()
    object CopyUrl : UrlDetailEvent()
    object ShareUrl : UrlDetailEvent()
    object ViewOnDiscord : UrlDetailEvent()
}
