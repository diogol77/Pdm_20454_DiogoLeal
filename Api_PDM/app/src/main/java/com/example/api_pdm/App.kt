package com.example.api_pdm

import android.app.Application
import com.example.api_pdm.article.presentation.module.articleModule

import com.example.api_pdm.news.presentation.module.coreModule
import com.example.api_pdm.news.presentation.module.newsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                coreModule,
                newsModule,
                articleModule
            )
        }
    }

}