package com.signum.app.presentation.admin

import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.signum.app.R
import com.signum.app.presentation.components.PdfViewer
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SignerStatusUI(
    val name: String,
    val role: String,
    val isSigned: Boolean,
    val signedAtDate: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentTrackingScreen(
    documentTitle: String = "Contrato de Prestação de Serviços",
    signersList: List<SignerAssignmentUI> = emptyList(),
    signedSignerNames: List<String> = emptyList(),
    isClientSigned: Boolean = false,
    onBackToHome: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current

    var showPdfViewerDialog by remember { mutableStateOf(false) }

    val activePdfFile = remember {
        File(context.filesDir, "active_document.pdf").takeIf { it.exists() }
            ?: File(context.filesDir, "contrato_prestacao_servicos.pdf")
    }

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault()) }
    val currentTimeStr = remember { dateFormat.format(Date()) }

    val activeSigners = remember(signersList, isClientSigned, signedSignerNames) {
        if (signersList.isNotEmpty()) {
            signersList.map { item ->
                val signed = signedSignerNames.any { signedName ->
                    item.name.contains(signedName, ignoreCase = true) || signedName.contains(item.name, ignoreCase = true)
                }
                SignerStatusUI(
                    name = item.name,
                    role = item.role,
                    isSigned = signed,
                    signedAtDate = if (signed) currentTimeStr else null
                )
            }
        } else {
            val isAnaSigned = signedSignerNames.any { it.contains("Ana", ignoreCase = true) }
            val isMarceloSigned = signedSignerNames.any { it.contains("Marcelo", ignoreCase = true) }
            listOf(
                SignerStatusUI(
                    name = "Ana Aparecida Lisboa",
                    role = "Contratante",
                    isSigned = isAnaSigned,
                    signedAtDate = if (isAnaSigned) currentTimeStr else null
                ),
                SignerStatusUI(
                    name = "Marcelo Pimentel",
                    role = "Contratado",
                    isSigned = isMarceloSigned,
                    signedAtDate = if (isMarceloSigned) currentTimeStr else null
                )
            )
        }
    }

    // Business Rule: Document is ONLY completed when 100% of required signers have finished signing!
    val isCompleted = activeSigners.isNotEmpty() && activeSigners.all { it.isSigned }
    val generalStatusText = when {
        isCompleted -> stringResource(id = R.string.status_completed_signed)
        activeSigners.any { it.isSigned } -> stringResource(id = R.string.status_partially_signed)
        else -> stringResource(id = R.string.status_waiting_signatures)
    }

    val backgroundGradient = if (isDark) {
        Brush.verticalGradient(listOf(Color(0xFF0A121E), Color(0xFF131D2A)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFFEBF2FA), Color(0xFFF4F7FB)))
    }

    val cardGradient = if (isDark) {
        Brush.linearGradient(listOf(Color(0xFF1E2A3A), Color(0xFF131D2A)))
    } else {
        Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color(0xFFFFFFFF)))
    }
    val cardBorderColor = if (isDark) Color(0xFF2A3C52) else Color(0xFFD8E5F2)
    val headerTextColor = if (isDark) Color.White else Color(0xFF0A192F)
    val mainTextColor = if (isDark) Color.White else Color(0xFF0A192F)
    val subTextColor = if (isDark) Color(0xFFB0BEC5) else Color(0xFF5E6C84)

    val btnGradient = if (isDark) {
        listOf(Color(0xFF0088CC), Color(0xFF005580))
    } else {
        listOf(Color(0xFF0052CC), Color(0xFF007AFF))
    }

    val signedAtText = stringResource(id = R.string.status_signed_at)
    val waitingSignatureText = stringResource(id = R.string.status_waiting_signature)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.tracking_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackToHome) {
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = documentTitle,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = headerTextColor
            )

            // 1. Situação Geral do Documento
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush = cardGradient)
                    .border(width = 1.dp, color = cardBorderColor, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = stringResource(id = R.string.tracking_general_status),
                        fontWeight = FontWeight.Bold,
                        color = subTextColor,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = generalStatusText,
                        color = if (isCompleted) Color(0xFF2E7D32) else if (activeSigners.any { it.isSigned }) Color(0xFFF57C00) else Color(0xFFD32F2F),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }
            }

            // 2. Situação Individual dos Signatários
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.People, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.tracking_individual_status),
                    fontWeight = FontWeight.Bold,
                    color = headerTextColor,
                    fontSize = 16.sp
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush = cardGradient)
                    .border(width = 1.dp, color = cardBorderColor, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    activeSigners.forEach { signer ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (signer.isSigned) Icons.Default.CheckCircle else Icons.Default.HourglassTop,
                                    contentDescription = null,
                                    tint = if (signer.isSigned) Color(0xFF2E7D32) else Color(0xFFF57C00),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "${signer.name} (${signer.role})",
                                        fontWeight = FontWeight.Bold,
                                        color = mainTextColor,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = if (signer.isSigned) "$signedAtText${signer.signedAtDate}" else waitingSignatureText,
                                        color = if (signer.isSigned) Color(0xFF2E7D32) else subTextColor,
                                        fontSize = 12.sp,
                                        fontWeight = if (signer.isSigned) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Histórico e Linha do Tempo
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.tracking_timeline),
                    fontWeight = FontWeight.Bold,
                    color = headerTextColor,
                    fontSize = 16.sp
                )
            }

            val signersNamesText = activeSigners.joinToString(" e ") { it.name }
            val signedSigners = activeSigners.filter { it.isSigned }
            val pendingSigners = activeSigners.filter { !it.isSigned }

            val docCreatedTitle = stringResource(id = R.string.timeline_created_title)
            val docCreatedBy = stringResource(id = R.string.timeline_created_by)
            val invitesSentTitle = stringResource(id = R.string.timeline_invites_sent_title)
            val sentToText = stringResource(id = R.string.timeline_sent_to)
            val signatureOfText = stringResource(id = R.string.timeline_signature_of)
            val signedSuccessfullyText = stringResource(id = R.string.timeline_signed_successfully)
            val pendingSignaturesTitle = stringResource(id = R.string.timeline_pending_signatures)
            val pendingPrefixText = stringResource(id = R.string.timeline_pending_prefix)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush = cardGradient)
                    .border(width = 1.dp, color = cardBorderColor, shape = RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column {
                    TimelineStep(
                        title = docCreatedTitle,
                        description = "$currentTimeStr - $docCreatedBy",
                        status = TimelineStepStatus.COMPLETED,
                        mainTextColor = mainTextColor,
                        subTextColor = subTextColor,
                        showBottomLine = true
                    )

                    TimelineStep(
                        title = invitesSentTitle,
                        description = "$currentTimeStr - $sentToText$signersNamesText",
                        status = TimelineStepStatus.COMPLETED,
                        mainTextColor = mainTextColor,
                        subTextColor = subTextColor,
                        showBottomLine = signedSigners.isNotEmpty() || pendingSigners.isNotEmpty()
                    )

                    signedSigners.forEachIndexed { index, signer ->
                        TimelineStep(
                            title = "$signatureOfText${signer.name}",
                            description = "${signer.signedAtDate} - $signedSuccessfullyText",
                            status = TimelineStepStatus.COMPLETED,
                            mainTextColor = mainTextColor,
                            subTextColor = subTextColor,
                            showBottomLine = pendingSigners.isNotEmpty() || index < signedSigners.size - 1
                        )
                    }

                    if (pendingSigners.isNotEmpty()) {
                        TimelineStep(
                            title = pendingSignaturesTitle,
                            description = "$pendingPrefixText${pendingSigners.joinToString(", ") { it.name }}",
                            status = TimelineStepStatus.IN_PROGRESS,
                            mainTextColor = mainTextColor,
                            subTextColor = subTextColor,
                            showBottomLine = false
                        )
                    }
                }
            }

            // 4. Documento Final Concluído (Disponível para Visualização pelo Admin)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush = Brush.horizontalGradient(colors = btnGradient))
                    .clickable {
                        showPdfViewerDialog = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.open_completed_doc_button),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }
        }
    }

    // Interactive PDF Viewer Dialog for the Admin
    if (showPdfViewerDialog) {
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

        AlertDialog(
            onDismissRequest = { showPdfViewerDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = documentTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    IconButton(onClick = { showPdfViewerDialog = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Page Navigation Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { if (currentPage > 1) currentPage-- },
                            enabled = currentPage > 1
                        ) {
                            Icon(Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = null)
                        }
                        Text(
                            text = "Página $currentPage de $totalPages",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        IconButton(
                            onClick = { if (currentPage < totalPages) currentPage++ },
                            enabled = currentPage < totalPages
                        ) {
                            Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = null)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        PdfViewer(
                            pdfFile = activePdfFile,
                            pageIndex = (currentPage - 1).coerceAtLeast(0),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPdfViewerDialog = false },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Fechar", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

enum class TimelineStepStatus {
    COMPLETED,
    IN_PROGRESS,
    PENDING
}

@Composable
fun TimelineStep(
    title: String,
    description: String,
    status: TimelineStepStatus,
    mainTextColor: Color,
    subTextColor: Color,
    showBottomLine: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            when (status) {
                TimelineStepStatus.COMPLETED -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(24.dp)
                    )
                }
                TimelineStepStatus.IN_PROGRESS -> {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color(0xFFF57C00),
                        modifier = Modifier.size(24.dp)
                    )
                }
                TimelineStepStatus.PENDING -> {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color.Gray.copy(alpha = 0.5f), shape = CircleShape)
                    )
                }
            }

            if (showBottomLine) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(36.dp)
                        .background(
                            if (status == TimelineStepStatus.COMPLETED) Color(0xFF2E7D32)
                            else Color.Gray.copy(alpha = 0.4f)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (showBottomLine) 12.dp else 0.dp)
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = mainTextColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 13.sp,
                color = subTextColor,
                fontWeight = FontWeight.Normal
            )
        }
    }
}
