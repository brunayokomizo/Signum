package com.signum.app.presentation.signer

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.signum.app.R
import com.signum.app.presentation.components.PdfViewer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SigningScreen(
    onSignCompleted: () -> Unit,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val pdfFile = remember { File(context.filesDir, "contrato_prestacao_servicos.pdf") }

    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val isDark = isSystemInDarkTheme()

    val path = remember { Path() }
    var pathUpdate by remember { mutableIntStateOf(0) }
    var hasSignature by remember { mutableStateOf(false) }

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

    val inkColor = if (isDark) Color(0xFF00D8FF) else Color(0xFF0052CC)
    val padHintColor = if (isDark) Color(0xFF555555) else Color(0xFF8C9BAE)

    val btnGradient = if (isDark) {
        listOf(Color(0xFF0088CC), Color(0xFF005580))
    } else {
        listOf(Color(0xFF0052CC), Color(0xFF007AFF))
    }

    val sigRequiredMsg = stringResource(id = R.string.signature_required_toast)
    val sigSuccessMsg = stringResource(id = R.string.signature_success_toast)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.sign_document_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush = cardGradient)
                    .border(1.dp, cardBorderColor, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (pdfFile.exists()) {
                    PdfViewer(pdfFile = pdfFile, modifier = Modifier.fillMaxSize())
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (isDark) MaterialTheme.colorScheme.error else Color(0xFFE53935),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(stringResource(id = R.string.pdf_not_found), color = headerTextColor, fontWeight = FontWeight.Bold)
                        Text(stringResource(id = R.string.simulation_mode), color = if(isDark) Color.LightGray else Color.DarkGray, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(id = R.string.draw_signature_label), fontWeight = FontWeight.Bold, color = headerTextColor)
                if (hasSignature) {
                    Row(
                        modifier = Modifier.clickable {
                            path.reset()
                            pathUpdate++
                            hasSignature = false
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = stringResource(id = R.string.clear), modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(id = R.string.clear), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush = cardGradient)
                    .border(
                        width = if (hasSignature) 2.dp else 1.dp,
                        color = if (hasSignature) inkColor else cardBorderColor,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                if (!hasSignature) {
                    Text(
                        text = stringResource(id = R.string.draw_signature_hint),
                        color = padHintColor,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    path.moveTo(offset.x, offset.y)
                                    pathUpdate++
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    path.lineTo(change.position.x, change.position.y)
                                    pathUpdate++
                                    hasSignature = true
                                }
                            )
                        }
                ) {
                    pathUpdate.let {
                        drawPath(
                            path = path,
                            color = inkColor,
                            style = Stroke(
                                width = 6f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush = Brush.horizontalGradient(colors = btnGradient))
                    .clickable(enabled = !isLoading) {
                        if (!hasSignature) {
                            Toast.makeText(context, sigRequiredMsg, Toast.LENGTH_SHORT).show()
                            return@clickable
                        }
                        isLoading = true
                        scope.launch {
                            delay(1500)
                            isLoading = false
                            Toast.makeText(context, sigSuccessMsg, Toast.LENGTH_SHORT).show()
                            onSignCompleted()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.apply_signature_button),
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
