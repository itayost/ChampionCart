package com.example.championcart.presentation.screens.scan

import android.Manifest
import android.content.Context
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AddShoppingCart
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.FlashOff
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material.icons.rounded.LocationOff
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.championcart.presentation.components.common.ChampionBottomSheet
import com.example.championcart.presentation.components.common.ChampionCartTopBar
import com.example.championcart.presentation.components.common.ChampionDialog
import com.example.championcart.presentation.components.common.EmptyState
import com.example.championcart.presentation.components.common.ErrorState
import com.example.championcart.presentation.components.common.LoadingOverlay
import com.example.championcart.presentation.components.common.PrimaryButton
import com.example.championcart.presentation.components.common.SecondaryButton
import com.example.championcart.presentation.components.common.TextInputDialog
import com.example.championcart.presentation.components.common.TopBarAction
import com.example.championcart.ui.theme.BrandColors
import com.example.championcart.ui.theme.Shapes
import com.example.championcart.ui.theme.Size
import com.example.championcart.ui.theme.Spacing
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: ScanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var showProductDetails by remember { mutableStateOf(false) }

    // Camera permission
    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )

    // Handle product found
    LaunchedEffect(uiState.scannedProduct) {
        if (uiState.scannedProduct != null) {
            showProductDetails = true
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            ChampionCartTopBar(
                title = "סרוק מוצר",
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "חזור")
                    }
                },
                actions = listOf(
                    TopBarAction(
                        icon = Icons.Rounded.Keyboard,
                        contentDescription = "הקלדה ידנית",
                        onClick = { viewModel.showManualEntry() }
                    ),
                    TopBarAction(
                        icon = if (uiState.isFlashOn) Icons.Rounded.FlashOn else Icons.Rounded.FlashOff,
                        contentDescription = if (uiState.isFlashOn) "כבה פלאש" else "הפעל פלאש",
                        onClick = { viewModel.toggleFlash() },
                        tint = if (!cameraPermissionState.status.isGranted)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        else null
                    )
                ),
                showTimeBasedGradient = false, // Keep it simple for scan screen
                elevation = 2.dp
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
        ) {
            when {
                cameraPermissionState.status.isGranted -> {
                    // Camera preview fills entire screen
                    CameraPreviewWithScanner(
                        onBarcodeScanned = { barcode ->
                            viewModel.processBarcode(barcode)
                        },
                        isFlashOn = uiState.isFlashOn,
                        isPaused = uiState.isProcessing
                    )

                    // Scanning UI Overlay
                    ScanningOverlay(
                        isScanning = !uiState.isProcessing,
                        lastScannedBarcode = uiState.lastScannedBarcode
                    )

                    // Recent Scans - positioned above bottom nav
                    if (uiState.recentScans.isNotEmpty()) {
                        RecentScansCard(
                            recentScans = uiState.recentScans,
                            onScanClick = { scan ->
                                viewModel.processBarcode(scan.barcode)
                            },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = Spacing.l)
                                .padding(bottom = Size.bottomNavHeight)
                        )
                    }
                }

                cameraPermissionState.status.shouldShowRationale -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = Size.bottomNavHeight)
                    ) {
                        PermissionRationaleScreen(
                            onRequestPermission = {
                                cameraPermissionState.launchPermissionRequest()
                            }
                        )
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = Size.bottomNavHeight)
                    ) {
                        PermissionDeniedScreen(
                            onNavigateBack = onNavigateBack
                        )
                    }
                }
            }

            // Loading overlay
            LoadingOverlay(
                visible = uiState.isProcessing,
                message = "מחפש מוצר..."
            )
        }
    }

    // Product Details Bottom Sheet
    ProductDetailsSheet(
        visible = showProductDetails,
        product = uiState.scannedProduct,
        onDismiss = {
            showProductDetails = false
            viewModel.clearScannedProduct()
        },
        onAddToCart = { product ->
            viewModel.addToCart(product)
            scope.launch {
                snackbarHostState.showSnackbar("המוצר נוסף לעגלה")
            }
        },
        onViewDetails = { product ->
            onNavigateToProduct(product.id)
        }
    )

    // Manual Entry Dialog
    ManualBarcodeDialog(
        visible = uiState.showManualEntry,
        onDismiss = { viewModel.hideManualEntry() },
        onSubmit = { barcode ->
            viewModel.processBarcode(barcode)
        }
    )

    // Product not available in city dialog
    val currentCity = remember {
        context.getSharedPreferences("champion_cart_prefs", Context.MODE_PRIVATE)
            .getString("selected_city", "תל אביב") ?: "תל אביב"
    }

    ProductNotAvailableDialog(
        visible = uiState.showCityChangeOption,
        currentCity = currentCity,
        onDismiss = { viewModel.clearError() },
        onChangeCity = {
            viewModel.clearError()
            onNavigateToSettings()
        },
        onTryAgain = {
            viewModel.clearError()
            viewModel.clearScannedProduct()
        }
    )

    // Error handling
    uiState.error?.let { error ->
        if (!uiState.showCityChangeOption) {
            LaunchedEffect(error) {
                val actionLabel = when {
                    error.contains("לא זמין") -> "שנה עיר"
                    error.contains("לא נמצא") -> "חפש ידנית"
                    else -> null
                }

                val result = snackbarHostState.showSnackbar(
                    message = error,
                    actionLabel = actionLabel,
                    duration = SnackbarDuration.Long
                )

                when (result) {
                    SnackbarResult.ActionPerformed -> {
                        when {
                            error.contains("לא זמין") -> onNavigateToSettings()
                            error.contains("לא נמצא") -> viewModel.showManualEntry()
                        }
                    }
                    SnackbarResult.Dismissed -> {
                        viewModel.clearError()
                    }
                }
            }
        }
    }
}

