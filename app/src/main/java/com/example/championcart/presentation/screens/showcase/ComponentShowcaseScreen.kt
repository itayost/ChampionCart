package com.example.championcart.presentation.screens.showcase

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.championcart.presentation.components.common.*
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentShowcaseScreen(
    onNavigateBack: () -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Tab state
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        "טעינה",
        "כפתורים",
        "קלט",
        "כרטיסים",
        "ניווט",
        "רשימות",
        "משוב",
        "חלונות",
        "מיוחדים"
    )

    Scaffold(
        topBar = {
            Column {
                ChampionTopBar(
                    title = "מרכז רכיבים",
                    navigationIcon = {
                        BackButton(onClick = onNavigateBack)
                    },
                    scrollBehavior = scrollBehavior
                )
                TabBar(
                    selectedTabIndex = selectedTab,
                    tabs = tabs,
                    onTabSelected = { selectedTab = it }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->
                    ChampionSnackbar(snackbarData = snackbarData)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(Padding.l),
            verticalArrangement = Arrangement.spacedBy(Spacing.xl)
        ) {
            when (selectedTab) {
                0 -> LoadingComponents()
                1 -> ButtonComponents()
                2 -> InputComponents(snackbarHostState)
                3 -> CardComponents()
                4 -> NavigationComponents()
                5 -> ListComponents()
                6 -> FeedbackComponents()
                7 -> OverlayComponents()
                8 -> SpecializedComponents()
            }
        }
    }
}

@Composable
private fun LoadingComponents() {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.l)
    ) {
        SectionHeader(title = "מחוונים")

        // Loading Indicator
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Padding.l),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LoadingIndicator(size = 24.dp)
                LoadingIndicator(size = 48.dp)
                LoadingIndicator(size = 64.dp)
            }
        }

        // Loading Button
        LoadingButton(
            isLoading = true,
            text = "טוען...",
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )

        // Shimmer Effect
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        )

        // Product Card Skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            ProductCardSkeleton()
            ProductCardSkeleton()
        }

        // List Item Skeleton
        ListItemSkeleton()
        ListItemSkeleton(showSubtitle = false)

        // Error States
        SectionHeader(title = "מצבי שגיאה")

        ErrorCard(
            message = "לא ניתן לטעון את הנתונים",
            onRetry = {}
        )

        WarningMessage(
            message = "החיבור לאינטרנט איטי"
        )

        // Empty States
        SectionHeader(title = "מצבים ריקים")

        EmptySearchState(query = "חלב")

        EmptyCartState(onStartShopping = {})
    }
}

