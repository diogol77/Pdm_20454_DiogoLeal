package com.example.api_pdm.news.domain.repository

import com.example.api_pdm.article.data.local.database.ArticleEntity
import com.example.api_pdm.article.data.remote.ArticleDto
import com.example.api_pdm.article.domain.models.Article
import com.example.api_pdm.article.data.remote.NewsListDto
import com.example.api_pdm.news.domain.models.NewsList

// Converte NewsListDto para NewsList
fun NewsListDto.toNewsList() = NewsList(
    nextPage = nextPage,
    articles = results?.map(ArticleDto::toArticle) ?: emptyList()
)

fun ArticleDto.toArticle(): Article {
    return Article(
        articleId = article_id ?: "",
        title = title ?: "",
        description = description ?: "",
        pubDate = pubDate ?: "",
        sourceName = source_name ?: "",
        imageUrl = image_url ?: "",
        content = "$article_id: Contéudo gerado do artigo nao aparece pelo fato do plano da API usada ser gratuito"
        ///Não é possivel mostrar o content pelo fato da API ser gratuita , senao daria
    )
}

// Converte ArticleEntity para Article
fun ArticleEntity.toArticle() = Article(
    articleId = articleId,
    title = title,
    description = description,
    pubDate = pubDate,
    sourceName = sourceName,
    imageUrl = imageUrl,
    content = content
)

// Converte Article para ArticleEntity
fun Article.toArticleEntity() = ArticleEntity(
    articleId = articleId,
    title = title,
    description = description,
    pubDate = pubDate,
    sourceName = sourceName,
    imageUrl = imageUrl,
    content = content
)
