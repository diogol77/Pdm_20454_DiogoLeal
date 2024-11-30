package com.example.api_pdm.news.presentation.design


sealed interface NewsAction {
    data object Paginate: NewsAction
}