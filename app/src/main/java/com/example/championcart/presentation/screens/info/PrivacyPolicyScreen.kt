package com.example.championcart.presentation.screens.info

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.championcart.presentation.components.common.GlassCard
import com.example.championcart.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    navController: NavController
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "מדיניות פרטיות",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
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
                                BrandColors.CosmicPurple.copy(alpha = 0.2f),
                                BrandColors.CosmicPurple.copy(alpha = 0.05f)
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
                        text = "מדיניות פרטיות",
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
                            text = "הפרטיות שלך חשובה לנו",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(Spacing.m))
                        Text(
                            text = "מדיניות פרטיות זו מסבירה כיצד סל ניצחון (\"אנחנו\", \"שלנו\") אוספת, משתמשת ומגנה על המידע האישי שלך כאשר אתה משתמש באפליקציה שלנו.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.l))

                // Section 1: Information Collection
                PrivacySection(
                    title = "1. איזה מידע אנחנו אוספים",
                    subsections = listOf(
                        PrivacySubsection(
                            title = "מידע שאתה מספק לנו:",
                            items = listOf(
                                "כתובת דוא\"ל בעת ההרשמה",
                                "שם משתמש (אופציונלי)",
                                "העיר שבה אתה מתגורר",
                                "רשימות קניות שאתה יוצר"
                            )
                        ),
                        PrivacySubsection(
                            title = "מידע שנאסף אוטומטית:",
                            items = listOf(
                                "נתוני שימוש באפליקציה",
                                "העדפות שפה ותצוגה",
                                "מידע טכני על המכשיר (סוג מכשיר, גרסת מערכת הפעלה)"
                            )
                        )
                    )
                )

                Spacer(modifier = Modifier.height(Spacing.l))

                // Section 2: How We Use Information
                PrivacySection(
                    title = "2. כיצד אנחנו משתמשים במידע",
                    subsections = listOf(
                        PrivacySubsection(
                            title = "אנחנו משתמשים במידע שלך כדי:",
                            items = listOf(
                                "לספק ולשפר את שירותי האפליקציה",
                                "להתאים אישית את החוויה שלך",
                                "לשמור את רשימות הקניות שלך",
                                "לחשב את הסל הזול ביותר עבורך",
                                "לשלוח עדכונים חשובים (אם הסכמת)"
                            )
                        )
                    )
                )

                Spacer(modifier = Modifier.height(Spacing.l))

                // Section 3: Data Sharing
                PrivacySection(
                    title = "3. שיתוף מידע",
                    subsections = listOf(
                        PrivacySubsection(
                            title = "אנחנו לא מוכרים או משתפים את המידע האישי שלך עם:",
                            items = listOf(
                                "חברות פרסום",
                                "צדדים שלישיים למטרות שיווק",
                                "כל גורם אחר ללא הסכמתך המפורשת"
                            )
                        ),
                        PrivacySubsection(
                            title = "אנחנו עשויים לשתף מידע במקרים הבאים:",
                            items = listOf(
                                "כאשר נדרש על פי חוק",
                                "להגנה על זכויותינו החוקיות",
                                "עם ספקי שירות המסייעים בתפעול האפליקציה"
                            )
                        )
                    )
                )

                Spacer(modifier = Modifier.height(Spacing.l))

                // Section 4: Data Security
                PrivacySection(
                    title = "4. אבטחת מידע",
                    subsections = listOf(
                        PrivacySubsection(
                            title = "אמצעי האבטחה שלנו כוללים:",
                            items = listOf(
                                "הצפנת נתונים בהעברה ובאחסון",
                                "גישה מוגבלת למידע אישי",
                                "עדכוני אבטחה שוטפים",
                                "ניטור פעילות חריגה"
                            )
                        )
                    )
                )

                Spacer(modifier = Modifier.height(Spacing.l))

                // Section 5: User Rights
                PrivacySection(
                    title = "5. הזכויות שלך",
                    subsections = listOf(
                        PrivacySubsection(
                            title = "יש לך זכות:",
                            items = listOf(
                                "לגשת למידע האישי שלך",
                                "לתקן או לעדכן את המידע שלך",
                                "למחוק את החשבון שלך",
                                "להתנגד לעיבוד מידע מסוים",
                                "לקבל העתק של המידע שלך"
                            )
                        )
                    )
                )

                Spacer(modifier = Modifier.height(Spacing.l))

                // Section 6: Data Retention
                PrivacySection(
                    title = "6. שמירת מידע",
                    subsections = listOf(
                        PrivacySubsection(
                            title = "תקופות שמירת מידע:",
                            items = listOf(
                                "מידע חשבון: כל עוד החשבון פעיל",
                                "רשימות קניות: עד 6 חודשים מהשימוש האחרון",
                                "נתוני שימוש אנונימיים: עד שנתיים",
                                "מידע שנמחק: עד 30 יום בגיבויים"
                            )
                        )
                    )
                )

                Spacer(modifier = Modifier.height(Spacing.l))

                // Section 7: Children's Privacy
                PrivacySection(
                    title = "7. פרטיות ילדים",
                    subsections = listOf(
                        PrivacySubsection(
                            title = "",
                            items = listOf(
                                "האפליקציה מיועדת לגילאי 13 ומעלה",
                                "איננו אוספים מידע מילדים מתחת לגיל 13 ביודעין",
                                "אם גילינו שאספנו מידע מילד מתחת לגיל 13, נמחק אותו מיידית"
                            )
                        )
                    )
                )

                Spacer(modifier = Modifier.height(Spacing.l))

                // Section 8: Changes to Policy
                PrivacySection(
                    title = "8. שינויים במדיניות",
                    subsections = listOf(
                        PrivacySubsection(
                            title = "",
                            items = listOf(
                                "אנו עשויים לעדכן מדיניות זו מעת לעת",
                                "נודיע לך על שינויים משמעותיים באפליקציה או בדוא\"ל",
                                "תאריך העדכון האחרון יופיע בראש המסמך"
                            )
                        )
                    )
                )

                Spacer(modifier = Modifier.height(Spacing.l))

                // Contact Section
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.l),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "יש לך שאלות?",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(Spacing.m))
                        Text(
                            text = "צור איתנו קשר בכתובת:",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(Spacing.s))
                        Text(
                            text = "privacy@championcart.co.il",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xl))
            }
        }
    }
}

@Composable
private fun PrivacySection(
    title: String,
    subsections: List<PrivacySubsection>
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

            subsections.forEach { subsection ->
                if (subsection.title.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(Spacing.m))
                    Text(
                        text = subsection.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(Spacing.s))
                } else {
                    Spacer(modifier = Modifier.height(Spacing.m))
                }

                subsection.items.forEach { item ->
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
}

private data class PrivacySubsection(
    val title: String,
    val items: List<String>
)