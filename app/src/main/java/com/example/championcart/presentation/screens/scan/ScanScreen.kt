package com.example.championcart.presentation.screens.scan

import android.Manifest
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.championcart.presentation.components.common.*
import com.example.championcart.ui.theme.*
import com.example.championcart.ui.theme.Size
import com.google.accompanist.permissions.*
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class, ExperimentalGetImage::class)
@Composable
fun ScanScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit,
    viewModel: ScanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Show product details bottom sheet
    var showProductDetails by remember { mutableStateOf(false) }

    LaunchedEffect(cameraPermissionState.status) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    // Handle scan result
    LaunchedEffect(uiState.scannedProduct) {
        if (uiState.scannedProduct != null) {
            showProductDetails = true
        }
    }

    Scaffold(
        topBar = {
            ScanTopBar(
                onNavigateBack = onNavigateBack,
                onToggleFlash = viewModel::toggleFlash,
                isFlashOn = uiState.isFlashOn,
                onManualEntry = { viewModel.showManualEntry() }
            )
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
        ) {
            when {
                cameraPermissionState.status.isGranted -> {
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

                    // Recent Scans
                    if (uiState.recentScans.isNotEmpty()) {
                        RecentScansCard(
                            recentScans = uiState.recentScans,
                            onScanClick = { scan ->
                                viewModel.processBarcode(scan.barcode)
                            },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(Spacing.l)
                        )
                    }
                }

                cameraPermissionState.status.shouldShowRationale -> {
                    PermissionRationaleScreen(
                        onRequestPermission = {
                            cameraPermissionState.launchPermissionRequest()
                        }
                    )
                }

                else -> {
                    PermissionDeniedScreen(
                        onNavigateBack = onNavigateBack
                    )
                }
            }

            // Loading overlay
            if (uiState.isProcessing) {
                LoadingOverlay(
                    visible = true,
                    message = "מחפש מוצר..."
                )
            }
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

    // Error handling
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
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
    val lifecycleOwner = LocalLifecycleOwner.current

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
        modifier = Modifier.fillMaxSize()
    ) { previewView ->
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                        if (!isPaused) {
                            processImageProxy(
                                imageProxy = imageProxy,
                                barcodeScanner = barcodeScanner,
                                onBarcodeDetected = onBarcodeScanned
                            )
                        } else {
                            imageProxy.close()
                        }
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )

                // Update flash state
                camera?.cameraControl?.enableTorch(isFlashOn)
            } catch (e: Exception) {
                // Handle camera binding error
            }
        }, ContextCompat.getMainExecutor(context))
    }
}

@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    imageProxy: ImageProxy,
    barcodeScanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    onBarcodeDetected: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                barcodes.firstOrNull()?.rawValue?.let { barcode ->
                    onBarcodeDetected(barcode)
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScanTopBar(
    onNavigateBack: () -> Unit,
    onToggleFlash: () -> Unit,
    isFlashOn: Boolean,
    onManualEntry: () -> Unit
) {
    Surface(
        color = Color.Black.copy(alpha = 0.7f),
        modifier = Modifier.fillMaxWidth()
    ) {
        ChampionTopBar(
            title = "סרוק מוצר",
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "חזור",
                        tint = Color.White
                    )
                }
            },
            actions = {
                IconButton(onClick = onToggleFlash) {
                    Icon(
                        imageVector = if (isFlashOn) Icons.Rounded.FlashOn else Icons.Rounded.FlashOff,
                        contentDescription = if (isFlashOn) "כבה פלאש" else "הדלק פלאש",
                        tint = if (isFlashOn) BrandColors.ElectricMint else Color.White
                    )
                }
                IconButton(onClick = onManualEntry) {
                    Icon(
                        imageVector = Icons.Rounded.Keyboard,
                        contentDescription = "הקלדה ידנית",
                        tint = Color.White
                    )
                }
            }
        )
    }
}

