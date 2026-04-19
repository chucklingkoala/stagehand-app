package com.chucklingkoala.stagehand.presentation.urldetail

import com.chucklingkoala.stagehand.domain.model.Category
import com.chucklingkoala.stagehand.domain.model.Episode
import com.chucklingkoala.stagehand.domain.model.LinkPreview
import com.chucklingkoala.stagehand.domain.model.Url
import com.chucklingkoala.stagehand.domain.model.UrlStatus

data class UrlDetailState(
    val url: Url? = null,
    val linkPreview: LinkPreview? = null,
    val categories: List<Category> = emptyList(),
    val episodes: List<Episode> = emptyList(),
    val selectedCategoryId: Int? = null,
    val selectedEpisodeId: Int? = null,
    val selectedStatus: UrlStatus? = null,
    val selectedCovered: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isLoadingPreview: Boolean = false,
    val savedSuccessfully: Boolean = false,
    val error: String? = null
)

sealed class UrlDetailEvent {
    object LoadUrl : UrlDetailEvent()
    object LoadLinkPreview : UrlDetailEvent()
    data class SelectCategory(val categoryId: Int?) : UrlDetailEvent()
    data class SelectEpisode(val episodeId: Int?) : UrlDetailEvent()
    data class SelectStatus(val status: UrlStatus?) : UrlDetailEvent()
    data class SetCovered(val covered: Boolean) : UrlDetailEvent()
    object SaveChanges : UrlDetailEvent()
    object OpenInBrowser : UrlDetailEvent()
    object CopyUrl : UrlDetailEvent()
    object ShareUrl : UrlDetailEvent()
    object ViewOnDiscord : UrlDetailEvent()
}
