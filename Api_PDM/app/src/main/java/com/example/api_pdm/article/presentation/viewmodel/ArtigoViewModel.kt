package com.example.api_pdm.article.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.api_pdm.article.domain.repository.NewsRepository
import com.example.api_pdm.article.presentation.state.EstadoArtigo
import com.example.api_pdm.article.presentation.design.ArticleAction
import com.example.api_pdm.news.domain.models.NewsResult
import kotlinx.coroutines.launch

// ViewModel responsável pela lógica de negócios e interação com o repositório para carregar dados do artigo
class ArticleViewModel(
    private val newsRepository: NewsRepository // Repositório que fornece os dados do artigo
) : ViewModel() {

    // Estado do artigo, observável pelas telas (Composables)
    var state by mutableStateOf(EstadoArtigo()) // Inicializa com valores padrão
        private set

    // Função que lida com as ações relacionadas ao artigo
    fun onAction(action: ArticleAction) {
        // Apenas chama a função para carregar o artigo
        if (action is ArticleAction.LoadArticle) loadArticle(action.articleId)
    }

    // Função para buscar o artigo, com base no ID
    private fun loadArticle(articleId: String) {
        // Verifica se o ID do artigo é válido
        val newState = if (articleId.isBlank()) {
            // Se o ID estiver em branco, marca erro no estado
            state.copy(isError = true)
        } else {
            // Caso contrário, inicia o carregamento
            state.copy(isLoading = true, isError = false)
        }

        // Atualiza o estado para refletir o início da operação
        state = newState

        // Se o ID não estiver em branco, realiza a busca do artigo
        if (articleId.isNotBlank()) {
            // Executa a busca de forma assíncrona dentro de uma corrotina
            viewModelScope.launch {
                // Coleta a resposta do repositório (erro ou sucesso)
                newsRepository.getArticle(articleId).collect { result ->
                    state = when (result) {
                        // Caso ocorra um erro, atualiza o estado para erro e termina o carregamento
                        is NewsResult.Error -> state.copy(isError = true, isLoading = false)
                        // Caso o artigo seja carregado com sucesso, atualiza o estado com os dados do artigo
                        is NewsResult.Success -> state.copy(
                            article = result.data,
                            isError = false,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
}