@Composable
private fun ButtonComponents() {
    var isLoading by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.l)
    ) {
        // Primary Buttons
        SectionHeader(title = "כפתורים ראשיים")

        PrimaryButton(
            text = "הוסף לעגלה",
            onClick = { isLoading = !isLoading },
            modifier = Modifier.fillMaxWidth(),
            isLoading = isLoading
        )

        PrimaryButton(
            text = "התחל קניות",
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Rounded.ShoppingCart
        )

        PrimaryButton(
            text = "כפתור מושבת",
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        )

        // Secondary Buttons
        SectionHeader(title = "כפתורים משניים")

        SecondaryButton(
            text = "סנן תוצאות",
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Rounded.FilterList
        )

        // Text Buttons
        SectionHeader(title = "כפתורי טקסט")

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            TextButton(text = "ביטול", onClick = {})
            TextButton(
                text = "מחק",
                onClick = {},
                color = SemanticColors.Error
            )
        }

        // Icon Buttons
        SectionHeader(title = "כפתורי אייקון")

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            IconButton(icon = Icons.Rounded.Favorite, onClick = {})
            IconButton(
                icon = Icons.Rounded.Share,
                onClick = {},
                tint = BrandColors.ElectricMint
            )
            IconButton(
                icon = Icons.Rounded.Delete,
                onClick = {},
                tint = SemanticColors.Error
            )
        }

        // FAB
        SectionHeader(title = "כפתור פעולה צף")

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            ChampionFloatingActionButton(onClick = {})
            ChampionFloatingActionButton(
                onClick = {},
                expanded = true,
                text = "סרוק מוצר"
            )
        }

        // Gradient Button
        GradientButton(
            text = "חסכון פרימיום",
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun InputComponents(snackbarHostState: SnackbarHostState) {
    val scope = rememberCoroutineScope()
    var textValue by remember { mutableStateOf("") }
    var searchValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf("תל אביב") }

    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.l)
    ) {
        // Text Fields
        SectionHeader(title = "שדות טקסט")

        ChampionTextField(
            value = textValue,
            onValueChange = { textValue = it },
            label = "שם מלא",
            placeholder = "הכנס את שמך",
            leadingIcon = Icons.Rounded.Person
        )

        ChampionTextField(
            value = "",
            onValueChange = {},
            label = "כתובת מייל",
            leadingIcon = Icons.Rounded.Email,
            isError = true,
            errorMessage = "כתובת מייל לא תקינה"
        )

        // Search Bar
        SectionHeader(title = "חיפוש")

        SearchBar(
            query = searchValue,
            onQueryChange = { searchValue = it },
            onSearch = {
                scope.launch {
                    snackbarHostState.showSnackbar("מחפש: $searchValue")
                }
            }
        )

        // Password Field
        SectionHeader(title = "סיסמה")

        PasswordField(
            password = passwordValue,
            onPasswordChange = { passwordValue = it },
            placeholder = "הכנס סיסמה חזקה"
        )

        // Dropdown
        SectionHeader(title = "רשימה נפתחת")

        ChampionDropdown(
            value = selectedCity,
            onValueChange = { selectedCity = it },
            items = listOf("תל אביב", "ירושלים", "חיפה", "באר שבע"),
            label = "בחר עיר",
            leadingIcon = Icons.Rounded.LocationCity
        )
    }
}

@Composable
private fun CardComponents() {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.l)
    ) {
        // Glass Card
        SectionHeader(title = "כרטיס זכוכית")

        GlassCard {
            Text(
                text = "זהו כרטיס עם אפקט זכוכית",
                modifier = Modifier.padding(Padding.l)
            )
        }

        // Product Cards
        SectionHeader(title = "כרטיסי מוצרים")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            ProductCard(
                name = "חלב תנובה 3%",
                imageUrl = null,
                price = "₪6.90",
                storeName = "שופרסל",
                priceLevel = PriceLevel.Best,
                onClick = {},
                onAddToCart = {}
            )

            ProductCard(
                name = "לחם אחיד פרוס",
                imageUrl = null,
                price = "₪8.50",
                storeName = "רמי לוי",
                priceLevel = PriceLevel.Mid,
                onClick = {},
                onAddToCart = {}
            )
        }

        // Store Cards
        SectionHeader(title = "כרטיסי חנויות")

        StoreCard(
            storeName = "רמי לוי",
            totalPrice = "₪256.30",
            itemCount = 15,
            distance = "2.5",
            onClick = {},
            isRecommended = true
        )

        StoreCard(
            storeName = "שופרסל",
            totalPrice = "₪289.90",
            itemCount = 15,
            distance = "1.2",
            onClick = {}
        )

        // Price Cards
        SectionHeader(title = "כרטיסי מחירים")

        PriceCard(
            storeName = "ויקטורי",
            price = "₪12.90",
            priceLevel = PriceLevel.Best,
            distance = "0.8",
            onNavigate = {}
        )

        PriceCard(
            storeName = "מגה",
            price = "₪15.50",
            priceLevel = PriceLevel.High,
            distance = "3.2"
        )
    }
}