@Composable
private fun CameraPreviewWithScanner(
    onBarcodeScanned: (String) -> Unit,
    isFlashOn: Boolean,
    isPaused: Boolean
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val barcodeScanner = remember { BarcodeScanning.getClient() }

    var camera: Camera? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
            barcodeScanner.close()
        }
    }

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                            if (!isPaused) {
                                processImageProxy(
                                    barcodeScanner,
                                    imageProxy,
                                    onBarcodeScanned
                                )
                            } else {
                                imageProxy.close()
                            }
                        }
                    }

                try {
                    cameraProvider.unbindAll()

                    camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalyzer
                    )

                    // Set flash mode
                    camera?.cameraControl?.enableTorch(isFlashOn)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}

private fun processImageProxy(
    barcodeScanner: BarcodeScanner,
    imageProxy: ImageProxy,
    onBarcodeScanned: (String) -> Unit
) {
    imageProxy.image?.let { mediaImage ->
        val image = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                barcodes.firstOrNull()?.let { barcode ->
                    barcode.rawValue?.let { value ->
                        onBarcodeScanned(value)
                    }
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}

@Composable
private fun ScanningOverlay(
    isScanning: Boolean,
    lastScannedBarcode: String?
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val scanLinePosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanLine"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Scanning frame
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.xl)
        ) {
            val strokeWidth = 4.dp.toPx()
            val cornerLength = 50.dp.toPx()

            // Draw corner brackets
            val corners = listOf(
                // Top left
                Path().apply {
                    moveTo(0f, cornerLength)
                    lineTo(0f, 0f)
                    lineTo(cornerLength, 0f)
                },
                // Top right
                Path().apply {
                    moveTo(size.width - cornerLength, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width, cornerLength)
                },
                // Bottom left
                Path().apply {
                    moveTo(0f, size.height - cornerLength)
                    lineTo(0f, size.height)
                    lineTo(cornerLength, size.height)
                },
                // Bottom right
                Path().apply {
                    moveTo(size.width - cornerLength, size.height)
                    lineTo(size.width, size.height)
                    lineTo(size.width, size.height - cornerLength)
                }
            )

            corners.forEach { path ->
                drawPath(
                    path = path,
                    color = BrandColors.ElectricMint,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // Scanning line
            if (isScanning) {
                val lineY = size.height * scanLinePosition
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            BrandColors.ElectricMint,
                            BrandColors.ElectricMint,
                            Color.Transparent
                        )
                    ),
                    start = Offset(0f, lineY),
                    end = Offset(size.width, lineY),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }

        // Instructions
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(Spacing.l),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.QrCodeScanner,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.White.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(Spacing.m))

            Text(
                text = if (isScanning) "יישר את הברקוד במסגרת" else "מעבד...",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            lastScannedBarcode?.let {
                Spacer(modifier = Modifier.height(Spacing.s))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun RecentScansCard(
    recentScans: List<RecentScan>,
    onScanClick: (RecentScan) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = Shapes.card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(Spacing.m)
        ) {
            Text(
                text = "סריקות אחרונות",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.s))

            recentScans.forEach { scan ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(Shapes.cardSmall)
                        .clickable { onScanClick(scan) }
                        .padding(Spacing.s),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = scan.productName,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = formatTimestamp(scan.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductDetailsSheet(
    visible: Boolean,
    product: ScannedProduct?,
    onDismiss: () -> Unit,
    onAddToCart: (ScannedProduct) -> Unit,
    onViewDetails: (ScannedProduct) -> Unit
) {
    if (product != null) {
        ChampionBottomSheet(
            visible = visible,
            onDismiss = onDismiss,
            title = product.name
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.l, vertical = Spacing.m)
            ) {
                // Product image
                product.imageUrl?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(Shapes.card),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(Spacing.l))
                }

                // Price range
                product.priceRange?.let { range ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "טווח מחירים",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "₪${range.min} - ₪${range.max}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "זמין ב",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${product.availableInStores} חנויות",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xl))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.m)
                ) {
                    SecondaryButton(
                        text = "פרטים נוספים",
                        onClick = { onViewDetails(product) },
                        modifier = Modifier.weight(1f),
                        icon = Icons.Rounded.Info
                    )

                    PrimaryButton(
                        text = "הוסף לעגלה",
                        onClick = { onAddToCart(product) },
                        modifier = Modifier.weight(1f),
                        icon = Icons.Rounded.AddShoppingCart
                    )
                }
            }
        }
    }
}

