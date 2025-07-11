package com.example.championcart.presentation.screens.info

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.championcart.presentation.components.common.GlassCard
import com.example.championcart.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsOfServiceScreen(
    navController: NavController
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "תנאי השירות",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "חזרה",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                BrandColors.ElectricMint.copy(alpha = 0.2f),
                                BrandColors.ElectricMint.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .padding(vertical = Spacing.xl),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "תנאי השירות",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(Spacing.s))
                    Text(
                        text = "עדכון אחרון: ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Content Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.l)
            ) {
                // Introduction
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.l)
                    ) {
                        Text(
                            text = "ברוכים הבאים ל-ChampionCart",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(Spacing.m))
                        Text(
                            text = "תנאי השירות הבאים (\"התנאים\") מסדירים את השימוש שלך באפליקציית ChampionCart (\"האפליקציה\"). על ידי שימוש באפליקציה, אתה מסכים לתנאים אלה.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.l))

                // Section 1: Service Description
                TermsSection(
                    title = "1. תיאור השירות",
                    content = listOf(
                        "ChampionCart היא אפליקציית השוואת מחירים המאפשרת לך להשוות מחירי מוצרים בין רשתות שיווק שונות בישראל.",
                        "האפליקציה מספקת מידע על מחירים, יצירת רשימות קניות, וחישוב הסל הזול ביותר.",
                        "המידע באפליקציה מתעדכן באופן שוטף אך איננו מתחייבים לדיוק מוחלט של המחירים."
                    )
                )

                Spacer(modifier = Modifier.height(Spacing.l))

                // Section 2: User Account
                TermsSection(
                    title = "2. חשבון משתמש",
                    content = listOf(
                        "ניתן להשתמש באפליקציה כאורח או ליצור חשבון משתמש.",
                        "אתה אחראי לשמור על סודיות פרטי ההתחברות שלך.",
                        "אתה מתחייב לספק מידע מדויק ועדכני בעת ההרשמה.",
                        "אנו רשאים להשעות או למחוק חשבונות המפרים את תנאי השירות."
                    )
                )

                Spacer(modifier = Modifier.height(Spacing.l))

                // Section 3: Usage Terms
                TermsSection(
                    title = "3. תנאי שימוש",
                    content = listOf(
                        "השימוש באפליקציה מותר למטרות אישיות בלבד.",
                        "אסור להשתמש באפליקציה למטרות מסחריות ללא אישור מראש.",
                        "אסור לבצע הנדסה לאחור, לפרוץ או לנסות לגשת למערכות שלנו.",
                        "אסור להעתיק, לשכפל או להפיץ תוכן מהאפליקציה ללא אישור."
                    )
                )

                Spacer(modifier = Modifier.height(Spacing.l))

                // Section 4: Privacy
                TermsSection(
                    title = "4. פרטיות",
                    content = listOf(
                        "השימוש במידע האישי שלך כפוף למדיניות הפרטיות שלנו.",
                        "אנו מתחייבים לשמור על פרטיותך ולא למכור את המידע שלך לצדדים שלישיים.",
                        "ייתכן שנשתמש במידע מצרפי ואנונימי לשיפור השירות."
                    )
                )

                Spacer(modifier = Modifier.height(Spacing.l))

                // Section 5: Disclaimer
                TermsSection(
                    title = "5. הגבלת אחריות",
                    content = listOf(
                        "המידע באפליקציה ניתן כ\"AS IS\" ללא אחריות מכל סוג.",
                        "איננו אחראים לנזקים ישירים או עקיפים הנובעים משימוש באפליקציה.",
                        "המחירים המוצגים עשויים להשתנות ואיננו מתחייבים לדיוקם המוחלט.",
                        "ההחלטה הסופית לגבי רכישות היא באחריותך בלבד."
                    )
                )

                Spacer(modifier = Modifier.height(Spacing.l))

                // Section 6: Changes to Terms
                TermsSection(
                    title = "6. שינויים בתנאי השירות",
                    content = listOf(
                        "אנו רשאים לעדכן את תנאי השירות מעת לעת.",
                        "שינויים משמעותיים יפורסמו באפליקציה.",
                        "המשך השימוש באפליקציה לאחר השינויים מהווה הסכמה לתנאים המעודכנים."
                    )
                )

                Spacer(modifier = Modifier.height(Spacing.l))

                // Section 7: Contact
                TermsSection(
                    title = "7. יצירת קשר",
                    content = listOf(
                        "לשאלות בנוגע לתנאי השירות ניתן לפנות אלינו בכתובת:",
                        "support@championcart.co.il",
                        "נשמח לסייע בכל שאלה או בקשה."
                    )
                )

                Spacer(modifier = Modifier.height(Spacing.xl))
            }
        }
    }
}

@Composable
private fun TermsSection(
    title: String,
    content: List<String>
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Spacing.l)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(Spacing.m))
            content.forEach { item ->
                Row(
                    modifier = Modifier.padding(vertical = Spacing.xs)
                ) {
                    Text(
                        text = "• ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}