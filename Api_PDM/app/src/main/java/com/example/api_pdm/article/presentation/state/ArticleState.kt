package com.example.api_pdm.article.presentation.state

import com.example.api_pdm.article.domain.models.Article

// Classe que representa o estado de uma tela ou componente relacionado ao artigo
data class ArticleState(
    // O artigo carregado (pode ser nulo caso ainda não tenha sido carregado)
    val article: Article? = null,

    // Indica se o artigo está sendo carregado (exemplo: enquanto espera resposta de uma API)
    val isLoading: Boolean = false,

    // Indica se ocorreu algum erro ao tentar carregar o artigo
    val isError: Boolean = false
)