@Composable
private fun NavigationComponents() {
    var selectedNavItem by remember { mutableStateOf("home") }
    var selectedSegment by remember { mutableStateOf(0) }

    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.l)
    ) {
        // Bottom Navigation
        SectionHeader(title = "ניווט תחתון")

        ChampionBottomNavBar(
            selectedRoute = selectedNavItem,
            onNavigate = { selectedNavItem = it },
            items = listOf(
                BottomNavItem(
                    route = "home",
                    label = "בית",
                    icon = Icons.Rounded.Home
                ),
                BottomNavItem(
                    route = "search",
                    label = "חיפוש",
                    icon = Icons.Rounded.Search
                ),
                BottomNavItem(
                    route = "cart",
                    label = "עגלה",
                    icon = Icons.Rounded.ShoppingCart,
                    badge = "3"
                ),
                BottomNavItem(
                    route = "profile",
                    label = "פרופיל",
                    icon = Icons.Rounded.Person
                )
            )
        )

        // Segmented Button
        SectionHeader(title = "כפתורים מפולחים")

        SegmentedButton(
            selectedIndex = selectedSegment,
            options = listOf("יומי", "שבועי", "חודשי"),
            onSelectionChange = { selectedSegment = it }
        )
    }
}

@Composable
private fun ListComponents() {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.l)
    ) {
        // List Items
        SectionHeader(title = "פריטי רשימה")

        ChampionListItem(
            title = "הגדרות חשבון",
            subtitle = "נהל את פרטי החשבון שלך",
            leadingIcon = Icons.Rounded.AccountCircle,
            onClick = {}
        )

        ChampionDivider()

        ChampionListItem(
            title = "התראות",
            subtitle = "4 התראות חדשות",
            leadingIcon = Icons.Rounded.Notifications,
            trailingContent = {
                ChampionBadge(count = 4)
            }
        )

        ChampionDivider()

        ChampionListItem(
            title = "שפה",
            leadingIcon = Icons.Rounded.Language,
            trailingContent = {
                Text(
                    text = "עברית",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )
    }
}

@Composable
private fun FeedbackComponents() {
    var showSuccess by remember { mutableStateOf(false) }
    var rating by remember { mutableStateOf(3.5f) }

    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.l)
    ) {
        // Chips
        SectionHeader(title = "צ׳יפס")

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            ChampionChip(text = "כשר", selected = true)
            ChampionChip(text = "ללא גלוטן")
            ChampionChip(text = "אורגני", leadingIcon = Icons.Rounded.Eco)
        }

        // Progress Bar
        SectionHeader(title = "סרגל התקדמות")

        ChampionProgressBar(progress = 0.7f)

        // Price Level Indicators
        SectionHeader(title = "מחווני מחיר")

        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            PriceLevelIndicator(priceLevel = PriceLevel.Best)
            PriceLevelIndicator(priceLevel = PriceLevel.Mid)
            PriceLevelIndicator(priceLevel = PriceLevel.High)
        }

        // Rating Bar
        SectionHeader(title = "דירוג")

        RatingBar(
            rating = rating,
            enabled = true,
            onRatingChange = { rating = it }
        )

        // Info Card
        InfoCard(
            message = "המחירים מתעדכנים כל שעה ממאגרי הרשתות",
            action = {
                TextButton(
                    text = "למד עוד",
                    onClick = {}
                )
            }
        )

        // Success Indicator
        Button(
            onClick = { showSuccess = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("הצג הודעת הצלחה")
        }

        SuccessIndicator(
            show = showSuccess,
            onDismiss = { showSuccess = false }
        )
    }
}

