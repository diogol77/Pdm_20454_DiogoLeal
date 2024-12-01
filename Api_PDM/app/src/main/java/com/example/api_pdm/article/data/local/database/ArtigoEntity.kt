package com.example.api_pdm.article.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

// Classe que define a entidade da tabela "articleEntity" na base de dados Room
@Entity
data class ArticleEntity(

    // Chave primária da entidade, sem geração automática
    @PrimaryKey(autoGenerate = false)
    val articleId: String,

    // Título do artigo
    val title: String,

    // Descrição do artigo
    val description: String,

    // Conteúdo completo do artigo
    val content: String,

    // Data de publicação do artigo
    val pubDate: String,

    // Nome da fonte que publicou o artigo
    val sourceName: String,

    // URL da imagem associada ao artigo
    val imageUrl: String
)
