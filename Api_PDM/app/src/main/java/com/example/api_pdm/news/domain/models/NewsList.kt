package com.example.api_pdm.news.domain.models

import com.example.api_pdm.article.domain.models.Article  // Importa o modelo de Article

// Classe que representa uma lista de notícias
data class NewsList(
    val nextPage: String?,  // Variável que indica a próxima página de notícias (se houver)
    val articles: List<Article>,  // Lista de artigos (notícias) que fazem parte da lista
)
