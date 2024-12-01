package com.example.api_pdm.article.presentation.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.api_pdm.article.domain.models.Article
import com.example.api_pdm.article.presentation.viewmodel.ArticleViewModel
import org.koin.androidx.compose.koinViewModel

// Função composable para exibir a tela de detalhes do artigo
@Composable
fun ArticleScreenCore(
    viewModel: ArticleViewModel = koinViewModel(), // Obtém o ViewModel usando Koin
    articleId: String // Recebe o ID do artigo como parâmetro
) {
    // Efeito que dispara quando a composição é realizada, carregando o artigo
    LaunchedEffect(true) {
        viewModel.onAction(ArticleAction.LoadArticle(articleId)) // Aciona o carregamento do artigo
    }

    // Estrutura Scaffold para a tela
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFF121212)), // Define o fundo escuro
            contentAlignment = Alignment.Center
        ) {
            // Exibe indicador de carregamento enquanto o artigo não for carregado
            if (viewModel.state.isLoading && viewModel.state.article == null) {
                CircularProgressIndicator(color = Color.Cyan) // Indicador ciano de carregamento
            }

            // Exibe mensagem de erro caso o carregamento falhe
            if (viewModel.state.isError && viewModel.state.article == null) {
                Text(
                    text = "Error: Couldn't load article",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red, // Mensagem de erro em vermelho
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Exibe o conteúdo do artigo caso tenha sido carregado com sucesso
        viewModel.state.article?.let { article ->
            ArticleScreen(
                modifier = Modifier.padding(paddingValues),
                article = article
            )
        }
    }
}

// Função composable para exibir o conteúdo do artigo
@Composable
private fun ArticleScreen(
    modifier: Modifier = Modifier,
    article: Article // Recebe o artigo como parâmetro
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Permite a rolagem do conteúdo
            .padding(16.dp)
            .background(Color(0xFF1E1E1E)) // Fundo escuro mais suave
            .clip(RoundedCornerShape(16.dp)) // Bordas arredondadas
            .shadow(8.dp, shape = RoundedCornerShape(16.dp)) // Sombra para profundidade
            .padding(16.dp)
    ) {
        // Exibe o título do artigo
        Text(
            text = article.title,
            fontFamily = FontFamily.Serif, // Fonte serifada para estilo
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFAE100), // Cor amarela dourada para o título
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 20.dp)
        )

        // Exibe a fonte ou autor do artigo
        Text(
            text = article.sourceName,
            fontFamily = FontFamily.Cursive, // Fonte cursiva para um estilo elegante
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFAAAAAA), // Cor cinza para o texto
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Exibe a data de publicação do artigo
        Text(
            text = article.pubDate,
            fontFamily = FontFamily.Serif,
            fontSize = 14.sp,
            color = Color(0xFFBBBBBB),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Exibe a imagem do artigo
        AsyncImage(
            model = article.imageUrl,
            contentDescription = article.title,
            contentScale = ContentScale.Crop, // Faz o corte da imagem para ajustar
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp)) // Bordas arredondadas para a imagem
                .background(Color(0xFF333333).copy(alpha = 0.6f)) // Fundo escuro semi-transparente na imagem
                .padding(bottom = 16.dp)
        )

        // Exibe a descrição do artigo
        Text(
            text = article.description,
            fontFamily = FontFamily.Serif,
            fontSize = 16.sp,
            fontWeight = FontWeight.Light,
            color = Color(0xFFDDDDDD), // Cor suave para a descrição
            textAlign = TextAlign.Justify, // Justifica o texto
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Divisória para separar seções
        HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp), color = Color.Gray.copy(alpha = 0.3f))

        // Exibe o conteúdo principal do artigo
        Text(
            text = article.content.substringAfter(":"),
            fontFamily = FontFamily.Serif,
            fontSize = 16.sp,
            color = Color(0xFFDDDDDD), // Cor suave para o conteúdo
            textAlign = TextAlign.Justify,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
