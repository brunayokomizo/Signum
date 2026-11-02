package com.signum.app.presentation.signer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TaskAlt
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
fun SignerHomeScreen(
    onOpenDocumentToSign: (documentId: String) -> Unit,
    onLogout: () -> Unit = {}
) {
    val pendingDocuments = listOf("Contrato_Prestacao_Servicos.pdf")
    val completedDocuments = listOf("Termo_de_Confidencialidade.pdf", "Autorizacao_Uso_Imagem.pdf")

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
    val secondaryTextColor = if (isDark) Color(0xFFAAAAAA) else Color.DarkGray

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(id = R.string.signer_area_title), fontWeight = FontWeight.Bold)
                        Text(stringResource(id = R.string.greeting_client), style = MaterialTheme.typography.bodySmall)
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = stringResource(id = R.string.logout_action), tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
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
        ) {

            Text(
                text = stringResource(id = R.string.pending_signatures_section),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = headerTextColor,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                items(pendingDocuments) { doc ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(brush = cardGradient)
                            .border(width = 1.dp, color = cardBorderColor, shape = RoundedCornerShape(16.dp))
                            .clickable { onOpenDocumentToSign("doc_123") }
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = doc, fontWeight = FontWeight.Bold, color = mainTextColor)
                                Text(
                                    text = stringResource(id = R.string.tap_to_open_sign),
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = stringResource(id = R.string.open_action),
                                tint = secondaryTextColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(id = R.string.signed_documents_section),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = headerTextColor,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(completedDocuments) { doc ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 6.dp, shape = RoundedCornerShape(14.dp))
                            .clip(RoundedCornerShape(14.dp))
                            .background(brush = cardGradient)
                            .border(width = 1.dp, color = cardBorderColor, shape = RoundedCornerShape(14.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TaskAlt,
                                contentDescription = stringResource(id = R.string.completed_action),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = doc,
                                color = mainTextColor,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
