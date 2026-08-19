package com.hapataka.questwalk.feature.camera

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hapataka.questwalk.core.ui.LocalPaddingValues
import com.hapataka.questwalk.feature.camera.component.CameraPermissionDeniedDialog
import com.hapataka.questwalk.feature.camera.component.CameraPermissionDialog
import com.hapataka.questwalk.feature.camera.component.CropFrame
import android.media.MediaActionSound
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors

private val Yellow = Color(0xFFFFFF00)

@Composable
internal fun CameraRoute(
    padding: PaddingValues = LocalPaddingValues.current,
    onBackClick: () -> Unit,
    onQuestSuccess: () -> Unit,
    onQuestFailed: (message: String) -> Unit,
    viewModel: CameraViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is CameraEvent.QuestSuccess -> onQuestSuccess()
                is CameraEvent.QuestFailed -> onQuestFailed(event.message)
            }
        }
    }

    CameraScreen(
        uiState = uiState,
        padding = padding,
        onBackClick = onBackClick,
        onPhotoTaken = viewModel::processPhoto,
        onFlashToggle = viewModel::toggleFlash,
        onCapturingChange = viewModel::setCapturing,
        onCapturedPhotoChange = viewModel::setCapturedPhoto,
        onFailureMessageDismiss = viewModel::clearFailure,
    )
}

