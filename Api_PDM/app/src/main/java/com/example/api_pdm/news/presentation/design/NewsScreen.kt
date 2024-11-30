package com.example.api_pdm.news.presentation.design

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.api_pdm.news.presentation.state.NewsState
import com.example.api_pdm.article.domain.models.Article
import com.example.api_pdm.news.presentation.viewmodel.NewsViewModel
import com.example.api_pdm.presentation.ui.theme.NewsAppTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewsScreenCore(
    viewModel: NewsViewModel = koinViewModel(),
    onArticleClick: (String) -> Unit
) {
    NewsScreen(
        state = viewModel.state,
        onAction = viewModel::onAction,
        onArticleClick = onArticleClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewsScreen(
    state: NewsState,
    onAction: (NewsAction) -> Unit,
    onArticleClick: (String) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState()
    )

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = "Latest News",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                },
                windowInsets = WindowInsets(
                    top = 50.dp, bottom = 8.dp
                ),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary // Cor fixa para a barra
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background) // Fixa o fundo
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Exibição de carregamento ou erro
            if (state.isLoading && state.articleList.isEmpty()) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
            }

            if (state.isError && state.articleList.isEmpty()) {
                Text(
                    text = "Can't Load News",
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Exibição das notícias
            if (state.articleList.isNotEmpty()) {
                val listState = rememberLazyListState()

                // Controle de paginação
                val shouldPaginate = remember {
                    derivedStateOf {
                        val totalItems = listState.layoutInfo.totalItemsCount
                        val lastVisibleIndex = listState.layoutInfo
                            .visibleItemsInfo.lastOrNull()?.index ?: 0

                        lastVisibleIndex == totalItems - 1 && !state.isLoading
                    }
                }

                // LaunchedEffect para carregar mais artigos quando chegar ao final da lista
                LaunchedEffect(key1 = listState) {
                    snapshotFlow { shouldPaginate.value }
                        .distinctUntilChanged()
                        .filter { it }
                        .collect { onAction(NewsAction.Paginate) }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 8.dp),
                    state = listState
                ) {
                    itemsIndexed(
                        items = state.articleList,
                        key = { _, article -> article.articleId }
                    ) { index, article ->
                        ArticleItem(
                            article = article,
                            onArticleClick = onArticleClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleItem(
    modifier: Modifier = Modifier,
    article: Article,
    onArticleClick: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onArticleClick(article.articleId) }
            .padding(vertical = 20.dp, horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
            .clip(MaterialTheme.shapes.medium)
            .padding(16.dp)
    ) {
        // Título da fonte
        Text(
            text = article.sourceName,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary, // Corrigido para usar `primary` ao invés de `primaryVariant`
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Título do artigo
        Text(
            text = article.title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Imagem do artigo
        AsyncImage(
            model = article.imageUrl.takeIf { it.isNotEmpty() } ?: "https://via.placeholder.com/150", // Fallback se URL estiver vazia
            contentDescription = article.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // Reduzir um pouco a altura para melhor balanceamento do layout
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.primary.copy(0.2f)) // Fundo suave atrás da imagem
                .padding(vertical = 8.dp)
        )

        // Descrição do artigo
        Text(
            text = article.description,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Data de publicação
        Text(
            text = article.pubDate,
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.secondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp)
        )
    }

    // Linha separadora
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            .padding(vertical = 8.dp)
    )
}

@Preview
@Composable
private fun NewsScreenPreview() {
    NewsAppTheme {
        NewsScreen(
            state = NewsState(),
            onAction = {},
            onArticleClick = {}
        )
    }
}