@Composable
private fun OverlayComponents() {
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showConfirmation by remember { mutableStateOf(false) }
    var showLoading by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.l)
    ) {
        // Bottom Sheet
        Button(
            onClick = { showBottomSheet = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("פתח תפריט תחתון")
        }

        // Dialog
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("פתח דיאלוג")
        }

        // Confirmation
        Button(
            onClick = { showConfirmation = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("פתח אישור מחיקה")
        }

        // Loading Overlay
        Button(
            onClick = { showLoading = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("הצג מסך טעינה")
        }

        // Popup Menu
        Box {
            Button(
                onClick = { showMenu = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("פתח תפריט קופץ")
            }

            PopupMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                items = listOf(
                    PopupMenuItem(
                        label = "ערוך",
                        icon = Icons.Rounded.Edit,
                        onClick = {}
                    ),
                    PopupMenuItem(
                        label = "שתף",
                        icon = Icons.Rounded.Share,
                        onClick = {}
                    ),
                    PopupMenuItem(
                        label = "מחק",
                        icon = Icons.Rounded.Delete,
                        onClick = {}
                    )
                )
            )
        }
    }

    // Bottom Sheet
    ChampionBottomSheet(
        visible = showBottomSheet,
        onDismiss = { showBottomSheet = false },
        title = "אפשרויות מיון"
    ) {
        Column(
            modifier = Modifier.padding(Padding.l),
            verticalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            ChampionListItem(
                title = "מחיר - מהנמוך לגבוה",
                leadingIcon = Icons.Rounded.ArrowUpward,
                onClick = { showBottomSheet = false }
            )
            ChampionListItem(
                title = "מחיר - מהגבוה לנמוך",
                leadingIcon = Icons.Rounded.ArrowDownward,
                onClick = { showBottomSheet = false }
            )
            ChampionListItem(
                title = "מרחק",
                leadingIcon = Icons.Rounded.NearMe,
                onClick = { showBottomSheet = false }
            )
        }
    }

    // Dialog
    ChampionDialog(
        visible = showDialog,
        onDismiss = { showDialog = false },
        title = "על האפליקציה",
        text = "ChampionCart - האפליקציה שלך לחיסכון חכם בסופר",
        icon = Icons.Rounded.Info,
        confirmButton = {
            PrimaryButton(
                text = "סגור",
                onClick = { showDialog = false }
            )
        }
    )

    // Confirmation Dialog
    ConfirmationDialog(
        visible = showConfirmation,
        onConfirm = {},
        onDismiss = { showConfirmation = false },
        title = "מחיקת עגלה",
        text = "האם אתה בטוח שברצונך למחוק את העגלה? פעולה זו אינה ניתנת לביטול.",
        confirmText = "מחק",
        isDangerous = true
    )

    // Loading Overlay
    LoadingOverlay(
        visible = showLoading,
        message = "מחפש את המחירים הטובים ביותר..."
    )

    if (showLoading) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2000)
            showLoading = false
        }
    }
}

@Composable
private fun SpecializedComponents() {
    var quantity by remember { mutableStateOf(1) }
    var selectedCity by remember { mutableStateOf("תל אביב") }

    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.l)
    ) {
        // Quantity Selector
        SectionHeader(title = "בורר כמות")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            QuantitySelector(
                quantity = quantity,
                onQuantityChange = { quantity = it }
            )
        }

        // Cart Summary Card
        SectionHeader(title = "סיכום עגלה")

        CartSummaryCard(
            itemCount = 15,
            totalPrice = "₪250-320",
            savings = "₪45",
            onFindBestStore = {}
        )

        // Stat Cards
        SectionHeader(title = "כרטיסי סטטיסטיקה")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            StatCard(
                title = "חיסכון החודש",
                value = "₪458",
                icon = Icons.Rounded.Savings,
                trend = 12.5f,
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "מוצרים במעקב",
                value = "24",
                icon = Icons.Rounded.Visibility,
                modifier = Modifier.weight(1f)
            )
        }

        // Category Cards
        SectionHeader(title = "קטגוריות")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            CategoryCard(
                name = "חלב ומוצריו",
                icon = Icons.Rounded.LocalDrink,
                color = BrandColors.ElectricMint,
                onClick = {}
            )

            CategoryCard(
                name = "פירות וירקות",
                icon = Icons.Rounded.Eco,
                color = SemanticColors.Success,
                onClick = {}
            )

            CategoryCard(
                name = "מוצרי ניקיון",
                icon = Icons.Rounded.CleaningServices,
                color = BrandColors.CosmicPurple,
                onClick = {}
            )
        }

        // City Selector
        SectionHeader(title = "בורר עיר")

        CitySelector(
            selectedCity = selectedCity,
            cities = listOf("תל אביב", "ירושלים", "חיפה", "באר שבע", "נתניה"),
            onCitySelected = { selectedCity = it }
        )
    }
}