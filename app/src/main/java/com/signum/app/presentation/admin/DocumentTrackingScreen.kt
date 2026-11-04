package com.signum.app.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.signum.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentTrackingScreen(
    onBackToHome: () -> Unit
) {
    val isDark = isSystemInDarkTheme()

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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = stringResource(id = R.string.tracking_contract_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = headerTextColor
            )

            // Status Card
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
                        color = mainTextColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(id = R.string.tracking_status_waiting),
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                }
            }

            // Timeline Header
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

            // Visual Timeline Section
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
                    // Passo 1: Documento Gerado
                    TimelineStep(
                        title = stringResource(id = R.string.timeline_step_created_title),
                        description = stringResource(id = R.string.timeline_step_created_desc),
                        status = TimelineStepStatus.COMPLETED,
                        mainTextColor = mainTextColor,
                        subTextColor = subTextColor,
                        showBottomLine = true
                    )

                    // Passo 2: Documento Enviado
                    TimelineStep(
                        title = stringResource(id = R.string.timeline_step_sent_title),
                        description = stringResource(id = R.string.timeline_step_sent_desc),
                        status = TimelineStepStatus.COMPLETED,
                        mainTextColor = mainTextColor,
                        subTextColor = subTextColor,
                        showBottomLine = true
                    )

                    // Passo 3: Aguardando Assinatura
                    TimelineStep(
                        title = stringResource(id = R.string.timeline_step_pending_title),
                        description = stringResource(id = R.string.timeline_step_pending_desc),
                        status = TimelineStepStatus.IN_PROGRESS,
                        mainTextColor = mainTextColor,
                        subTextColor = subTextColor,
                        showBottomLine = false
                    )
                }
            }
        }
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
