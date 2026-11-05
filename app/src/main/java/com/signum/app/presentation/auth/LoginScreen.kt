package com.signum.app.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.signum.app.R
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginClick: (email: String, pass: String, onError: (String) -> Unit) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
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

    val logoBacklight = if (isDark) {
        Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = 0.2f), Color.Transparent),
            radius = 400f
        )
    } else {
        Brush.radialGradient(
            colors = listOf(Color(0xFF0052CC).copy(alpha = 0.08f), Color.Transparent),
            radius = 400f
        )
    }

    val btnGradient = if (isDark) {
        listOf(Color(0xFF0088CC), Color(0xFF005580))
    } else {
        listOf(Color(0xFF0052CC), Color(0xFF007AFF))
    }

    val emptyErrorText = stringResource(id = R.string.login_error_empty)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .shadow(elevation = 12.dp, shape = RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(brush = cardGradient)
                .border(width = 1.dp, color = cardBorderColor, shape = RoundedCornerShape(24.dp))
                .padding(32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(brush = logoBacklight)
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logosignumtransp),
                        contentDescription = stringResource(id = R.string.logo_content_description),
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(90.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(stringResource(id = R.string.email_label)) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(stringResource(id = R.string.password_label)) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(elevation = 10.dp, shape = RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(brush = Brush.horizontalGradient(colors = btnGradient))
                        .clickable(enabled = !isLoading) {
                            if (email.isBlank() || password.isBlank()) {
                                errorMessage = emptyErrorText
                                return@clickable
                            }
                            errorMessage = ""
                            isLoading = true
                            onLoginClick(email, password) { err ->
                                isLoading = false
                                errorMessage = err
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(id = R.string.login_button),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
