package com.example.api_pdm.news.presentation.state

// Importação necessária para o modelo de dados Article, que é usado para armazenar os artigos.
import com.example.api_pdm.article.domain.models.Article

// A classe de estado NewsState é usada para representar o estado atual da tela de notícias.
data class NewsState(
    // A lista de artigos que será exibida na interface do usuário.
    val articleList: List<Article> = emptyList(),

    // A URL da próxima página de notícias, caso haja paginação.
    val nextPage: String? = null,

    // Um indicador para saber se os dados estão sendo carregados.
    val isLoading: Boolean = false,

    // Um indicador para saber se ocorreu algum erro na busca dos dados.
    val isError: Boolean = false,
)
