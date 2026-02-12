package com.example.navya.ui.components

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.PixelCopy
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.navya.ui.screens.market.MarketViewModel
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.math.Scale
import io.github.sceneview.math.Size
import io.github.sceneview.node.ImageNode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PlantARView(
        navController: NavController,
        plantImageUrl: String,
        viewModel: MarketViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val marketState by viewModel.uiState.collectAsState()

    var currentPlantUrl by remember { mutableStateOf(plantImageUrl) }

    LaunchedEffect(marketState.plants) {
        if (currentPlantUrl.isEmpty() && marketState.plants.isNotEmpty()) {
            val tulsi = marketState.plants.find { it.name.contains("Tulsi", ignoreCase = true) }
            if (tulsi != null && !tulsi.image_url.isNullOrEmpty()) {
                currentPlantUrl = tulsi.image_url
            } else {
                marketState.plants.firstOrNull()?.image_url?.let { currentPlantUrl = it }
            }
        }
    }

    var scaleFactor by remember { mutableStateOf(1f) }

    var hasCameraPermission by remember {
        mutableStateOf(
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher =
            rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                hasCameraPermission = isGranted
                if (!isGranted) {
                    Toast.makeText(context, "Camera permission needed for AR", Toast.LENGTH_LONG)
                            .show()
                }
            }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    if (!hasCameraPermission) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Camera permission is required for AR")
                Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                    Text("Grant Permission")
                }
            }
        }
        return
    }

    var plantBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var lastAnchorNodeRef by remember { mutableStateOf<AnchorNode?>(null) }
    var arSceneView: ARSceneView? by remember { mutableStateOf(null) }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            try {
                arSceneView?.destroy()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            arSceneView = null
        }
    }

    LaunchedEffect(currentPlantUrl) {
        isLoading = true

        try {
            withContext(Dispatchers.IO) {
                val loader = ImageLoader(context)
                val builder =
                        ImageRequest.Builder(context).data(currentPlantUrl).allowHardware(false)

                if (currentPlantUrl.contains("supabase")) {
                    builder.addHeader(
                            "apikey",
                            com.example.navya.utils.SupabaseConfig.SUPABASE_ANON_KEY
                    )
                    builder.addHeader(
                            "Authorization",
                            "Bearer ${com.example.navya.utils.SupabaseConfig.SUPABASE_ANON_KEY}"
                    )
                }

                val request = builder.build()

                val result = loader.execute(request)
                if (result is SuccessResult) {

                    val drawable = result.drawable
                    plantBitmap =
                            if (drawable is android.graphics.drawable.BitmapDrawable) {
                                drawable.bitmap
                            } else {

                                null
                            }

                    if (plantBitmap != null) {
                        withContext(Dispatchers.Main) {
                            arSceneView?.let { view ->
                                lastAnchorNodeRef?.let { anchorNode ->
                                    anchorNode.childNodes.toList().forEach {
                                        anchorNode.removeChildNode(it)
                                    }

                                    val bitmap = plantBitmap!!
                                    val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
                                    val heightMeters = 0.4f
                                    val widthMeters = heightMeters * ratio
                                    val scaledHalfHeight = (heightMeters * scaleFactor) / 2f

                                    val imageNode =
                                            ImageNode(
                                                            materialLoader = view.materialLoader,
                                                            bitmap = bitmap,
                                                            size = Size(widthMeters, heightMeters)
                                                    )
                                                    .apply {
                                                        position =
                                                                Position(0f, scaledHalfHeight, 0f)
                                                        isTouchable = false
                                                        worldScale =
                                                                Scale(
                                                                        scaleFactor,
                                                                        scaleFactor,
                                                                        scaleFactor
                                                                )
                                                        rotation = Rotation(0f, 0f, 0f)
                                                    }
                                    anchorNode.addChildNode(imageNode)
                                }
                            }
                        }
                    }
                } else {

                    if (result is coil.request.ErrorResult) {

                        result.throwable.printStackTrace()
                    }
                    plantBitmap = null
                }
            }
        } catch (e: Exception) {

            e.printStackTrace()
            plantBitmap = null
        } finally {
            isLoading = false
        }
    }

    var instructionText by remember { mutableStateOf("Point camera at a surface") }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    ARSceneView(ctx).apply {
                        this.lifecycle = lifecycleOwner.lifecycle
                        arSceneView = this
                        configureSession { session, config ->
                            config.planeFindingMode =
                                    Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
                            config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP

                            if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                                config.depthMode = Config.DepthMode.AUTOMATIC
                            }
                            config.lightEstimationMode =
                                    Config.LightEstimationMode.ENVIRONMENTAL_HDR
                            config.focusMode = Config.FocusMode.AUTO
                        }

                        planeRenderer.isEnabled = true

                        var latestFrame: Frame? = null

                        onSessionUpdated = { session, frame ->
                            latestFrame = frame

                            Handler(Looper.getMainLooper()).post {
                                val isTracking =
                                        frame.camera.trackingState ==
                                                com.google.ar.core.TrackingState.TRACKING
                                val hasPlanes =
                                        session.getAllTrackables(
                                                        com.google.ar.core.Plane::class.java
                                                )
                                                .any {
                                                    it.trackingState ==
                                                            com.google.ar.core.TrackingState
                                                                    .TRACKING
                                                }

                                val newInstruction =
                                        when {
                                            plantBitmap == null -> ""
                                            !isTracking -> "Point camera at a surface"
                                            !hasPlanes -> "Move phone slowly to scan surroundings"
                                            lastAnchorNodeRef == null ->
                                                    "Tap on dots to place plant"
                                            else -> "Drag to move • Use +/- to scale"
                                        }

                                if (instructionText != newInstruction) {
                                    instructionText = newInstruction
                                }

                                lastAnchorNodeRef?.let { anchor ->
                                    val node = anchor.childNodes.firstOrNull()
                                    node?.let { imageNode ->
                                        val cameraPose = frame.camera.pose
                                        val cameraPosition =
                                                Position(
                                                        cameraPose.tx(),
                                                        cameraPose.ty(),
                                                        cameraPose.tz()
                                                )
                                        val nodePosition = imageNode.worldPosition
                                        imageNode.lookAt(
                                                Position(
                                                        cameraPosition.x,
                                                        nodePosition.y,
                                                        cameraPosition.z
                                                ),
                                        )
                                    }
                                }
                            }
                        }

                        fun placeAtHit(hit: HitResult) {
                            val anchor =
                                    try {
                                        hit.createAnchor()
                                    } catch (e: Exception) {
                                        null
                                    }
                            anchor?.let { createdAnchor ->
                                lastAnchorNodeRef?.anchor?.detach()
                                lastAnchorNodeRef?.let { removeChildNode(it) }
                                lastAnchorNodeRef = null

                                try {
                                    val bitmap = plantBitmap ?: return@let
                                    val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
                                    val heightMeters = 0.4f
                                    val widthMeters = heightMeters * ratio
                                    val scaledHalfHeight = (heightMeters * scaleFactor) / 2f

                                    val imageNode =
                                            ImageNode(
                                                            materialLoader =
                                                                    this@apply.materialLoader,
                                                            bitmap = bitmap,
                                                            size = Size(widthMeters, heightMeters)
                                                    )
                                                    .apply {
                                                        position =
                                                                Position(0f, scaledHalfHeight, 0f)
                                                        isTouchable = false
                                                        scale =
                                                                Scale(
                                                                        scaleFactor,
                                                                        scaleFactor,
                                                                        scaleFactor
                                                                )
                                                        rotation = Rotation(0f, 0f, 0f)
                                                    }

                                    val anchorNode = AnchorNode(engine, createdAnchor)
                                    anchorNode.addChildNode(imageNode)
                                    addChildNode(anchorNode)
                                    lastAnchorNodeRef = anchorNode
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }

                        onTouchEvent = { motionEvent: MotionEvent, _ ->
                            val action = motionEvent.action
                            if ((action == MotionEvent.ACTION_UP ||
                                            action == MotionEvent.ACTION_MOVE) &&
                                            motionEvent.pointerCount == 1
                            ) {

                                val frame = latestFrame
                                if (frame != null && plantBitmap != null) {
                                    val hits =
                                            try {
                                                frame.hitTest(motionEvent.x, motionEvent.y)
                                            } catch (e: Exception) {
                                                emptyList<HitResult>()
                                            }

                                    val hitResult =
                                            hits.firstOrNull { hit ->
                                                val trackable = hit.trackable
                                                when (trackable) {
                                                    is Plane ->
                                                            trackable.isPoseInPolygon(hit.hitPose)
                                                    is com.google.ar.core.Point ->
                                                            trackable.orientationMode ==
                                                                    com.google.ar.core.Point
                                                                            .OrientationMode
                                                                            .ESTIMATED_SURFACE_NORMAL
                                                    is com.google.ar.core.InstantPlacementPoint ->
                                                            true
                                                    else -> false
                                                }
                                            }

                                    hitResult?.let { placeAtHit(it) }
                                }
                                true
                            } else {
                                true
                            }
                        }
                    }
                },
                update = {}
        )

        if (plantBitmap == null) {
            if (!isLoading) {
                Box(
                        modifier = Modifier.fillMaxSize().padding(bottom = 100.dp),
                        contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.Surface(
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.6f),
                            contentColor = Color.White
                    ) {
                        Text(
                                text = "Select a plant from the list below",
                                style =
                                        androidx.compose.material3.MaterialTheme.typography
                                                .titleMedium,
                                modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        } else {
            if (instructionText.isNotEmpty()) {
                Box(
                        modifier =
                                Modifier.align(Alignment.TopCenter)
                                        .padding(top = 80.dp)
                                        .padding(horizontal = 32.dp)
                ) {
                    androidx.compose.material3.Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.6f),
                            contentColor = Color.White
                    ) {
                        Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (instructionText.contains("scan")) {
                                CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                            }

                            Text(
                                    text = instructionText,
                                    style =
                                            androidx.compose.material3.MaterialTheme.typography
                                                    .bodyMedium,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        ) {
            Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
            )
        }

        Column(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                    modifier =
                            Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                FilledIconButton(
                        onClick = {
                            scaleFactor = (scaleFactor * 1.08f).coerceIn(0.5f, 1.6f)

                            lastAnchorNodeRef?.let { anchor ->
                                val node = anchor.childNodes.firstOrNull()
                                node?.let {
                                    it.scale = Scale(scaleFactor, scaleFactor, scaleFactor)
                                    val baseHeight = 0.4f
                                    it.position = Position(0f, (baseHeight * scaleFactor) / 2f, 0f)
                                }
                            }
                        },
                        colors =
                                IconButtonDefaults.iconButtonColors(
                                        containerColor = Color.White.copy(alpha = 0.8f),
                                        contentColor = Color.Black
                                ),
                        modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase Scale",
                            modifier = Modifier.size(32.dp)
                    )
                }

                FilledIconButton(
                        onClick = {
                            scaleFactor = (scaleFactor * 0.92f).coerceIn(0.5f, 1.6f)

                            lastAnchorNodeRef?.let { anchor ->
                                val node = anchor.childNodes.firstOrNull()
                                node?.let {
                                    it.scale = Scale(scaleFactor, scaleFactor, scaleFactor)
                                    val baseHeight = 0.4f
                                    it.position = Position(0f, (baseHeight * scaleFactor) / 2f, 0f)
                                }
                            }
                        },
                        colors =
                                IconButtonDefaults.iconButtonColors(
                                        containerColor = Color.White.copy(alpha = 0.8f),
                                        contentColor = Color.Black
                                ),
                        modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease Scale",
                            modifier = Modifier.size(32.dp)
                    )
                }
            }

            if (plantImageUrl.isEmpty() && marketState.plants.isNotEmpty()) {
                LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(marketState.plants.take(10)) { plant ->
                        val isSelected = plant.image_url == currentPlantUrl

                        AsyncImage(
                                model =
                                        ImageRequest.Builder(LocalContext.current)
                                                .data(plant.image_url)
                                                .crossfade(true)
                                                .build(),
                                contentDescription = plant.name,
                                contentScale = ContentScale.Crop,
                                modifier =
                                        Modifier.size(60.dp)
                                                .clip(CircleShape)
                                                .border(
                                                        width = if (isSelected) 3.dp else 1.dp,
                                                        color =
                                                                if (isSelected) Color.Green
                                                                else Color.White,
                                                        shape = CircleShape
                                                )
                                                .clickable {
                                                    plant.image_url?.let { currentPlantUrl = it }
                                                }
                        )
                    }
                }
            }

            FloatingActionButton(
                    onClick = {
                        arSceneView?.let { view ->
                            view.planeRenderer.isEnabled = false

                            Handler(Looper.getMainLooper())
                                    .postDelayed(
                                            {
                                                val bitmap =
                                                        Bitmap.createBitmap(
                                                                view.width,
                                                                view.height,
                                                                Bitmap.Config.ARGB_8888
                                                        )
                                                try {
                                                    PixelCopy.request(
                                                            view,
                                                            bitmap,
                                                            { result ->
                                                                view.planeRenderer.isEnabled = true

                                                                if (result == PixelCopy.SUCCESS) {
                                                                    saveBitmapToGallery(
                                                                            context,
                                                                            bitmap
                                                                    )
                                                                } else {
                                                                    Toast.makeText(
                                                                                    context,
                                                                                    "Capture failed",
                                                                                    Toast.LENGTH_SHORT
                                                                            )
                                                                            .show()
                                                                }
                                                            },
                                                            Handler(Looper.getMainLooper())
                                                    )
                                                } catch (e: Exception) {
                                                    view.planeRenderer.isEnabled = true
                                                    Toast.makeText(
                                                                    context,
                                                                    "Error capturing view",
                                                                    Toast.LENGTH_SHORT
                                                            )
                                                            .show()
                                                }
                                            },
                                            100
                                    )
                        }
                    },
                    containerColor = Color.White,
                    contentColor = Color.Black
            ) { Icon(Icons.Default.CameraAlt, contentDescription = "Capture Photo") }
        }
    }
}

fun saveBitmapToGallery(context: Context, bitmap: Bitmap) {
    val filename = "Navya_AR_${System.currentTimeMillis()}.jpg"
    var fos: java.io.OutputStream? = null
    var imageUri: android.net.Uri? = null

    try {
        val contentValues =
                ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                        put(MediaStore.MediaColumns.IS_PENDING, 1)
                    }
                }

        val resolver = context.contentResolver
        imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let { uri ->
            fos = resolver.openOutputStream(uri)
            fos?.let { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }

            Toast.makeText(context, "Saved to Gallery!", Toast.LENGTH_SHORT).show()
        }
                ?: run {
                    Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
                }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
    } finally {
        try {
            fos?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