@Composable
private fun ManualBarcodeDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    TextInputDialog(
        visible = visible,
        onDismiss = onDismiss,
        onConfirm = { barcode ->
            onSubmit(barcode)
            onDismiss()
        },
        title = "הקלד ברקוד",
        description = "הכנס את מספר הברקוד של המוצר",
        icon = Icons.Rounded.QrCodeScanner,
        label = "ברקוד",
        placeholder = "למשל: 7290000000000",
        validator = { value ->
            when {
                value.isBlank() -> "יש להזין ברקוד"
                !value.all { it.isDigit() } -> "ברקוד חייב להכיל ספרות בלבד"
                value.length < 8 -> "ברקוד קצר מדי"
                value.length > 13 -> "ברקוד ארוך מדי"
                else -> null
            }
        },
        confirmText = "חפש",
        dismissText = "ביטול"
    )
}

@Composable
private fun ProductNotAvailableDialog(
    visible: Boolean,
    currentCity: String,
    onDismiss: () -> Unit,
    onChangeCity: () -> Unit,
    onTryAgain: () -> Unit
) {
    ChampionDialog(
        visible = visible,
        onDismiss = onDismiss,
        title = "המוצר לא זמין",
        text = "המוצר לא נמצא ב$currentCity",
        icon = Icons.Rounded.LocationOff,
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                PrimaryButton(
                    text = "שנה עיר",
                    onClick = {
                        onChangeCity()
                        onDismiss()
                    },
                    icon = Icons.Rounded.LocationOn,
                    modifier = Modifier.fillMaxWidth()
                )

                SecondaryButton(
                    text = "נסה שוב",
                    onClick = {
                        onTryAgain()
                        onDismiss()
                    },
                    icon = Icons.Rounded.Refresh,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("ביטול")
            }
        }
    )
}

@Composable
private fun PermissionRationaleScreen(
    onRequestPermission: () -> Unit
) {
    EmptyState(
        icon = Icons.Rounded.CameraAlt,
        title = "נדרשת הרשאת מצלמה",
        message = "כדי לסרוק ברקודים של מוצרים, האפליקציה זקוקה לגישה למצלמה",
        actionText = "אשר הרשאה",
        onAction = onRequestPermission,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun PermissionDeniedScreen(
    onNavigateBack: () -> Unit
) {
    ErrorState(
        title = "אין הרשאת מצלמה",
        message = "ללא הרשאת מצלמה לא ניתן לסרוק ברקודים.\nניתן לאשר את ההרשאה בהגדרות המכשיר",
        actionText = "חזור",
        onAction = onNavigateBack,
        modifier = Modifier.fillMaxSize()
    )
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60_000 -> "כרגע"
        diff < 3_600_000 -> "${diff / 60_000} דקות"
        diff < 86_400_000 -> "${diff / 3_600_000} שעות"
        else -> SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(timestamp))
    }
}