package com.signum.app.presentation.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TouchApp
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentReviewScreen(
    contractName: String = "Contrato de Prestação de Serviços",
    signersInfo: String = "• João Silva (Contratante)",
    signaturePagesInfo: String = "• 2 campos de assinatura na Página 1",
    onUpdateContractName: (String) -> Unit = {},
    onEditSignersClick: () -> Unit = {},
    onEditPlacementClick: () -> Unit = {},
    onConfirmAndSend: (inviteMessage: String) -> Unit,
    onBackClick: () -> Unit = {}
) {
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var editableContractName by remember(contractName) { mutableStateOf(contractName) }
    var showEditTitleDialog by remember { mutableStateOf(false) }

    var inviteMessage by remember {
        mutableStateOf("Olá! Segue o documento oficial para a coleta da sua assinatura digital na plataforma Signum.")
    }
    var isLoading by remember { mutableStateOf(false) }

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

    val btnGradient = if (isDark) {
        listOf(Color(0xFF0088CC), Color(0xFF005580))
    } else {
        listOf(Color(0xFF0052CC), Color(0xFF007AFF))
    }

    val editActionText = stringResource(id = R.string.edit_action)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.review_final_title), fontWeight = FontWeight.Bold) },
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
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.review_shipping_summary),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = headerTextColor
                )

                // Resumo do Documento e Signatários com botões de edição rápida
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(brush = cardGradient)
                        .border(width = 1.dp, color = cardBorderColor, shape = RoundedCornerShape(16.dp))
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // 1. Nome do Documento (Editável)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.AutoMirrored.Filled.InsertDriveFile, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = stringResource(id = R.string.review_document_label), fontWeight = FontWeight.Bold, color = mainTextColor)
                            }
                            IconButton(
                                onClick = { showEditTitleDialog = true },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = editActionText, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Text(
                            text = editableContractName,
                            color = mainTextColor.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 28.dp)
                        )

                        HorizontalDivider(color = cardBorderColor.copy(alpha = 0.5f))

                        // 2. Lista de Signatários (Editável)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.People, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = stringResource(id = R.string.review_signers_label), fontWeight = FontWeight.Bold, color = mainTextColor)
                            }
                            IconButton(
                                onClick = onEditSignersClick,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = editActionText, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Text(
                            text = signersInfo,
                            color = mainTextColor.copy(alpha = 0.85f),
                            modifier = Modifier.padding(start = 28.dp)
                        )

                        HorizontalDivider(color = cardBorderColor.copy(alpha = 0.5f))

                        // 3. Campos e Páginas de Assinatura (Editável)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.TouchApp, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = stringResource(id = R.string.review_fields_pages_label), fontWeight = FontWeight.Bold, color = mainTextColor)
                            }
                            IconButton(
                                onClick = onEditPlacementClick,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = editActionText, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Text(
                            text = signaturePagesInfo,
                            color = mainTextColor.copy(alpha = 0.85f),
                            modifier = Modifier.padding(start = 28.dp)
                        )
                    }
                }

                // 4. Mensagem Editável do Convite
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(brush = cardGradient)
                        .border(width = 1.dp, color = cardBorderColor, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EditNote, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(id = R.string.review_invite_message) + " " + stringResource(id = R.string.review_editable_tag),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = inviteMessage,
                            onValueChange = { inviteMessage = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 4
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
                    .clickable(enabled = !isLoading) {
                        isLoading = true
                        scope.launch {
                            delay(1000)
                            isLoading = false
                            onUpdateContractName(editableContractName)
                            Toast.makeText(context, "Convite registrado e enviado com sucesso!", Toast.LENGTH_SHORT).show()
                            onConfirmAndSend(inviteMessage)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.review_send_button),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    // Modal Dialog to edit Document Title directly
    if (showEditTitleDialog) {
        var tempTitle by remember { mutableStateOf(editableContractName) }
        AlertDialog(
            onDismissRequest = { showEditTitleDialog = false },
            title = { Text(stringResource(id = R.string.edit_title_dialog_title), fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = tempTitle,
                    onValueChange = { tempTitle = it },
                    label = { Text(stringResource(id = R.string.contract_name_label)) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempTitle.isNotBlank()) {
                            editableContractName = tempTitle
                            onUpdateContractName(tempTitle)
                        }
                        showEditTitleDialog = false
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Salvar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditTitleDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
