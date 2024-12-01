package com.example.api_pdm.article.data.remote

import kotlinx.serialization.Serializable

// Classe de dados para mapear a resposta da API, representando o artigo
@Serializable
data class ArticleDto(
    // ID do artigo
    val article_id: String?,

    // Título do artigo
    val title: String?,

    // Descrição do artigo
    val description: String?,

    // Conteúdo completo do artigo
    val content: String?,

    // Data de publicação do artigo
    val pubDate: String?,

    // Nome da fonte que publicou o artigo
    val source_name: String?,

    // URL da imagem associada ao artigo
    val image_url: String?
)
