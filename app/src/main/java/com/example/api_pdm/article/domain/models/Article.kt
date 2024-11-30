package com.example.api_pdm.article.domain.models

// Modelo de dados representando um artigo na camada de domínio
data class Article(
    // ID único do artigo
    val articleId: String,

    // Título do artigo
    val title: String,

    // Descrição breve do artigo
    val description: String,

    // Conteúdo completo do artigo
    val content: String,

    // Data de publicação do artigo
    val pubDate: String,

    // Nome da fonte do artigo
    val sourceName: String,

    // URL da imagem associada ao artigo
    val imageUrl: String
)
