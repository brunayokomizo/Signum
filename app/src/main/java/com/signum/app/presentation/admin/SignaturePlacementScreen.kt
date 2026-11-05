package com.signum.app.presentation.admin

import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.signum.app.R
import com.signum.app.presentation.components.PdfViewer
import java.io.File
import java.util.UUID
import kotlin.math.roundToInt

data class SignatureBlockUI(
    val id: String = UUID.randomUUID().toString(),
    var offsetX: Float = 0f,
    var offsetY: Float = 100f,
    var pageNumber: Int = 1,
    val role: String = "Contratante"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignaturePlacementScreen(
    onSendDocument: (fieldsSummary: String) -> Unit,
    onBackClick: () -> Unit = {}
) {
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current

    val activePdfFile = remember {
        File(context.filesDir, "active_document.pdf").takeIf { it.exists() }
            ?: File(context.filesDir, "contrato_prestacao_servicos.pdf")
    }

    val totalPages = remember(activePdfFile) {
        try {
            val pfd = ParcelFileDescriptor.open(activePdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(pfd)
            val count = renderer.pageCount
            renderer.close()
            pfd.close()
            count.coerceAtLeast(1)
        } catch (e: Exception) {
            1
        }
    }

    var currentPage by remember { mutableIntStateOf(1) }

    val signatureBlocks = remember {
        mutableStateListOf(
            SignatureBlockUI(id = "1", offsetX = 0f, offsetY = 120f, pageNumber = 1, role = "Contratante")
        )
    }

    val backgroundGradient = if (isDark) {
        Brush.verticalGradient(listOf(Color(0xFF0A121E), Color(0xFF131D2A)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFFEBF2FA), Color(0xFFF4F7FB)))
    }

    val documentGradient = if (isDark) {
        Brush.linearGradient(listOf(Color(0xFF1E2A3A), Color(0xFF131D2A)))
    } else {
        Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color(0xFFFFFFFF)))
    }
    val cardBorderColor = if (isDark) Color(0xFF2A3C52) else Color(0xFFD8E5F2)
    val headerTextColor = if (isDark) Color.White else Color(0xFF0A192F)

    val btnGradient = if (isDark) {
        listOf(Color(0xFF0088CC), Color(0xFF005580))
    } else {
        listOf(Color(0xFF0052CC), Color(0xFF007AFF))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.place_signature_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(brush = backgroundGradient)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.place_signature_instruction),
                        color = headerTextColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                IconButton(
                    onClick = {
                        val newBlockNumber = signatureBlocks.size + 1
                        val role = if (newBlockNumber % 2 == 0) "Contratado" else "Testemunha"
                        signatureBlocks.add(
                            SignatureBlockUI(
                                id = UUID.randomUUID().toString(),
                                offsetX = 0f,
                                offsetY = 80f + (newBlockNumber * 50),
                                pageNumber = currentPage,
                                role = role
                            )
                        )
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar Campo",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush = documentGradient)
                    .border(width = 1.dp, color = cardBorderColor, shape = RoundedCornerShape(16.dp))
            ) {
                if (activePdfFile.exists()) {
                    PdfViewer(
                        pdfFile = activePdfFile,
                        pageIndex = (currentPage - 1).coerceAtLeast(0),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                val density = LocalDensity.current
                val containerWidthPx = with(density) { maxWidth.toPx() }
                val containerHeightPx = with(density) { maxHeight.toPx() }

                // Navigation Header
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp)
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (currentPage > 1) currentPage-- },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.NavigateBefore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Página $currentPage de $totalPages",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { if (currentPage < totalPages) currentPage++ },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Render each signature block for current page
                signatureBlocks.filter { it.pageNumber == currentPage }.forEach { block ->
                    key(block.id) {
                        DraggableSignatureBlock(
                            block = block,
                            containerWidthPx = containerWidthPx,
                            containerHeightPx = containerHeightPx,
                            canDelete = signatureBlocks.size > 1,
                            onDelete = {
                                signatureBlocks.removeAll { it.id == block.id }
                            },
                            onPositionChanged = { newX, newY ->
                                block.offsetX = newX
                                block.offsetY = newY
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush = Brush.horizontalGradient(colors = btnGradient))
                    .clickable {
                        val summary = "• ${signatureBlocks.size} campo(s) de assinatura na Página $currentPage"
                        onSendDocument(summary)
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.review_and_send_doc),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun DraggableSignatureBlock(
    block: SignatureBlockUI,
    containerWidthPx: Float,
    containerHeightPx: Float,
    canDelete: Boolean,
    onDelete: () -> Unit,
    onPositionChanged: (Float, Float) -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(block.offsetX) }
    var offsetY by remember { mutableFloatStateOf(block.offsetY) }
    var measuredWidthPx by remember { mutableFloatStateOf(300f) }
    var measuredHeightPx by remember { mutableFloatStateOf(100f) }

    val maxAllowedX = (containerWidthPx - measuredWidthPx).coerceAtLeast(0f)
    val maxAllowedY = (containerHeightPx - measuredHeightPx).coerceAtLeast(0f)

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .onGloballyPositioned { coordinates ->
                measuredWidthPx = coordinates.size.width.toFloat()
                measuredHeightPx = coordinates.size.height.toFloat()
            }
            .shadow(elevation = 10.dp, shape = RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF0052CC), Color(0xFF007AFF))
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX = (offsetX + dragAmount.x).coerceIn(0f, maxAllowedX)
                    offsetY = (offsetY + dragAmount.y).coerceIn(0f, maxAllowedY)
                    onPositionChanged(offsetX, offsetY)
                }
            }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(
                    text = stringResource(id = R.string.signature_block_title),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 12.sp
                )
                Text(
                    text = block.role,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 11.sp
                )
            }
            if (canDelete) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color.White.copy(alpha = 0.3f), CircleShape)
                        .clickable { onDelete() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Excluir",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