@Composable
private fun ScanningOverlay(
    isScanning: Boolean,
    lastScannedBarcode: String?
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scanLinePosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Scanning frame
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(RoundedCornerShape(Spacing.xl))
                .border(
                    width = 3.dp,
                    color = if (isScanning) BrandColors.ElectricMint else SemanticColors.Warning,
                    shape = RoundedCornerShape(Spacing.xl)
                )
                .drawBehind {
                    // Corner highlights
                    val cornerLength = 40.dp.toPx()
                    val strokeWidth = 4.dp.toPx()

                    drawPath(
                        path = Path().apply {
                            // Top-left corner
                            moveTo(0f, cornerLength)
                            lineTo(0f, 0f)
                            lineTo(cornerLength, 0f)
                        },
                        color = BrandColors.ElectricMint,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Top-right corner
                    drawPath(
                        path = Path().apply {
                            moveTo(size.width - cornerLength, 0f)
                            lineTo(size.width, 0f)
                            lineTo(size.width, cornerLength)
                        },
                        color = BrandColors.ElectricMint,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Bottom-left corner
                    drawPath(
                        path = Path().apply {
                            moveTo(0f, size.height - cornerLength)
                            lineTo(0f, size.height)
                            lineTo(cornerLength, size.height)
                        },
                        color = BrandColors.ElectricMint,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Bottom-right corner
                    drawPath(
                        path = Path().apply {
                            moveTo(size.width - cornerLength, size.height)
                            lineTo(size.width, size.height)
                            lineTo(size.width, size.height - cornerLength)
                        },
                        color = BrandColors.ElectricMint,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

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
        ) {
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
}

@Composable
private fun RecentScansCard(
    recentScans: List<RecentScan>,
    onScanClick: (RecentScan) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Black.copy(alpha = 0.5f),
        shape = RoundedCornerShape(Spacing.m)
    ) {
        Column(
            modifier = Modifier.padding(Padding.m)
        ) {
            Text(
                text = "סריקות אחרונות",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(Spacing.s))

            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                recentScans.take(3).forEach { scan ->
                    RecentScanChip(
                        scan = scan,
                        onClick = { onScanClick(scan) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentScanChip(
    scan: RecentScan,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(Spacing.m),
        color = BrandColors.ElectricMint.copy(alpha = 0.2f),
        border = BorderStroke(1.dp, BrandColors.ElectricMint.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = Spacing.m,
                vertical = Spacing.s
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Icon(
                imageVector = Icons.Rounded.History,
                contentDescription = null,
                modifier = Modifier.size(Size.icon),
                tint = Color.White
            )
            Text(
                text = scan.productName,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDetailsSheet(
    visible: Boolean,
    product: ScannedProduct?,
    onDismiss: () -> Unit,
    onAddToCart: (ScannedProduct) -> Unit,
    onViewDetails: (ScannedProduct) -> Unit
) {
    ChampionBottomSheet(
        visible = visible,
        onDismiss = onDismiss
    ) {
        product?.let {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Padding.l),
                verticalArrangement = Arrangement.spacedBy(Spacing.m)
            ) {
                // Product Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = product.barcode,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (product.imageUrl != null) {
                        // Product image placeholder
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(Spacing.m))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }
                }

                ChampionDivider()

                // Price Summary
                if (product.priceRange != null) {
                    PriceSummaryCard(
                        minPrice = product.priceRange.min,
                        maxPrice = product.priceRange.max,
                        avgPrice = product.priceRange.avg,
                        availableStores = product.availableInStores
                    )
                }

                // Action Buttons
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
private fun PriceSummaryCard(
    minPrice: Double,
    maxPrice: Double,
    avgPrice: Double,
    availableStores: Int
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Padding.m),
            verticalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "טווח מחירים",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "נמצא ב-$availableStores חנויות",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PriceItem(
                    label = "מינימום",
                    price = "₪%.2f".format(minPrice),
                    color = PriceColors.Best
                )
                PriceItem(
                    label = "ממוצע",
                    price = "₪%.2f".format(avgPrice),
                    color = PriceColors.Mid
                )
                PriceItem(
                    label = "מקסימום",
                    price = "₪%.2f".format(maxPrice),
                    color = PriceColors.High
                )
            }

            if (maxPrice - minPrice > 0) {
                val savingsPotential = ((maxPrice - minPrice) / maxPrice) * 100
                Text(
                    text = "פוטנציאל חיסכון: %.0f%%".format(savingsPotential),
                    style = MaterialTheme.typography.labelMedium,
                    color = SemanticColors.Success,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun PriceItem(
    label: String,
    price: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = price,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun ManualBarcodeDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    if (visible) {
        var barcode by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.m),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Keyboard,
                        contentDescription = null,
                        tint = BrandColors.ElectricMint,
                        modifier = Modifier.size(Size.icon)
                    )
                    Text(
                        text = "הקלד ברקוד",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.m)
                ) {
                    Text(
                        text = "הקלד את מספר הברקוד של המוצר",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    ChampionTextField(
                        value = barcode,
                        onValueChange = { barcode = it.filter { char -> char.isDigit() } },
                        label = "מספר ברקוד",
                        placeholder = "הקלד 13 ספרות",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                PrimaryButton(
                    text = "חפש",
                    onClick = {
                        if (barcode.isNotBlank()) {
                            onSubmit(barcode)
                            onDismiss()
                        }
                    },
                    enabled = barcode.isNotBlank()
                )
            },
            dismissButton = {
                TextButton(
                    text = "ביטול",
                    onClick = onDismiss
                )
            },
            shape = Shapes.cardLarge,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        )
    }
}

@Composable
private fun PermissionRationaleScreen(
    onRequestPermission: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.l),
            modifier = Modifier.padding(Padding.xl)
        ) {
            Icon(
                imageVector = Icons.Rounded.CameraAlt,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = BrandColors.ElectricMint
            )

            Text(
                text = "נדרשת הרשאת מצלמה",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "כדי לסרוק ברקודים של מוצרים, האפליקציה צריכה גישה למצלמה שלך",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.l))

            PrimaryButton(
                text = "אשר הרשאה",
                onClick = onRequestPermission,
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Rounded.Check
            )
        }
    }
}

@Composable
private fun PermissionDeniedScreen(
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.l),
            modifier = Modifier.padding(Padding.xl)
        ) {
            Icon(
                imageVector = Icons.Rounded.CameraAlt,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.error
            )

            Text(
                text = "אין גישה למצלמה",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "לא ניתן לסרוק ברקודים ללא גישה למצלמה. ניתן לאשר את ההרשאה בהגדרות המכשיר",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.l))

            SecondaryButton(
                text = "חזור",
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Rounded.ArrowBack
            )
        }
    }
}

// Data classes for scan screen
data class RecentScan(
    val barcode: String,
    val productName: String,
    val timestamp: Long
)

data class ScannedProduct(
    val id: String,
    val barcode: String,
    val name: String,
    val imageUrl: String? = null,
    val priceRange: PriceRange? = null,
    val availableInStores: Int = 0
)

data class PriceRange(
    val min: Double,
    val max: Double,
    val avg: Double
)