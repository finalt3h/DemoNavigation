package com.example.demonavigation

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.video.FallbackStrategy
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.demonavigation.ui.theme.DemoNavigationTheme
import java.text.SimpleDateFormat
import java.util.Locale

class CameraFragment : Fragment() {
    private var hasCameraPermission by mutableStateOf(false)
    private var hasAudioPermission by mutableStateOf(false)
    private var statusText by mutableStateOf("Ready")
    private var isRecording by mutableStateOf(false)
    private var isTorchOn by mutableStateOf(false)
    private var hasFlashUnit by mutableStateOf(false)
    private var lastMediaUri by mutableStateOf<Uri?>(null)
    private var lensFacing by mutableStateOf(CameraSelector.LENS_FACING_BACK)

    private var previewView: PreviewView? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var activeRecording: Recording? = null

    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        hasCameraPermission = results[Manifest.permission.CAMERA] == true
        hasAudioPermission = results[Manifest.permission.RECORD_AUDIO] == true
        if (hasCameraPermission) {
            statusText = if (hasAudioPermission) "Camera ready" else "Camera ready without audio"
            startCamera()
        } else {
            statusText = "Camera permission is required"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        refreshPermissionState()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                DemoNavigationTheme {
                    CameraScreen(
                        hasCameraPermission = hasCameraPermission,
                        statusText = statusText,
                        isRecording = isRecording,
                        isTorchOn = isTorchOn,
                        hasFlashUnit = hasFlashUnit,
                        lastMediaUri = lastMediaUri,
                        onRequestPermission = ::requestCameraPermissions,
                        onBackClick = { findNavController().navigateUp() },
                        onPreviewReady = { view ->
                            previewView = view
                            startCamera()
                        },
                        onSwitchCameraClick = ::switchCamera,
                        onTorchClick = ::toggleTorch,
                        onTakePhotoClick = ::takePhoto,
                        onRecordClick = ::toggleRecording
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        activeRecording?.stop()
        activeRecording = null
        cameraProvider?.unbindAll()
        previewView = null
        super.onDestroyView()
    }

    private fun refreshPermissionState() {
        val context = requireContext()
        hasCameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        hasAudioPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermissions() {
        permissionsLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        )
    }

    private fun startCamera() {
        if (!hasCameraPermission || previewView == null || !isAdded) return

        val context = requireContext()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener(
            {
                cameraProvider = cameraProviderFuture.get()
                bindCameraUseCases()
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    private fun bindCameraUseCases() {
        val provider = cameraProvider ?: return
        val view = previewView ?: return
        if (!hasCameraPermission) return

        val selector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()
        val preview = Preview.Builder()
            .setTargetRotation(view.display.rotation)
            .build()
        val capture = ImageCapture.Builder()
            .setTargetRotation(view.display.rotation)
            .build()
        val recorder = Recorder.Builder()
            .setQualitySelector(
                QualitySelector.from(
                    Quality.HD,
                    FallbackStrategy.higherQualityOrLowerThan(Quality.HD)
                )
            )
            .build()
        val video = VideoCapture.withOutput(recorder)

        try {
            provider.unbindAll()
            preview.setSurfaceProvider(view.surfaceProvider)
            camera = provider.bindToLifecycle(
                viewLifecycleOwner,
                selector,
                preview,
                capture,
                video
            )
            imageCapture = capture
            videoCapture = video
            hasFlashUnit = camera?.cameraInfo?.hasFlashUnit() == true
            if (hasFlashUnit) {
                camera?.cameraControl?.enableTorch(isTorchOn)
            } else {
                isTorchOn = false
            }
            statusText = "Camera ready"
        } catch (exception: Exception) {
            imageCapture = null
            videoCapture = null
            hasFlashUnit = false
            statusText = "Unable to start camera: ${exception.message.orEmpty()}"
        }
    }

    private fun switchCamera() {
        if (isRecording) return
        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
        isTorchOn = false
        bindCameraUseCases()
    }

    private fun toggleTorch() {
        if (!hasFlashUnit || isRecording) return
        isTorchOn = !isTorchOn
        camera?.cameraControl?.enableTorch(isTorchOn)
        statusText = if (isTorchOn) "Flash on" else "Flash off"
    }

    private fun takePhoto() {
        val capture = imageCapture ?: run {
            statusText = "Camera is not ready"
            return
        }
        if (isRecording) return

        val name = timestampedName("IMG", "jpg")
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/DemoNavigation")
            }
        }
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            requireContext().contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        statusText = "Saving photo..."
        capture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    lastMediaUri = outputFileResults.savedUri
                    statusText = "Photo saved"
                }

                override fun onError(exception: ImageCaptureException) {
                    statusText = "Photo failed: ${exception.message.orEmpty()}"
                }
            }
        )
    }

