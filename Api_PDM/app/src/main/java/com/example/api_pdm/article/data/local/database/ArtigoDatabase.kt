package com.example.api_pdm.article.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.api_pdm.article.data.local.dao.ArticlesDao

// Classe que define a base de dados Room para armazenar artigos
@Database(
    entities = [ArticleEntity::class], // Define as tabelas da base de dados
    version = 1 // Versão da base de dados
)
abstract class ArticleDatabase : RoomDatabase() {
    // Referência ao DAO (Data Access Object) que fornece métodos de acesso à base de dados
    abstract val dao: ArticlesDao
}
