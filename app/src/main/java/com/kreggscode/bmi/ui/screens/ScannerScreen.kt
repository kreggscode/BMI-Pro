package com.kreggscode.bmi.ui.screens

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.kreggscode.bmi.data.database.BMIDatabase
import com.kreggscode.bmi.ui.components.FormattedAIText
import com.kreggscode.bmi.ui.components.GlassCard
import com.kreggscode.bmi.ui.components.LoadingAnimation
import com.kreggscode.bmi.ui.components.calculateDynamicBottomPadding
import com.kreggscode.bmi.ui.theme.*
import com.kreggscode.bmi.viewmodel.ScannerViewModel
import com.kreggscode.bmi.viewmodel.ScannerViewModelFactory

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen() {
    val context = LocalContext.current
    val database = remember { BMIDatabase.getDatabase(context) }
    val viewModel: ScannerViewModel = viewModel(
        factory = ScannerViewModelFactory(database.mealLogDao())
    )
    val dynamicBottomPadding = calculateDynamicBottomPadding()

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val analysisState by viewModel.analysisState.collectAsState()

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    if (cameraPermissionState.status.isGranted) {
        ScannerContent(
            viewModel = viewModel,
            analysisState = analysisState,
            dynamicBottomPadding = dynamicBottomPadding
        )
    } else {
        PermissionDeniedContent(
            onRequestPermission = { cameraPermissionState.launchPermissionRequest() }
        )
    }
}

@Composable
fun ScannerContent(
    viewModel: ScannerViewModel,
    analysisState: ScannerViewModel.AnalysisState,
    dynamicBottomPadding: androidx.compose.ui.unit.Dp
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (analysisState) {
            is ScannerViewModel.AnalysisState.Idle -> {
                CameraPreview(
                    cameraController = cameraController,
                    lifecycleOwner = lifecycleOwner,
                    onCapture = { bitmap ->
                        viewModel.analyzeFood(bitmap)
                    }
                )
            }
            is ScannerViewModel.AnalysisState.Loading -> {
                AnalysisLoadingScreen()
            }
            is ScannerViewModel.AnalysisState.Success -> {
                AnalysisResultScreen(
                    analysis = analysisState.analysis,
                    onClose = { viewModel.resetAnalysis() },
                    onSaveToLog = { foodName, calories, protein, carbs, fat ->
                        viewModel.saveMealLog(foodName, calories, protein, carbs, fat, "scanned", null)
                    }
                )
            }
            is ScannerViewModel.AnalysisState.Error -> {
                ErrorScreen(
                    message = analysisState.message,
                    onRetry = { viewModel.resetAnalysis() }
                )
            }
        }
    }
}

