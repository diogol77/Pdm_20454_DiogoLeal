package com.example.api_pdm.news.domain.repository

import com.example.api_pdm.article.data.local.dao.ArticlesDao
import com.example.api_pdm.article.domain.models.Article
import com.example.api_pdm.article.domain.repository.NewsRepository
import com.example.api_pdm.article.data.remote.NewsListDto
import com.example.api_pdm.news.domain.models.NewsList
import com.example.api_pdm.news.domain.models.NewsResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.cancellation.CancellationException

class NewsRepositoryImpl(
    private val httpClient: HttpClient,  // Cliente HTTP para fazer requisições
    private val dao: ArticlesDao       // DAO para interagir com a base de dados local
) : NewsRepository {

    private val tag = "NewsRepository: "  // Tag para logs

    private val baseUrl = "https://newsdata.io/api/1/latest"  // URL base para API
    private val apiKey = "pub_606896a05a2d70071bc983e9875993403b285" // Chave da API

    // Função para obter notícias locais a partir do banco de dados
    private suspend fun getLocalNews(nextPage: String?): NewsList {
        val localNews = dao.getArticleList()  // Obtém a lista de artigos do banco
        println(tag + "getLocalNews: " + localNews.size + " nextPage: " + nextPage)

        return NewsList(
            nextPage = nextPage,  // Passa a próxima página para a lista
            articles = localNews.map { it.toArticle() }  // Converte os artigos para o modelo de artigo
        )
    }

    // Função para obter notícias remotas da API
    private suspend fun getRemoteNews(nextPage: String?): NewsList? {
        println(tag + "Fazendo requisição para: $baseUrl?apikey=$apiKey&language=en")
        return try {
            val response: HttpResponse = httpClient.get(baseUrl) {
                parameter("apikey", apiKey)  // Adiciona a chave da API
                parameter("language", "en")  // Define o idioma como inglês
                nextPage?.let { parameter("page", it) }  // Se houver próxima página, adiciona ao parâmetro
            }

            println("Código de status da resposta: ${response.status.value}")
            if (response.status.isSuccess()) {  // Se a resposta for bem-sucedida
                val newsListDto: NewsListDto = response.body()  // Converte a resposta em um objeto DTO
                println(tag + "getRemoteNews: " + newsListDto.results?.size + " nextPage: " + nextPage)
                newsListDto.toNewsList()  // Converte o DTO para o modelo de lista de notícias
            } else {
                throw Exception("Erro ao buscar dados: ${response.status.value}")
            }
        } catch (e: Exception) {
            e.printStackTrace()  // Exibe o erro
            if (e is CancellationException) throw e  // Se for uma exceção de cancelamento, relança
            null  // Caso contrário, retorna null
        }
    }

    // Função para obter notícias, tentando primeiro da API remota, depois do banco local
    override suspend fun getNews(): Flow<NewsResult<NewsList>> = flow {
        // Tenta buscar as notícias remotamente
        val remoteNewsList = getRemoteNews(null)

        // Se obtiver notícias remotas, salva no banco e emite o resultado
        if (remoteNewsList != null) {
            dao.clearDatabase()  // Limpa o banco de dados
            dao.upsertArticleList(remoteNewsList.articles.map { it.toArticleEntity() })  // Atualiza o banco com os novos artigos
            emit(NewsResult.Success(getLocalNews(remoteNewsList.nextPage)))  // Emite as notícias locais atualizadas
        } else {
            // Se as notícias remotas falharem, tenta pegar as notícias locais
            val localNewsList = getLocalNews(null)
            if (localNewsList.articles.isNotEmpty()) {
                emit(NewsResult.Success(localNewsList))  // Emite as notícias locais
            } else {
                emit(NewsResult.Error("No Data"))  // Se não houver dados, emite erro
            }
        }
    }

    // Função para fazer paginação, obtendo a próxima página de notícias
    override suspend fun paginate(nextPage: String?): Flow<NewsResult<NewsList>> = flow {
        // Tenta buscar as notícias remotas paginadas
        val remoteNewsList = getRemoteNews(nextPage)

        // Se a busca remota for bem-sucedida, atualiza o banco e emite o resultado
        remoteNewsList?.let {
            dao.upsertArticleList(remoteNewsList.articles.map { it.toArticleEntity() })  // Atualiza o banco
            emit(NewsResult.Success(remoteNewsList))  // Emite as notícias remotas
        } ?: emit(NewsResult.Error("No Data"))  // Se falhar, emite erro
    }

    // Função para buscar um artigo específico, primeiro no banco de dados e depois remotamente se necessário
    override suspend fun getArticle(articleId: String): Flow<NewsResult<Article>> = flow {
        // Tenta buscar o artigo no banco de dados local
        dao.getArticle(articleId)?.let { article ->
            println(tag + "get local article " + article.articleId)
            emit(NewsResult.Success(article.toArticle()))  // Se encontrado, emite o artigo
            return@flow
        }

        // Se não encontrar, tenta buscar o artigo remotamente
        try {
            val remoteArticle: NewsListDto = httpClient.get(baseUrl) {
                parameter("apikey", apiKey)
                parameter("id", articleId)  // Passa o ID do artigo na requisição
            }.body()

            println(tag + "get article remote " + remoteArticle.results?.size)
            if (remoteArticle.results?.isNotEmpty() == true) {
                emit(NewsResult.Success(remoteArticle.results[0].toArticle()))  // Se encontrado, emite o artigo
            } else {
                emit(NewsResult.Error("Can't Load Article"))  // Se não encontrado, emite erro
            }
        } catch (e: Exception) {
            e.printStackTrace()  // Exibe o erro
            if (e is CancellationException) throw e  // Relança exceção de cancelamento
            println(tag + "get article remote exception: " + e.message)
            emit(NewsResult.Error("Can't Load Article"))  // Se ocorrer erro, emite erro
        }
    }
}
