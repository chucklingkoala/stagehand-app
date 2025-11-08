package com.chucklingkoala.stagehand.di

import com.chucklingkoala.stagehand.data.remote.NetworkModule
import com.chucklingkoala.stagehand.data.repository.CategoryRepository
import com.chucklingkoala.stagehand.data.repository.UrlRepository
import com.chucklingkoala.stagehand.presentation.categories.CategoriesViewModel
import com.chucklingkoala.stagehand.presentation.dashboard.DashboardViewModel
import com.chucklingkoala.stagehand.presentation.urldetail.UrlDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Network
    single { NetworkModule.api }

    // Repositories
    single { UrlRepository(get()) }
    single { CategoryRepository(get()) }

    // ViewModels
    viewModel { DashboardViewModel(get(), get()) }
    viewModel { (urlId: Int) -> UrlDetailViewModel(urlId, get(), get()) }
    viewModel { CategoriesViewModel(get()) }
}
