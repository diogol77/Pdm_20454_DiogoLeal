package com.example.api_pdm.news.domain.models

// Classe selada que representa o resultado de uma operação de carregamento de notícias
sealed class NewsResult<T>(  // Usamos um tipo genérico T para que possa ser reutilizado para diferentes tipos de dados
    val data: T? = null,  // Dados de sucesso (pode ser nulo se houver erro)
    val error: String?  // Mensagem de erro (pode ser nula se a operação for bem-sucedida)
) {
    // Classe que representa um resultado de sucesso, contendo os dados
    class Success<T>(data: T?) : NewsResult<T>(data, null)

    // Classe que representa um resultado de erro, contendo a mensagem de erro
    class Error<T>(error: String?) : NewsResult<T>(null, error)
}
