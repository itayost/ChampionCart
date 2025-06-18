package com.example.championcart.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.championcart.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.tooling.preview.Preview as Preview

/**
 * City information data class
 * Parses from server format: "Tel Aviv: 45 shufersal, 12 victory"
 */
data class CityInfo(
    val name: String,
    val nameHebrew: String,
    val totalStores: Int,
    val storeBreakdown: Map<String, Int>,
    val isPopular: Boolean = false
)

/**
 * Modern city selector with glassmorphic bottom sheet
 * Features search, recent cities, and store count visualization
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun CitySelector(
    currentCity: String,
    cities: List<String>, // Raw format from server
    onCitySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    recentCities: List<String> = emptyList()
) {
    var isExpanded by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current

    // Parse city data
    val parsedCities = remember(cities) {
        cities.map { parseCityString(it) }
            .sortedByDescending { it.totalStores }
    }

    // Current city info
    val currentCityInfo = remember(currentCity, parsedCities) {
        parsedCities.find { it.name.equals(currentCity, ignoreCase = true) }
            ?: CityInfo(currentCity, translateCityName(currentCity), 0, emptyMap())
    }

    Column(modifier = modifier) {
        // Compact city display
        CitySelectorButton(
            cityInfo = currentCityInfo,
            isExpanded = isExpanded,
            isLoading = isLoading,
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                isExpanded = true
            }
        )

        // City selection dialog
        if (isExpanded) {
            CitySelectionDialog(
                cities = parsedCities,
                currentCity = currentCity,
                recentCities = recentCities,
                onCitySelected = { city ->
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onCitySelected(city)
                    isExpanded = false
                },
                onDismiss = { isExpanded = false }
            )
        }
    }
}

@ExperimentalAnimationApi
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun CitySelectorButton(
    cityInfo: CityInfo,
    isExpanded: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = SpringSpecs.Bouncy,
        label = "arrowRotation"
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = GlassmorphicShapes.GlassCard,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
                .padding(horizontal = SpacingTokens.L, vertical = SpacingTokens.M)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Location icon and city info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
                ) {
                    // Animated location icon
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.1f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.extended.electricMint,
                            modifier = Modifier
                                .size(24.dp)
                                .animateContentSize()
                        )
                    }

                    Column {
                        Text(
                            text = cityInfo.nameHebrew,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        AnimatedContent(
                            targetState = isLoading,
                            transitionSpec = {
                                fadeIn() with fadeOut()
                            },
                            label = "storeCountAnimation"
                        ) { loading ->
                            if (loading) {
                                LoadingDots()
                            } else {
                                Text(
                                    text = "${cityInfo.totalStores} חנויות זמינות",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }

                // Dropdown arrow
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "Select city",
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotation),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CitySelectionDialog(
    cities: List<CityInfo>,
    currentCity: String,
    recentCities: List<String>,
    onCitySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    // Filter cities based on search
    val filteredCities = remember(searchQuery, cities) {
        if (searchQuery.isEmpty()) cities
        else cities.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.nameHebrew.contains(searchQuery, ignoreCase = true)
        }
    }

    // Group cities
    val popularCities = remember(cities) {
        listOf("Tel Aviv", "Jerusalem", "Haifa", "Beer Sheva", "Rishon LeZion")
            .mapNotNull { cityName -> cities.find { it.name.equals(cityName, ignoreCase = true) } }
    }

    val recentCityInfos = remember(recentCities, cities) {
        recentCities.mapNotNull { cityName ->
            cities.find { it.name.equals(cityName, ignoreCase = true) }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(horizontal = SpacingTokens.L),
            shape = GlassmorphicShapes.GlassCardLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(SpacingTokens.L)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "בחר עיר",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(onClick = onDismiss) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Search bar
                SearchCityBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    focusRequester = focusRequester,
                    modifier = Modifier.padding(horizontal = SpacingTokens.L)
                )

                Spacer(modifier = Modifier.height(SpacingTokens.M))

                // City list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(
                        horizontal = SpacingTokens.L,
                        vertical = SpacingTokens.M
                    ),
                    verticalArrangement = Arrangement.spacedBy(SpacingTokens.S)
                ) {
                    // Recent cities section
                    if (recentCityInfos.isNotEmpty() && searchQuery.isEmpty()) {
                        item {
                            SectionHeader(title = "ערים אחרונות", icon = Icons.Default.History)
                        }

                        items(recentCityInfos) { city ->
                            CityItem(
                                cityInfo = city,
                                isSelected = city.name.equals(currentCity, ignoreCase = true),
                                isRecent = true,
                                onClick = { onCitySelected(city.name) }
                            )
                        }

                        item { Spacer(modifier = Modifier.height(SpacingTokens.M)) }
                    }

                    // Popular cities section
                    if (searchQuery.isEmpty()) {
                        item {
                            SectionHeader(title = "ערים פופולריות", icon = Icons.Default.Star)
                        }

                        items(popularCities) { city ->
                            CityItem(
                                cityInfo = city,
                                isSelected = city.name.equals(currentCity, ignoreCase = true),
                                isPopular = true,
                                onClick = { onCitySelected(city.name) }
                            )
                        }

                        item { Spacer(modifier = Modifier.height(SpacingTokens.M)) }
                    }

                    // All cities section
                    item {
                        SectionHeader(
                            title = if (searchQuery.isEmpty()) "כל הערים" else "תוצאות חיפוש",
                            icon = if (searchQuery.isEmpty()) Icons.Default.LocationCity else Icons.Default.Search
                        )
                    }

                    items(filteredCities) { city ->
                        CityItem(
                            cityInfo = city,
                            isSelected = city.name.equals(currentCity, ignoreCase = true),
                            onClick = { onCitySelected(city.name) }
                        )
                    }

                    // No results
                    if (filteredCities.isEmpty()) {
                        item {
                            EmptyCitySearch(query = searchQuery)
                        }
                    }
                }
            }
        }
    }

    // Request focus on search field
    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }
}

@Composable
private fun SearchCityBar(
    query: String,
    onQueryChange: (String) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(GlassmorphicShapes.SearchField)
            .background(MaterialTheme.colorScheme.extended.surfaceGlass)
            .padding(horizontal = SpacingTokens.L)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.extended.electricMint,
                modifier = Modifier.size(24.dp)
            )

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.extended.electricMint),
                decorationBox = { innerTextField ->
                    Box {
                        if (query.isEmpty()) {
                            Text(
                                text = "חפש עיר...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                        innerTextField()
                    }
                }
            )

            // Clear button
            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SpacingTokens.S),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.extended.cosmicPurple,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CityItem(
    cityInfo: CityInfo,
    onClick: () -> Unit,
    isSelected: Boolean,
    isRecent: Boolean = false,
    isPopular: Boolean = false,
    modifier: Modifier = Modifier // It's good practice for all Composables to accept a modifier
) {
    // Derived UI properties based on selection state
    val targetBackgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.extended.electricMint.copy(alpha = 0.15f)
    } else {
        Color.Transparent
    }
    val targetScale = if (isSelected) 1.02f else 1f
    val cardElevation = if (isSelected) 2.dp else 0.dp
    val cityTextFontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
    val badgeBackgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.extended.electricMint
    } else {
        MaterialTheme.colorScheme.extended.surfaceGlass
    }
    val badgeTextColor = if (isSelected) {
        MaterialTheme.colorScheme.extended.deepNavy
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    // Animations
    val animatedBackgroundColor by animateColorAsState(
        targetValue = targetBackgroundColor,
        animationSpec = spring(
            dampingRatio = SpringSpecs.DampingRatioLowBounce,
            stiffness = SpringSpecs.StiffnessMedium
        ),
        label = "cityItemBg"
    )

    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = SpringSpecs.Snappy, // Assuming SpringSpecs is defined elsewhere
        label = "cityItemScale"
    )

    Card(
        onClick = onClick,
        modifier = modifier // Apply the passed-in modifier
            .fillMaxWidth()
            .scale(animatedScale),
        shape = GlassmorphicShapes.GlassCardSmall, // Assuming GlassmorphicShapes is defined
        colors = CardDefaults.cardColors(containerColor = animatedBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacingTokens.L), // Assuming SpacingTokens is defined
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // City info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SpacingTokens.S)
                ) {
                    Text(
                        text = cityInfo.nameHebrew, // Assuming CityInfo has nameHebrew
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = cityTextFontWeight
                    )

                    // Tags
                    if (isPopular) {
                        CityTag(
                            text = "פופולרי",
                            color = MaterialTheme.colorScheme.extended.cosmicPurple
                        )
                    }
                    if (isRecent) {
                        CityTag(
                            text = "אחרון",
                            color = MaterialTheme.colorScheme.extended.electricMint
                        )
                    }
                }

                Spacer(modifier = Modifier.height(SpacingTokens.XS)) // Added a small spacer for better visual separation

                // Store breakdown
                StoreBreakdown(cityInfo.storeBreakdown) // Assuming CityInfo has storeBreakdown
            }

            // Total stores badge
            Box(
                modifier = Modifier
                    .clip(GlassmorphicShapes.BottomSheet) // Assuming GlassmorphicShapes is defined
                    .background(badgeBackgroundColor)
                    .padding(horizontal = SpacingTokens.M, vertical = SpacingTokens.S)
            ) {
                Text(
                    text = cityInfo.totalStores.toString(), // Assuming CityInfo has totalStores
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = badgeTextColor
                )
            }
        }
    }
}

@Composable
private fun CityTag(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = SpacingTokens.S, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun StoreBreakdown(breakdown: Map<String, Int>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(SpacingTokens.M)
    ) {
        breakdown.forEach { (chain, count) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Chain icon
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(getChainColor(chain).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = chain.first().uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = getChainColor(chain)
                    )
                }

                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun EmptyCitySearch(query: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SpacingTokens.XL),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(SpacingTokens.M))

        Text(
            text = "לא נמצאו תוצאות עבור \"$query\"",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LoadingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(3) { index ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1000
                        0.3f at 0 with LinearEasing
                        1f at 300 + (index * 100) with LinearEasing
                        0.3f at 600 + (index * 100) with LinearEasing
                    },
                    repeatMode = RepeatMode.Restart
                ),
                label = "dot_$index"
            )

            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.extended.electricMint.copy(alpha = alpha)
                    )
            )
        }
    }
}

// Helper functions
private fun parseCityString(cityString: String): CityInfo {
    // Parse format: "Tel Aviv: 45 shufersal, 12 victory"
    val parts = cityString.split(":")
    val cityName = parts[0].trim()

    val storeBreakdown = mutableMapOf<String, Int>()
    var totalStores = 0

    if (parts.size > 1) {
        val storeParts = parts[1].trim().split(",")
        storeParts.forEach { storePart ->
            val storeInfo = storePart.trim().split(" ")
            if (storeInfo.size >= 2) {
                val count = storeInfo[0].toIntOrNull() ?: 0
                val chain = storeInfo[1]
                storeBreakdown[chain] = count
                totalStores += count
            }
        }
    }

    return CityInfo(
        name = cityName,
        nameHebrew = translateCityName(cityName),
        totalStores = totalStores,
        storeBreakdown = storeBreakdown
    )
}

private fun translateCityName(englishName: String): String {
    return when (englishName.lowercase()) {
        "tel aviv" -> "תל אביב"
        "jerusalem" -> "ירושלים"
        "haifa" -> "חיפה"
        "beer sheva", "beersheva" -> "באר שבע"
        "rishon lezion" -> "ראשון לציון"
        "petah tikva" -> "פתח תקווה"
        "ashdod" -> "אשדוד"
        "netanya" -> "נתניה"
        "holon" -> "חולון"
        "bnei brak" -> "בני ברק"
        "ramat gan" -> "רמת גן"
        "ashkelon" -> "אשקלון"
        "rehovot" -> "רחובות"
        "bat yam" -> "בת ים"
        "herzliya" -> "הרצליה"
        else -> englishName // Return as-is if translation not found
    }
}

private fun getChainColor(chain: String): Color {
    return when (chain.lowercase()) {
        "shufersal" -> Color(0xFF0056B3) // Shufersal blue
        "victory" -> Color(0xFFE31E24) // Victory red
        "rami levy" -> Color(0xFFFFD700) // Rami Levy yellow
        "mega" -> Color(0xFF006400) // Mega green
        else -> BrandColors.CosmicPurple
    }
}