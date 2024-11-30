package com.example.api_pdm.news.presentation.module

// Importações necessárias para Koin e o ViewModel da aplicação
import com.example.api_pdm.news.presentation.viewmodel.NewsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel  // Função para criar a instância do ViewModel
import org.koin.dsl.module  // Função para definir o módulo Koin

// Definição do módulo Koin para injeção de dependência relacionado ao NewsViewModel
val newsModule = module {

    // A função viewModel cria a instância do NewsViewModel e injeta a dependência necessária
    // O get() aqui é usado para pegar uma instância do repositório necessário para o ViewModel
    viewModel { NewsViewModel(get()) }
}
