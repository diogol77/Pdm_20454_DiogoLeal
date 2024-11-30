package com.example.api_pdm.article.data.remote

import kotlinx.serialization.Serializable

// Classe de dados para mapear a resposta da API contendo uma lista de artigos
@Serializable
data class NewsListDto(
    // URL da próxima página de resultados, caso exista
    val nextPage: String?,

    // Lista de artigos recuperados da API
    val results: List<ArticleDto>?,
)
