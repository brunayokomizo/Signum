package com.signum.app.presentation.admin

import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.signum.app.R
import com.signum.app.data.local.entity.UserEntity
import java.io.File
import java.util.UUID

data class SignerAssignmentUI(
    val id: String,
    val name: String,
    val email: String,
    var role: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentSetupScreen(
    signersList: List<UserEntity> = emptyList(),
    onAddSignerUser: (name: String, email: String, phone: String, pass: String) -> Unit = { _, _, _, _ -> },
    onNextStepClick: (title: String, signers: List<SignerAssignmentUI>) -> Unit,
    onBackClick: () -> Unit = {}
) {
    var contractName by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf("") }
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    var showAddSignerDialog by remember { mutableStateOf(false) }

    val assignedSigners = remember(signersList) {
        mutableStateListOf<SignerAssignmentUI>().apply {
            signersList.forEach { user ->
                add(SignerAssignmentUI(user.id, user.fullName, user.email, "Contratante"))
            }
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Validation step
            val isPdfValid = try {
                val pfd = context.contentResolver.openFileDescriptor(it, "r")
                val isValid = pfd != null
                pfd?.close()
                isValid
            } catch (e: Exception) {
                false
            }

            if (!isPdfValid) {
                Toast.makeText(
                    context,
                    "O arquivo PDF selecionado é inválido, está corrompido ou não pôde ser processado.",
                    Toast.LENGTH_LONG
                ).show()
                return@rememberLauncherForActivityResult
            }

            // Copy selected PDF to active_document.pdf in app filesDir
            try {
                val activeFile = File(context.filesDir, "active_document.pdf")
                context.contentResolver.openInputStream(it)?.use { input ->
                    activeFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            selectedFileUri = it
            var name = ""
            try {
                val cursor = context.contentResolver.query(it, null, null, null, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) {
                        name = cursor.getString(nameIndex)
                    }
                    cursor.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (name.isBlank()) {
                val lastSegment = it.lastPathSegment ?: ""
                name = if (lastSegment.contains('/')) lastSegment.substringAfterLast('/') else lastSegment
            }
            selectedFileName = if (name.isNotBlank()) name else "documento.pdf"
            if (contractName.isBlank()) {
                contractName = selectedFileName.substringBeforeLast('.')
            }
        }
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

    val cardBorderColor = if (selectedFileUri != null) {
        if (isDark) Color(0xFF00D8FF) else Color(0xFF0052CC)
    } else {
        if (isDark) Color(0xFF2A3C52) else Color(0xFFD8E5F2)
    }

    val btnGradient = if (isDark) {
        listOf(Color(0xFF0088CC), Color(0xFF005580))
    } else {
        listOf(Color(0xFF0052CC), Color(0xFF007AFF))
    }
    val mainTextColor = if (isDark) Color.White else Color(0xFF0A192F)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.doc_setup_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
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
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Título do Documento
                OutlinedTextField(
                    value = contractName,
                    onValueChange = { contractName = it },
                    label = { Text(stringResource(id = R.string.contract_name_label)) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.InsertDriveFile,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // 2. Upload de Arquivo PDF
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(brush = cardGradient)
                        .border(
                            width = if (selectedFileUri != null) 2.dp else 1.dp,
                            color = cardBorderColor,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { filePickerLauncher.launch("application/pdf") },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        if (selectedFileUri != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = selectedFileName,
                                color = mainTextColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.FileUpload,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(id = R.string.no_file_selected),
                                color = mainTextColor,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // 3. Seleção, Classificação e Remoção de Signatários
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.select_signers_label),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = mainTextColor
                    )
                }

                if (assignedSigners.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(cardBorderColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhum signatário adicionado ainda. Clique no botão abaixo para adicionar.",
                            color = mainTextColor.copy(alpha = 0.7f),
                            fontSize = 13.sp
                        )
                    }
                }

                assignedSigners.forEachIndexed { index, signer ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 6.dp, shape = RoundedCornerShape(14.dp))
                            .clip(RoundedCornerShape(14.dp))
                            .background(brush = cardGradient)
                            .border(width = 1.dp, color = cardBorderColor, shape = RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = signer.name,
                                            fontWeight = FontWeight.Bold,
                                            color = mainTextColor
                                        )
                                        Text(
                                            text = signer.email,
                                            fontSize = 12.sp,
                                            color = mainTextColor.copy(alpha = 0.7f)
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { assignedSigners.removeAt(index) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remover",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Seletor de Papel / Classificação
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Contratante", "Contratado", "Testemunha").forEach { roleOption ->
                                    val isSelected = signer.role == roleOption
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            assignedSigners[index] = signer.copy(role = roleOption)
                                        },
                                        label = {
                                            Text(
                                                text = roleOption,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                OutlinedButton(
                    onClick = { showAddSignerDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(id = R.string.add_signer_button), fontWeight = FontWeight.Bold)
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
                        if (contractName.isBlank()) {
                            Toast.makeText(context, "Informe o título do documento.", Toast.LENGTH_SHORT).show()
                            return@clickable
                        }
                        if (assignedSigners.isEmpty()) {
                            Toast.makeText(context, "Adicione pelo menos 1 signatário.", Toast.LENGTH_SHORT).show()
                            return@clickable
                        }
                        onNextStepClick(contractName, assignedSigners.toList())
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.continue_to_signatures),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }

    // Modal Interactive Dialog for Adding a New Signer with Credentials and LGPD Compliance
    if (showAddSignerDialog) {
        var newSignerName by remember { mutableStateOf("") }
        var newSignerEmail by remember { mutableStateOf("") }
        var newSignerPhone by remember { mutableStateOf("") }
        var newSignerPassword by remember { mutableStateOf("Signum@123") }
        var selectedRole by remember { mutableStateOf("Testemunha") }
        var isLgpdAccepted by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { showAddSignerDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cadastrar Credenciais do Signatário",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = newSignerName,
                        onValueChange = { newSignerName = it },
                        label = { Text("Nome Completo") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newSignerEmail,
                        onValueChange = { newSignerEmail = it },
                        label = { Text("E-mail (Login de Acesso)") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newSignerPhone,
                        onValueChange = { newSignerPhone = it },
                        label = { Text("Telefone") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newSignerPassword,
                        onValueChange = { newSignerPassword = it },
                        label = { Text("Palavra-passe de Acesso") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Classificação:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Contratante", "Contratado", "Testemunha").forEach { role ->
                            FilterChip(
                                selected = selectedRole == role,
                                onClick = { selectedRole = role },
                                label = { Text(role, fontSize = 10.sp) }
                            )
                        }
                    }

                    // LGPD Compliance Notice Box
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isLgpdAccepted = !isLgpdAccepted }
                            .padding(top = 4.dp)
                    ) {
                        Checkbox(
                            checked = isLgpdAccepted,
                            onCheckedChange = { isLgpdAccepted = it }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Autorizo o tratamento de dados pessoais conforme a LGPD (Lei nº 13.709/2018).",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            lineHeight = 14.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newSignerName.isBlank() || newSignerEmail.isBlank() || newSignerPassword.isBlank()) {
                            Toast.makeText(context, "Preencha o nome, e-mail e palavra-passe do signatário.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (!isLgpdAccepted) {
                            Toast.makeText(context, "É necessário aceitar os termos da LGPD para cadastrar.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // Register persistent user credentials into Room DB
                        onAddSignerUser(newSignerName, newSignerEmail, newSignerPhone, newSignerPassword)

                        val newId = UUID.randomUUID().toString()
                        assignedSigners.add(
                            SignerAssignmentUI(
                                id = newId,
                                name = newSignerName,
                                email = newSignerEmail,
                                role = selectedRole
                            )
                        )
                        Toast.makeText(context, "Signatário $newSignerName cadastrado com sucesso com senha de acesso!", Toast.LENGTH_SHORT).show()
                        showAddSignerDialog = false
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cadastrar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSignerDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
