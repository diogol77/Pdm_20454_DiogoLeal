package com.example.api_pdm.news.presentation.module

// Importações necessárias para Koin, Ktor, Room e Serialização JSON
import androidx.room.Room
import com.example.api_pdm.article.data.local.database.ArticleDatabase
import com.example.api_pdm.article.domain.repository.NewsRepository
import com.example.api_pdm.news.domain.repository.NewsRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

// Definindo o módulo Koin para injeção de dependências
val coreModule = module {

    // Configura a injeção do NewsRepositoryImpl, vinculando à interface NewsRepository
    singleOf(::NewsRepositoryImpl).bind<NewsRepository>()

    // Cria o banco de dados ArticleDatabase com Room
    single {
        Room.databaseBuilder(
            androidApplication(),
            ArticleDatabase::class.java,  // Classe do banco de dados
            "article_db.db" // Nome do banco de dados
        ).build()
    }

    // Injeção do DAO do banco de dados
    single {
        get<ArticleDatabase>().dao  // Obtém o DAO para manipulação de dados
    }

    // Criação e configuração do cliente HTTP utilizando o motor CIO
    single {
        HttpClient(CIO) {
            expectSuccess = true // Faz o cliente lançar exceção para respostas HTTP com erro

            // Configuração do motor CIO
            engine {
                endpoint {
                    keepAliveTime = 5000 // Tempo para manter a conexão viva
                    connectTimeout = 5000 // Timeout para estabelecer a conexão
                    connectAttempts = 3 // Número de tentativas de conexão
                }
            }

            // Instalação do plugin ContentNegotiation para trabalhar com JSON
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true // Formatar o JSON de forma legível
                        isLenient = true // Permitir parsing flexível de JSON
                        ignoreUnknownKeys = true // Ignorar campos desconhecidos no JSON
                    }
                )
            }

            // Instalação do plugin DefaultRequest para adicionar cabeçalhos padrão
            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json) // Define o tipo de conteúdo como JSON
            }

            // Instalação do plugin de Logging para registrar as requisições e respostas HTTP
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message) // Imprime as mensagens de log no console
                    }
                }
                level = LogLevel.ALL // Registra todos os logs HTTP
            }
        }
    }
}
