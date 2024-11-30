package com.example.api_pdm.news.presentation.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.api_pdm.article.domain.repository.NewsRepository
import com.example.api_pdm.news.domain.models.NewsResult
import com.example.api_pdm.news.presentation.design.NewsAction
import com.example.api_pdm.news.presentation.state.NewsState
import kotlinx.coroutines.launch

class NewsViewModel(
    private val newsRepository: NewsRepository
) : ViewModel() {

    // Armazenamento do estado da UI
    var state by mutableStateOf(NewsState())
        private set

    // Inicializa com o carregamento das notícias
    init {
        loadNews()
    }

    // Função para processar as ações disparadas pela UI
    fun onAction(action: NewsAction) {
        when (action) {
            NewsAction.Paginate -> paginate()  // Ação de paginação
        }
    }

    // Função para carregar as notícias pela primeira vez
    private fun loadNews() {
        state = state.copy(isLoading = true) // Marca como carregando

        // Lança uma corrotina para buscar as notícias
        viewModelScope.launch {
            newsRepository.getNews().collect { result ->
                // Atualiza o estado com o resultado da busca
                state = when (result) {
                    is NewsResult.Error -> {
                        state.copy(isError = true)  // Em caso de erro
                    }
                    is NewsResult.Success -> {
                        state.copy(
                            isError = false,
                            articleList = result.data?.articles ?: emptyList(),
                            nextPage = result.data?.nextPage
                        ) // Em caso de sucesso
                    }
                }
                // Marca o fim do carregamento
                state = state.copy(isLoading = false)
            }
        }
    }

    // Função para carregar mais notícias (paginando)
    private fun paginate() {
        // Checa se há uma próxima página
        val nextPage = state.nextPage ?: return

        state = state.copy(isLoading = true) // Marca como carregando

        // Lança uma corrotina para buscar a próxima página de notícias
        viewModelScope.launch {
            newsRepository.paginate(nextPage).collect { result ->
                // Atualiza o estado com o resultado da paginação
                state = when (result) {
                    is NewsResult.Error -> {
                        state.copy(isError = true) // Em caso de erro
                    }
                    is NewsResult.Success -> {
                        val articles = result.data?.articles ?: emptyList()
                        state.copy(
                            isError = false,
                            articleList = state.articleList + articles, // Concatena os novos artigos
                            nextPage = result.data?.nextPage
                        )
                    }
                }
                // Marca o fim do carregamento
                state = state.copy(isLoading = false)
            }
        }
    }
}
