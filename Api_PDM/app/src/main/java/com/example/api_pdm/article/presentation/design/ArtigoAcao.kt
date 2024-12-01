package com.example.api_pdm.article.presentation.design

// Sealed interface para definir as ações possíveis relacionadas ao artigo
sealed interface ArticleAction {

    // Ação que carrega um artigo com base no seu ID
    data class LoadArticle(val articleId: String) : ArticleAction
}
