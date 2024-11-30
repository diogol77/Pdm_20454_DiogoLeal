package com.example.api_pdm.article.domain.repository

import com.example.api_pdm.article.domain.models.Article
import com.example.api_pdm.news.domain.models.NewsList
import com.example.api_pdm.news.domain.models.NewsResult
import kotlinx.coroutines.flow.Flow

// Interface que define as operações de acesso aos dados relacionados a notícias
interface NewsRepository {

    // Função para obter a lista de notícias
    suspend fun getNews(): Flow<NewsResult<NewsList>>

    // Função para paginar as notícias com base na próxima página
    suspend fun paginate(nextPage: String?): Flow<NewsResult<NewsList>>

    // Função para obter um artigo específico pelo seu ID
    suspend fun getArticle(articleId: String): Flow<NewsResult<Article>>
}
