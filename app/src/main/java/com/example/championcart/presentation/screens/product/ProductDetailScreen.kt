package com.example.championcart.presentation.screens.product

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.championcart.domain.models.Product
import com.example.championcart.domain.models.StorePrice
import com.example.championcart.presentation.components.common.*
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onNavigateBack: () -> Unit,
    onNavigateToStore: (String) -> Unit = {},
    onNavigateToScan: () -> Unit = {},
    innerPadding: PaddingValues = PaddingValues(),
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->
                    ChampionSnackbar(snackbarData = snackbarData)
                }
            )
        },
        topBar = {
            ChampionCartTopBar(
                title = uiState.product?.name ?: "פרטי מוצר",
                navigationIcon = {
                    BackButton(onClick = onNavigateBack)
                },
                actions = listOf(
                    TopBarAction(
                        icon = Icons.Rounded.Share,
                        contentDescription = "שתף",
                        onClick = {
                            // Share product functionality
                        }
                    )
                )
            )
        },
        floatingActionButton = {
            if (uiState.product != null && !uiState.isInCart) {
                FloatingActionButton(
                    onClick = {
                        uiState.product?.let { product ->
                            viewModel.addToCart(product)
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "נוסף לעגלה",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    },
                    modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
                    containerColor = BrandColors.ElectricMint,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AddShoppingCart,
                        contentDescription = "הוסף לעגלה"
                    )
                }
            }
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingScreen(
                    message = "טוען פרטי מוצר...",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            uiState.error != null -> {
                ErrorScreen(
                    message = uiState.error ?: "שגיאה לא ידועה",
                    onRetry = { viewModel.loadProduct(productId) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            uiState.product != null -> {
                val product = uiState.product!!
                LazyColumn(
                    state = rememberLazyListState(),
                    contentPadding = PaddingValues(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding() + innerPadding.calculateBottomPadding() + 80.dp
                    )
                ) {
                    // Product Header
                    item {
                        ProductHeader(
                            product = product,
                            isInCart = uiState.isInCart,
                            cartQuantity = uiState.cartQuantity
                        )
                    }

                    // Quick Stats
                    item {
                        QuickStatsSection(
                            product = product,
                            modifier = Modifier.padding(horizontal = Spacing.l)
                        )
                    }

                    // Price Comparison
                    item {
                        Spacer(modifier = Modifier.height(Spacing.l))
                        Text(
                            text = "השוואת מחירים",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = Spacing.l)
                        )
                        Spacer(modifier = Modifier.height(Spacing.m))
                    }

                    // Store Prices
                    items(
                        items = product.stores,
                        key = { it.storeName }
                    ) { storePrice ->
                        PriceCard(
                            storeName = storePrice.storeName,
                            price = "₪${storePrice.price}",
                            priceLevel = when (storePrice.priceLevel) {
                                com.example.championcart.domain.models.PriceLevel.BEST -> PriceLevel.Best
                                com.example.championcart.domain.models.PriceLevel.MID -> PriceLevel.Mid
                                com.example.championcart.domain.models.PriceLevel.HIGH -> PriceLevel.High
                            },
                            modifier = Modifier
                                .padding(horizontal = Spacing.l, vertical = Spacing.s)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductHeader(
    product: Product,
    isInCart: Boolean,
    cartQuantity: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        Color.Transparent
                    )
                )
            )
            .padding(Spacing.l),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Product Image
        Card(
            modifier = Modifier.size(200.dp),
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            AsyncImage(
                model = product.imageUrl ?: "https://via.placeholder.com/200",
                contentDescription = product.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(Spacing.l))

        // Product Name
        Text(
            text = product.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        // Barcode
        product.barcode?.let { barcode ->
            Spacer(modifier = Modifier.height(Spacing.s))
            Text(
                text = barcode,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Category
        Spacer(modifier = Modifier.height(Spacing.m))
        ChampionChip(
            text = product.category,
            selected = false,
            onClick = {}
        )
    }
}

@Composable
private fun QuickStatsSection(
    product: Product,
    modifier: Modifier = Modifier
) {
    val savings = product.stores.maxOfOrNull { it.price }?.let { maxPrice ->
        maxPrice - product.bestPrice
    } ?: 0.0

    val savingsPercentage = product.stores.maxOfOrNull { it.price }?.let { maxPrice ->
        if (maxPrice > 0) ((savings / maxPrice) * 100).toInt() else 0
    } ?: 0

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        // Best Price Card
        GlassCard(
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier.padding(Spacing.m),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Rounded.PriceCheck,
                    contentDescription = null,
                    tint = PriceColors.Best,
                    modifier = Modifier.size(Size.icon)
                )

                Spacer(modifier = Modifier.height(Spacing.s))

                Text(
                    text = "מחיר הכי זול",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "₪${product.bestPrice}",
                    style = TextStyles.price,
                    color = PriceColors.Best
                )

                Text(
                    text = product.bestStore,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Savings Card
        GlassCard(
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier.padding(Spacing.m),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Rounded.Savings,
                    contentDescription = null,
                    tint = BrandColors.CosmicPurple,
                    modifier = Modifier.size(Size.icon)
                )

                Spacer(modifier = Modifier.height(Spacing.s))

                Text(
                    text = "חיסכון מקסימלי",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "₪${String.format("%.2f", savings)}",
                    style = TextStyles.price,
                    color = BrandColors.CosmicPurple
                )

                Text(
                    text = "$savingsPercentage% חיסכון",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}