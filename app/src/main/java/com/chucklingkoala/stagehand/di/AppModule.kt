package com.chucklingkoala.stagehand.di

import com.chucklingkoala.stagehand.data.remote.NetworkModule
import com.chucklingkoala.stagehand.data.repository.CategoryRepository
import com.chucklingkoala.stagehand.data.repository.EpisodeRepository
import com.chucklingkoala.stagehand.data.repository.UrlRepository
import com.chucklingkoala.stagehand.presentation.categories.CategoriesViewModel
import com.chucklingkoala.stagehand.presentation.dashboard.DashboardViewModel
import com.chucklingkoala.stagehand.presentation.urldetail.UrlDetailViewModel
import org.koin.dsl.module

val appModule = module {
    // Network
    single { NetworkModule.api }

    // Repositories - using factory constructor references
    factory { UrlRepository(api = get()) }
    factory { CategoryRepository(api = get()) }
    factory { EpisodeRepository(api = get()) }

    // ViewModels - using explicit factory definitions to avoid ProGuard issues
    factory {
        DashboardViewModel(
            urlRepository = get(),
            categoryRepository = get(),
            episodeRepository = get()
        )
    }

    factory { params ->
        UrlDetailViewModel(
            urlId = params.get(),
            urlRepository = get(),
            categoryRepository = get(),
            episodeRepository = get()
        )
    }

    factory {
        CategoriesViewModel(
            categoryRepository = get()
        )
    }
}
