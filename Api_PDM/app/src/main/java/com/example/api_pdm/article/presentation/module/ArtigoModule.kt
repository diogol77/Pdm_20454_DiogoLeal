package com.example.api_pdm.article.presentation.module

import com.example.api_pdm.article.presentation.viewmodel.ArticleViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// Módulo Koin que fornece dependências para o ViewModel relacionado a artigos
val articleModule = module {
    // Define a injeção de dependência para o ViewModel, usando o Koin
    viewModel { ArticleViewModel(get()) } // 'get()' resolve as dependências necessárias para o ViewModel
}