@Composable
fun CameraPreview(
    cameraController: LifecycleCameraController,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    onCapture: (Bitmap) -> Unit
) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    controller = cameraController
                    cameraController.bindToLifecycle(lifecycleOwner)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top instruction with proper spacing
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                Text(
                    text = "Point camera at food",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    text = "Get instant nutritional information",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            // Capture and Gallery buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 100.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery button
                val galleryLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri: Uri? ->
                    uri?.let {
                        try {
                            val inputStream = context.contentResolver.openInputStream(it)
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            inputStream?.close()
                            onCapture(bitmap)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.size(64.dp),
                    containerColor = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(AccentOrange, AccentYellow)
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "Gallery",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Camera capture button
                FloatingActionButton(
                    onClick = {
                        cameraController.takePicture(
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageCapturedCallback() {
                                override fun onCaptureSuccess(image: ImageProxy) {
                                    val bitmap = imageProxyToBitmap(image)
                                    image.close()
                                    onCapture(bitmap)
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    exception.printStackTrace()
                                }
                            }
                        )
                    },
                    modifier = Modifier.size(80.dp),
                    containerColor = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(AccentPurple, AccentPink)
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Capture",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnalysisLoadingScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "scanning")
    
    val scanLinePosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanLine"
    )
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Animated scanning frame
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .scale(pulseScale),
                contentAlignment = Alignment.Center
            ) {
                // Corner brackets
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cornerLength = 60f
                    val strokeWidth = 8f
                    
                    // Top-left corner
                    drawLine(
                        color = AccentTeal,
                        start = Offset(0f, 0f),
                        end = Offset(cornerLength, 0f),
                        strokeWidth = strokeWidth
                    )
                    drawLine(
                        color = AccentTeal,
                        start = Offset(0f, 0f),
                        end = Offset(0f, cornerLength),
                        strokeWidth = strokeWidth
                    )
                    
                    // Top-right corner
                    drawLine(
                        color = AccentTeal,
                        start = Offset(size.width, 0f),
                        end = Offset(size.width - cornerLength, 0f),
                        strokeWidth = strokeWidth
                    )
                    drawLine(
                        color = AccentTeal,
                        start = Offset(size.width, 0f),
                        end = Offset(size.width, cornerLength),
                        strokeWidth = strokeWidth
                    )
                    
                    // Bottom-left corner
                    drawLine(
                        color = AccentTeal,
                        start = Offset(0f, size.height),
                        end = Offset(cornerLength, size.height),
                        strokeWidth = strokeWidth
                    )
                    drawLine(
                        color = AccentTeal,
                        start = Offset(0f, size.height),
                        end = Offset(0f, size.height - cornerLength),
                        strokeWidth = strokeWidth
                    )
                    
                    // Bottom-right corner
                    drawLine(
                        color = AccentTeal,
                        start = Offset(size.width, size.height),
                        end = Offset(size.width - cornerLength, size.height),
                        strokeWidth = strokeWidth
                    )
                    drawLine(
                        color = AccentTeal,
                        start = Offset(size.width, size.height),
                        end = Offset(size.width, size.height - cornerLength),
                        strokeWidth = strokeWidth
                    )
                    
                    // Animated scan line
                    val scanY = size.height * scanLinePosition
                    drawLine(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                AccentPurple.copy(alpha = 0.8f),
                                AccentPink.copy(alpha = 0.8f),
                                Color.Transparent
                            )
                        ),
                        start = Offset(0f, scanY),
                        end = Offset(size.width, scanY),
                        strokeWidth = 4f
                    )
                }
                
                // Center icon
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = AccentPurple.copy(alpha = 0.6f)
                )
            }
            
            // Loading text with animation
            GlassCard(
                modifier = Modifier.padding(horizontal = 40.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LoadingAnimation(text = "Analyzing food...")
                    
                    Text(
                        text = "🔍 Detecting ingredients\n📊 Calculating nutrition\n✨ Powered by AI",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AnalysisResultScreen(
    analysis: String,
    onClose: () -> Unit,
    onSaveToLog: (String, Float, Float, Float, Float) -> Unit
) {
    val dynamicBottomPadding = calculateDynamicBottomPadding()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 60.dp, bottom = dynamicBottomPadding),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Food Analysis",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        // Parse nutritional values for display
        val lines = analysis.lines().filter { it.isNotBlank() }
        val caloriesMatch = Regex("(\\d+\\.?\\d*)\\s*(cal|kcal|calories)", RegexOption.IGNORE_CASE).find(analysis)
        val displayCalories = caloriesMatch?.groupValues?.get(1)?.toFloatOrNull() ?: 
                          Regex("calories?\\s*:?\\s*(\\d+\\.?\\d*)", RegexOption.IGNORE_CASE).find(analysis)?.groupValues?.get(1)?.toFloatOrNull() ?: 
                          250f
        
        val proteinMatch = Regex("(\\d+\\.?\\d*)\\s*g?\\s*(of\\s+)?protein", RegexOption.IGNORE_CASE).find(analysis)
        val displayProtein = proteinMatch?.groupValues?.get(1)?.toFloatOrNull() ?: 
                     Regex("protein\\s*:?\\s*(\\d+\\.?\\d*)", RegexOption.IGNORE_CASE).find(analysis)?.groupValues?.get(1)?.toFloatOrNull() ?: 
                     10f
        
        val carbsMatch = Regex("(\\d+\\.?\\d*)\\s*g?\\s*(of\\s+)?(carb|carbohydrate)", RegexOption.IGNORE_CASE).find(analysis)
        val displayCarbs = carbsMatch?.groupValues?.get(1)?.toFloatOrNull() ?: 
                   Regex("carb(ohydrate)?s?\\s*:?\\s*(\\d+\\.?\\d*)", RegexOption.IGNORE_CASE).find(analysis)?.groupValues?.get(1)?.toFloatOrNull() ?: 
                   30f
        
        val fatMatch = Regex("(\\d+\\.?\\d*)\\s*g?\\s*(of\\s+)?fat", RegexOption.IGNORE_CASE).find(analysis)
        val displayFat = fatMatch?.groupValues?.get(1)?.toFloatOrNull() ?: 
                 Regex("fat\\s*:?\\s*(\\d+\\.?\\d*)", RegexOption.IGNORE_CASE).find(analysis)?.groupValues?.get(1)?.toFloatOrNull() ?: 
                 8f
        
        // Nutritional Summary Card with Progress Circles
        item {
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Nutritional Summary",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        com.kreggscode.bmi.ui.components.AnimatedCircularProgress(
                            value = displayCalories,
                            maxValue = 500f,
                            label = "Calories",
                            colors = listOf(AccentOrange, AccentYellow),
                            size = 80.dp,
                            strokeWidth = 8.dp
                        )
                        com.kreggscode.bmi.ui.components.AnimatedCircularProgress(
                            value = displayProtein,
                            maxValue = 50f,
                            label = "Protein",
                            colors = listOf(AccentBlue, AccentTeal),
                            size = 80.dp,
                            strokeWidth = 8.dp
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        com.kreggscode.bmi.ui.components.AnimatedCircularProgress(
                            value = displayCarbs,
                            maxValue = 100f,
                            label = "Carbs",
                            colors = listOf(AccentGreen, AccentTeal),
                            size = 80.dp,
                            strokeWidth = 8.dp
                        )
                        com.kreggscode.bmi.ui.components.AnimatedCircularProgress(
                            value = displayFat,
                            maxValue = 30f,
                            label = "Fat",
                            colors = listOf(AccentRed, AccentOrange),
                            size = 80.dp,
                            strokeWidth = 8.dp
                        )
                    }
                }
            }
        }
        
        item {
            GlassCard {
                FormattedAIText(text = analysis)
            }
        }

        item {
            Button(
                onClick = {
                    // Parse analysis to extract nutritional values with multiple patterns
                    val lines = analysis.lines().filter { it.isNotBlank() }
                    
                    // Enhanced food name extraction - try multiple strategies
                    val foodName = run {
                        // NEW STRATEGY: AI now returns food name as first line
                        // Strategy 1: Get the first non-empty line (should be the food name)
                        val firstLine = lines.firstOrNull { it.isNotBlank() && it.length > 2 }?.trim()
                        
                        // Validate it's actually a food name (not a header or nutritional info)
                        if (firstLine != null && 
                            firstLine.length in 3..60 &&
                            !firstLine.contains(Regex("(calorie|protein|carb|fat|nutrition|analysis|portion|health|insight|food items|detected)", RegexOption.IGNORE_CASE)) &&
                            !firstLine.matches(Regex("^[A-Z\\s]+$")) && // Not all caps (not a header)
                            !firstLine.contains(":") && // No colons (not a label)
                            !firstLine.matches(Regex(".*\\d+.*g.*")) // Not nutritional info
                        ) {
                            firstLine
                        } else {
                            // Fallback: Look for lines that look like food names
                            lines.firstOrNull { line ->
                                line.matches(Regex("^[A-Z][a-zA-Z\\s&'-]+$")) && 
                                line.length in 3..60 &&
                                !line.contains(Regex("(calorie|protein|carb|fat|gram|serving|portion|health)", RegexOption.IGNORE_CASE))
                            }?.trim() ?: "Scanned Food"
                        }
                    }.take(60) // Limit length
                    
                    // Try multiple patterns for calories
                    val caloriesMatch = Regex("(\\d+\\.?\\d*)\\s*(cal|kcal|calories)", RegexOption.IGNORE_CASE).find(analysis)
                    val calories = caloriesMatch?.groupValues?.get(1)?.toFloatOrNull() ?: 
                                  Regex("calories?\\s*:?\\s*(\\d+\\.?\\d*)", RegexOption.IGNORE_CASE).find(analysis)?.groupValues?.get(1)?.toFloatOrNull() ?: 
                                  250f // Default estimate if not found
                    
                    // Protein patterns
                    val proteinMatch = Regex("(\\d+\\.?\\d*)\\s*g?\\s*(of\\s+)?protein", RegexOption.IGNORE_CASE).find(analysis)
                    val protein = proteinMatch?.groupValues?.get(1)?.toFloatOrNull() ?: 
                                 Regex("protein\\s*:?\\s*(\\d+\\.?\\d*)", RegexOption.IGNORE_CASE).find(analysis)?.groupValues?.get(1)?.toFloatOrNull() ?: 
                                 10f
                    
                    // Carbs patterns
                    val carbsMatch = Regex("(\\d+\\.?\\d*)\\s*g?\\s*(of\\s+)?(carb|carbohydrate)", RegexOption.IGNORE_CASE).find(analysis)
                    val carbs = carbsMatch?.groupValues?.get(1)?.toFloatOrNull() ?: 
                               Regex("carb(ohydrate)?s?\\s*:?\\s*(\\d+\\.?\\d*)", RegexOption.IGNORE_CASE).find(analysis)?.groupValues?.get(1)?.toFloatOrNull() ?: 
                               30f
                    
                    // Fat patterns
                    val fatMatch = Regex("(\\d+\\.?\\d*)\\s*g?\\s*(of\\s+)?fat", RegexOption.IGNORE_CASE).find(analysis)
                    val fat = fatMatch?.groupValues?.get(1)?.toFloatOrNull() ?: 
                             Regex("fat\\s*:?\\s*(\\d+\\.?\\d*)", RegexOption.IGNORE_CASE).find(analysis)?.groupValues?.get(1)?.toFloatOrNull() ?: 
                             8f
                    
                    onSaveToLog(foodName, calories, protein, carbs, fat)
                    onClose()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(AccentGreen, AccentTeal)
                            ),
                            shape = RoundedCornerShape(28.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Save to Meal Log",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            modifier = Modifier.padding(40.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Error",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentRed
                )
                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Button(onClick = onRetry) {
                    Text("Try Again")
                }
            }
        }
    }
}

@Composable
fun PermissionDeniedContent(
    onRequestPermission: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            modifier = Modifier.padding(40.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Camera Permission Required",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Please grant camera permission to scan food",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Button(onClick = onRequestPermission) {
                    Text("Grant Permission")
                }
            }
        }
    }
}

fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    val buffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    
    // Rotate bitmap if needed
    val matrix = Matrix()
    matrix.postRotate(image.imageInfo.rotationDegrees.toFloat())
    
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