    private fun toggleRecording() {
        val video = videoCapture ?: run {
            statusText = "Camera is not ready"
            return
        }

        val recording = activeRecording
        if (recording != null) {
            recording.stop()
            activeRecording = null
            return
        }

        val name = timestampedName("VID", "mp4")
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, name)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/DemoNavigation")
            }
        }
        val outputOptions = MediaStoreOutputOptions.Builder(
            requireContext().contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        )
            .setContentValues(contentValues)
            .build()

        var pendingRecording = video.output.prepareRecording(requireContext(), outputOptions)
        if (hasAudioPermission) {
            pendingRecording = pendingRecording.withAudioEnabled()
        }

        statusText = "Recording..."
        activeRecording = pendingRecording.start(
            ContextCompat.getMainExecutor(requireContext())
        ) { event ->
            when (event) {
                is VideoRecordEvent.Start -> {
                    isRecording = true
                    statusText = "Recording..."
                }

                is VideoRecordEvent.Finalize -> {
                    isRecording = false
                    activeRecording = null
                    if (!event.hasError()) {
                        lastMediaUri = event.outputResults.outputUri
                        statusText = "Video saved"
                    } else {
                        statusText = "Video failed: ${event.error}"
                    }
                }
            }
        }
    }

    private fun timestampedName(prefix: String, extension: String): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
            .format(System.currentTimeMillis())
        return "${prefix}_$timestamp.$extension"
    }
}

@Composable
private fun CameraScreen(
    hasCameraPermission: Boolean,
    statusText: String,
    isRecording: Boolean,
    isTorchOn: Boolean,
    hasFlashUnit: Boolean,
    lastMediaUri: Uri?,
    onRequestPermission: () -> Unit,
    onBackClick: () -> Unit,
    onPreviewReady: (PreviewView) -> Unit,
    onSwitchCameraClick: () -> Unit,
    onTorchClick: () -> Unit,
    onTakePhotoClick: () -> Unit,
    onRecordClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        if (!hasCameraPermission) {
            PermissionContent(
                statusText = statusText,
                onBackClick = onBackClick,
                onRequestPermission = onRequestPermission
            )
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    factory = { context ->
                        PreviewView(context).apply {
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                            onPreviewReady(this)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                CameraTopBar(
                    statusText = statusText,
                    onBackClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                )

                CameraControls(
                    isRecording = isRecording,
                    isTorchOn = isTorchOn,
                    hasFlashUnit = hasFlashUnit,
                    lastMediaUri = lastMediaUri,
                    onSwitchCameraClick = onSwitchCameraClick,
                    onTorchClick = onTorchClick,
                    onTakePhotoClick = onTakePhotoClick,
                    onRecordClick = onRecordClick,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun PermissionContent(
    statusText: String,
    onBackClick: () -> Unit,
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = statusText,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRequestPermission) {
            Text(text = "Grant camera access")
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onBackClick) {
            Text(text = "Back")
        }
    }
}

@Composable
private fun CameraTopBar(
    statusText: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.55f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(onClick = onBackClick) {
            Text(text = "Back")
        }
        Text(
            text = statusText,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CameraControls(
    isRecording: Boolean,
    isTorchOn: Boolean,
    hasFlashUnit: Boolean,
    lastMediaUri: Uri?,
    onSwitchCameraClick: () -> Unit,
    onTorchClick: () -> Unit,
    onTakePhotoClick: () -> Unit,
    onRecordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.62f))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MediaThumbnail(
                uri = lastMediaUri,
                modifier = Modifier.size(58.dp)
            )
            OutlinedButton(
                onClick = onSwitchCameraClick,
                enabled = !isRecording,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Switch")
            }
            OutlinedButton(
                onClick = onTorchClick,
                enabled = hasFlashUnit && !isRecording,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = if (isTorchOn) "Flash Off" else "Flash On")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onTakePhotoClick,
                enabled = !isRecording,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Photo")
            }
            Button(
                onClick = onRecordClick,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = if (isRecording) "Stop" else "Record")
            }
        }
    }
}

@Composable
private fun MediaThumbnail(
    uri: Uri?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color.DarkGray, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (uri == null) {
            Text(
                text = "Last",
                color = Color.White,
                style = MaterialTheme.typography.labelMedium
            )
        } else {
            AndroidView(
                factory = { context ->
                    ImageView(context).apply {
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        setImageURI(uri)
                    }
                },
                update = { imageView ->
                    imageView.setImageURI(uri)
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
