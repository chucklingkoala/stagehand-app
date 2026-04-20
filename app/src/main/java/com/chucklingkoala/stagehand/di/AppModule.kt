package com.chucklingkoala.stagehand.di

import com.chucklingkoala.stagehand.data.local.TokenManager
import com.chucklingkoala.stagehand.data.remote.NetworkModule
import com.chucklingkoala.stagehand.data.repository.AuthRepository
import com.chucklingkoala.stagehand.data.repository.CategoryRepository
import com.chucklingkoala.stagehand.data.repository.EpisodeRepository
import com.chucklingkoala.stagehand.data.repository.UrlRepository
import com.chucklingkoala.stagehand.presentation.categories.CategoriesViewModel
import com.chucklingkoala.stagehand.presentation.dashboard.DashboardViewModel
import com.chucklingkoala.stagehand.presentation.login.LoginViewModel
import com.chucklingkoala.stagehand.presentation.urldetail.UrlDetailViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    // Auth
    single { TokenManager(androidContext()) }
    single { NetworkModule.create(tokenManager = get()) }
    single { AuthRepository(api = get(), tokenManager = get()) }

    // Repositories
    factory { UrlRepository(api = get()) }
    factory { CategoryRepository(api = get()) }
    factory { EpisodeRepository(api = get()) }

    // ViewModels
    factory { LoginViewModel(authRepository = get(), tokenManager = get()) }

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