@Composable
private fun CameraScreen(
    uiState: CameraUiState,
    padding: PaddingValues,
    onBackClick: () -> Unit,
    onPhotoTaken: (String) -> Unit,
    onFlashToggle: () -> Unit,
    onCapturingChange: (Boolean) -> Unit,
    onCapturedPhotoChange: (ByteArray?) -> Unit,
    onFailureMessageDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showPermissionDeniedDialog by remember { mutableStateOf(false) }
    var permissionRequested by remember { mutableStateOf(false) }

    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setJpegQuality(95)
            .build()
    }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    var previewView by remember { mutableStateOf<PreviewView?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted && permissionRequested) {
            showPermissionDeniedDialog = true
        }
    }

    // Check permission on launch
    LaunchedEffect(Unit) {
        hasCameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (!hasCameraPermission) {
            showPermissionDialog = true
        }
    }

    // Update flash mode (torch for real-time, flashMode for capture)
    LaunchedEffect(uiState.isFlashOn, camera) {
        camera?.cameraControl?.enableTorch(uiState.isFlashOn)
        imageCapture.flashMode = if (uiState.isFlashOn) {
            ImageCapture.FLASH_MODE_ON
        } else {
            ImageCapture.FLASH_MODE_OFF
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            camera?.cameraControl?.enableTorch(false)
            cameraProvider?.unbindAll()
        }
    }

    if (showPermissionDialog) {
        CameraPermissionDialog(
            onDismiss = {
                showPermissionDialog = false
                onBackClick()
            },
            onConfirm = {
                showPermissionDialog = false
                permissionRequested = true
                permissionLauncher.launch(Manifest.permission.CAMERA)
            },
        )
    }

    if (showPermissionDeniedDialog) {
        CameraPermissionDeniedDialog(
            onDismiss = {
                showPermissionDeniedDialog = false
                onBackClick()
            },
            onGoToSettings = {
                showPermissionDeniedDialog = false
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
                onBackClick()
            },
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        val showCapturedPhoto = (uiState.isCapturing || uiState.isProcessing) && uiState.capturedPhotoBytes != null

        if (hasCameraPermission) {
            // Camera Preview - 항상 렌더링 (제거되지 않음)
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }.also { previewView = it }
                },
                modifier = Modifier.fillMaxSize(),
                update = { pv ->
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                    cameraProviderFuture.addListener({
                        val provider = cameraProviderFuture.get()
                        cameraProvider = provider

                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(pv.surfaceProvider)
                        }

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        try {
                            provider.unbindAll()
                            camera = provider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageCapture,
                            )
                        } catch (e: Exception) {
                            Log.e("CameraScreen", "Camera binding failed", e)
                        }
                    }, ContextCompat.getMainExecutor(context))
                },
            )

            // Crop Frame Overlay (캡처된 사진이 없을 때만 표시)
            if (!showCapturedPhoto) {
                CropFrame()
            }

            // 캡처된 사진 오버레이 (카메라 프리뷰 위에 표시)
            if (showCapturedPhoto) {
                uiState.capturedPhotoBytes?.let { capturedPhotoBytes ->
                    val bitmap = remember(capturedPhotoBytes) {
                        BitmapFactory.decodeByteArray(
                            capturedPhotoBytes,
                            0,
                            capturedPhotoBytes.size
                        )
                    }
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Captured Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
            }

            // Top Bar (Back + Flash buttons)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = padding.calculateTopPadding() + 16.dp,
                        start = 10.dp,
                        end = 10.dp,
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Back button
                Image(
                    painter = painterResource(id = R.drawable.btn_back_shadow),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onBackClick() },
                )

                // Flash button
                Image(
                    painter = painterResource(
                        id = if (uiState.isFlashOn) R.drawable.btn_flash_on else R.drawable.btn_flash
                    ),
                    contentDescription = if (uiState.isFlashOn) "Flash On" else "Flash Off",
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onFlashToggle() },
                )
            }

            // Keyword and Guide text (positioned relative to crop frame)
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
                val frameRatio = 0.75f
                val frameSize = maxWidth * frameRatio
                val frameTop = (maxHeight - frameSize) / 2
                val frameBottom = frameTop + frameSize
                val textSpacing = 16.dp

                // Keyword - above crop frame
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(frameTop - textSpacing)
                        .align(Alignment.TopCenter),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    Text(
                        text = uiState.keyword,
                        color = Yellow,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                }

                // Guide text - below crop frame
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = frameBottom + textSpacing)
                        .align(Alignment.TopCenter),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    Text(
                        text = "사각형에 맞춰 키워드를 찍어주세요",
                        color = Yellow,
                        fontSize = 14.sp,
                    )
                }

                uiState.failureMessage?.let { message ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = frameBottom + 52.dp,
                                start = 24.dp,
                                end = 24.dp,
                            )
                            .align(Alignment.TopCenter)
                            .background(
                                color = Color.Black.copy(alpha = 0.72f),
                                shape = RoundedCornerShape(8.dp),
                            )
                            .clickable(onClick = onFailureMessageDismiss)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = message,
                            color = Yellow,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            // 촬영 중 또는 처리 중일 때 로딩 오버레이 (사진 위에 표시)
            if (showCapturedPhoto) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(60.dp),
                            color = Yellow,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "확인 중...",
                            color = Color.White,
                            fontSize = 16.sp,
                        )
                    }
                }
            }

            // Bottom Controls (Capture button)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = padding.calculateBottomPadding() + 24.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (uiState.isCapturing || uiState.isProcessing) {
                    // 촬영 중 또는 처리 중에는 로딩 스피너 표시 (사진이 없을 때만 하단에 표시)
                    if (uiState.capturedPhotoBytes == null) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(80.dp),
                            color = Yellow,
                        )
                    }
                } else {
                    CaptureButton(
                        onClick = {
                            // 1. 즉시 프리뷰 화면 캡처하여 표시 (instant feedback)
                            // 크롭 없이 그대로 표시 (화면 점프 방지)
                            val previewBitmap = previewView?.bitmap
                            val previewBytes = if (previewBitmap != null) {
                                val previewStream = ByteArrayOutputStream()
                                previewBitmap.compress(Bitmap.CompressFormat.JPEG, 80, previewStream)
                                previewStream.toByteArray()
                            } else null

                            // isCapturing 먼저 설정 후 이미지 설정 (순서 중요)
                            onCapturingChange(true)
                            onCapturedPhotoChange(previewBytes)

                            // 2. 실제 고화질 촬영은 백그라운드에서 진행 (OCR용 크롭은 여기서)
                            capturePhoto(
                                context = context,
                                imageCapture = imageCapture,
                                onPhotoTaken = { filePath ->
                                    // 상태 리셋하지 않음 - processPhoto에서 처리
                                    // isCapturing, capturedPhotoBytes 유지 → 로딩 오버레이 계속 표시
                                    onPhotoTaken(filePath)
                                },
                                onError = {
                                    // 촬영 에러 시에만 즉시 리셋
                                    onCapturingChange(false)
                                    onCapturedPhotoChange(null)
                                },
                            )
                        },
                    )
                }
            }
        } else {
            // Permission not granted placeholder
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "카메라 권한이 필요합니다",
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
private fun CaptureButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Image(
        painter = painterResource(
            id = if (isPressed) R.drawable.btn_capture_click else R.drawable.btn_capture
        ),
        contentDescription = "Capture",
        modifier = modifier
            .size(80.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
    )
}

private fun capturePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onPhotoTaken: (String) -> Unit,
    onError: () -> Unit,
) {
    val executor = Executors.newSingleThreadExecutor()
    val photoFile = File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg")

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    // 촬영음 재생
    val sound = MediaActionSound()
    sound.play(MediaActionSound.SHUTTER_CLICK)

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                try {
                    // 1. 이미지 크기 확인 (디코딩 없이)
                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    BitmapFactory.decodeFile(photoFile.absolutePath, options)

                    // 2. 샘플 사이즈 계산 (타겟: 1536px) - ML Kit OCR에 고해상도 불필요
                    val targetSize = 1536
                    options.inSampleSize = calculateInSampleSize(options, targetSize, targetSize)
                    options.inJustDecodeBounds = false

                    // 3. 다운샘플링된 비트맵 디코딩
                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath, options)

                    val rotatedBitmap = rotateBitmapIfRequired(bitmap, photoFile.absolutePath)
                    val croppedBitmap = cropToSquare(rotatedBitmap)

                    // 4. 처리된 이미지를 파일로 저장
                    val processedFile = File(context.cacheDir, "processed_${System.currentTimeMillis()}.jpg")
                    FileOutputStream(processedFile).use { fos ->
                        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
                    }

                    // Cleanup
                    photoFile.delete()
                    bitmap.recycle()
                    if (rotatedBitmap != bitmap) rotatedBitmap.recycle()
                    croppedBitmap.recycle()

                    // 처리 완료 후 파일 경로 콜백
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        onPhotoTaken(processedFile.absolutePath)
                    }
                } catch (e: Exception) {
                    Log.e("CameraScreen", "Photo processing failed", e)
                    photoFile.delete()
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        onError()
                    }
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraScreen", "Photo capture failed", exception)
                photoFile.delete()
                android.os.Handler(android.os.Looper.getMainLooper()).post {
                    onError()
                }
            }
        },
    )
}

