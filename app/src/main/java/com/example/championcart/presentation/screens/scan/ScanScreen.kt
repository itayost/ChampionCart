package com.example.championcart.presentation.screens.scan

import android.Manifest
import android.content.Context
import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size as ComposeSize
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.championcart.presentation.components.common.*
import com.example.championcart.ui.theme.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
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
            TopAppBar(
                title = { Text("סרוק מוצר") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "חזור")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleFlash() },
                        enabled = cameraPermissionState.status.isGranted
                    ) {
                        Icon(
                            imageVector = if (uiState.isFlashOn) Icons.Rounded.FlashOn else Icons.Rounded.FlashOff,
                            contentDescription = if (uiState.isFlashOn) "כבה פלאש" else "הפעל פלאש"
                        )
                    }
                    IconButton(onClick = { viewModel.showManualEntry() }) {
                        Icon(Icons.Rounded.Keyboard, contentDescription = "הקלדה ידנית")
                    }
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
                                .padding(bottom = com.example.championcart.ui.theme.Size.bottomNavHeight )
                        )
                    }
                }

                cameraPermissionState.status.shouldShowRationale -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = com.example.championcart.ui.theme.Size.bottomNavHeight) // Account for nav bar
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
                            .padding(bottom = com.example.championcart.ui.theme.Size.bottomNavHeight) // Account for nav bar
                    ) {
                        PermissionDeniedScreen(
                            onNavigateBack = onNavigateBack
                        )
                    }
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
        modifier = Modifier.fillMaxSize(),
        update = { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setTargetResolution(Size(1280, 720))
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
            // Top left
            drawPath(
                path = Path().apply {
                    moveTo(0f, cornerLength)
                    lineTo(0f, 0f)
                    lineTo(cornerLength, 0f)
                },
                color = BrandColors.ElectricMint,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Top right
            drawPath(
                path = Path().apply {
                    moveTo(size.width - cornerLength, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width, cornerLength)
                },
                color = BrandColors.ElectricMint,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Bottom left
            drawPath(
                path = Path().apply {
                    moveTo(0f, size.height - cornerLength)
                    lineTo(0f, size.height)
                    lineTo(cornerLength, size.height)
                },
                color = BrandColors.ElectricMint,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Bottom right
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDetailsSheet(
    visible: Boolean,
    product: ScannedProduct?,
    onDismiss: () -> Unit,
    onAddToCart: (ScannedProduct) -> Unit,
    onViewDetails: (ScannedProduct) -> Unit
) {
    if (visible && product != null) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.l)
                    .navigationBarsPadding()
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

                // Product name
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(Spacing.m))

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
                                text = "זמין ב-",
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
    if (visible) {
        var barcode by remember { mutableStateOf("") }

        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.l),
                shape = Shapes.card,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.xl)
                ) {
                    Text(
                        text = "הקלד ברקוד",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(Spacing.l))

                    OutlinedTextField(
                        value = barcode,
                        onValueChange = { barcode = it.filter { char -> char.isDigit() } },
                        label = { Text("ברקוד") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(Spacing.xl))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.m)
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("ביטול")
                        }

                        PrimaryButton(
                            text = "חפש",
                            onClick = {
                                if (barcode.isNotBlank()) {
                                    onSubmit(barcode)
                                    onDismiss()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = barcode.isNotBlank()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductNotAvailableDialog(
    visible: Boolean,
    currentCity: String,
    onDismiss: () -> Unit,
    onChangeCity: () -> Unit,
    onTryAgain: () -> Unit
) {
    if (visible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.l),
                shape = Shapes.card,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.xl),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icon
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                color = SemanticColors.Warning.copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.LocationOff,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = SemanticColors.Warning
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.l))

                    // Title
                    Text(
                        text = "המוצר לא זמין",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(Spacing.m))

                    // Message
                    Text(
                        text = "המוצר לא נמצא ב$currentCity",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(Spacing.xl))

                    // Actions
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(Spacing.m)
                    ) {
                        // Change city button
                        PrimaryButton(
                            text = "שנה עיר",
                            onClick = onChangeCity,
                            modifier = Modifier.fillMaxWidth(),
                            icon = Icons.Rounded.LocationOn
                        )

                        // Try again button
                        SecondaryButton(
                            text = "נסה שוב",
                            onClick = onTryAgain,
                            modifier = Modifier.fillMaxWidth(),
                            icon = Icons.Rounded.Refresh
                        )

                        // Cancel button
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "ביטול",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionRationaleScreen(
    onRequestPermission: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(Spacing.xl),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.CameraAlt,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(Spacing.l))

            Text(
                text = "נדרשת הרשאת מצלמה",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.m))

            Text(
                text = "כדי לסרוק ברקודים של מוצרים, האפליקציה זקוקה לגישה למצלמה",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

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
            .background(MaterialTheme.colorScheme.background)
            .padding(Spacing.xl),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Block,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = SemanticColors.Error
            )

            Spacer(modifier = Modifier.height(Spacing.l))

            Text(
                text = "אין הרשאת מצלמה",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.m))

            Text(
                text = "ללא הרשאת מצלמה לא ניתן לסרוק ברקודים.\nניתן לאשר את ההרשאה בהגדרות המכשיר",
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