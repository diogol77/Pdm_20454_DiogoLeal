package com.example.api_pdm.article.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.api_pdm.article.data.local.database.ArticleEntity

// Interface que define as operações de acesso à base de dados para a tabela ArticleEntity
@Dao
interface ArticlesDao {

    // Obtém a lista de todos os artigos armazenados na tabela
    @Query("SELECT * FROM articleentity")
    suspend fun getArticleList(): List<ArticleEntity>

    // Insere ou atualiza a lista de artigos na base de dados
    @Upsert
    suspend fun upsertArticleList(articleList: List<ArticleEntity>)

    // Obtém um artigo específico com base no seu ID
    @Query("SELECT * FROM articleentity WHERE articleId = :articleId")
    suspend fun getArticle(articleId: String): ArticleEntity?

    // Remove todos os artigos da tabela, limpando a base de dados
    @Query("DELETE FROM articleentity")
    suspend fun clearDatabase()
}