/**
 * 이미지 다운샘플링을 위한 inSampleSize 계산
 * 12MP+ 고해상도 이미지를 1536x1536 이하로 축소하여 처리 성능 향상
 */
private fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int,
    reqHeight: Int
): Int {
    val (height, width) = options.outHeight to options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2

        // inSampleSize는 2의 거듭제곱이어야 효율적
        while ((halfHeight / inSampleSize) >= reqHeight &&
            (halfWidth / inSampleSize) >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}

private fun rotateBitmapIfRequired(bitmap: Bitmap, imagePath: String): Bitmap {
    val exif = ExifInterface(imagePath)
    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )

    val rotationDegrees = when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
        else -> return bitmap
    }

    val matrix = Matrix().apply {
        postRotate(rotationDegrees)
    }

    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

private fun cropToSquare(bitmap: Bitmap): Bitmap {
    val size = minOf(bitmap.width, bitmap.height)
    val x = (bitmap.width - size) / 2
    val y = (bitmap.height - size) / 2

    // Apply 75% crop frame ratio
    val frameSize = (size * 0.75f).toInt()
    val frameX = x + (size - frameSize) / 2
    val frameY = y + (size - frameSize) / 2

    return Bitmap.createBitmap(bitmap, frameX, frameY, frameSize, frameSize)
}
