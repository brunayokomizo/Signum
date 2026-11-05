package com.signum.app.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.signum.app.MainActivity
import com.signum.app.domain.model.UserRole
import com.signum.app.presentation.admin.*
import com.signum.app.presentation.auth.LoginScreen
import com.signum.app.presentation.signer.SignerHomeScreen
import com.signum.app.presentation.signer.SignerViewModel
import com.signum.app.presentation.signer.SigningScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SignumNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val adminViewModel = remember {
        AdminViewModel(
            MainActivity.appContainer.userDao,
            MainActivity.appContainer.database.documentDao(),
            MainActivity.appContainer.signatureFieldDao,
            MainActivity.appContainer.documentRepository
        )
    }

    val signerViewModel = remember {
        SignerViewModel(
            MainActivity.appContainer.database.documentDao(),
            MainActivity.appContainer.signatureFieldDao
        )
    }

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onLoginClick = { email, pass, onError ->
                    scope.launch(Dispatchers.IO) {
                        val user = MainActivity.appContainer.userRepository.authenticate(email, pass)
                        withContext(Dispatchers.Main) {
                            if (user != null) {
                                signerViewModel.loggedInSignerName = user.name
                                signerViewModel.loggedInSignerEmail = user.email
                                if (user.role == UserRole.ADMIN) {
                                    navController.navigate("adminHome") { popUpTo("login") { inclusive = true } }
                                } else {
                                    navController.navigate("signerHome") { popUpTo("login") { inclusive = true } }
                                }
                            } else {
                                onError("Credenciais inválidas ou utilizador não cadastrado pelo Administrador.")
                            }
                        }
                    }
                }
            )
        }

        composable("adminHome") {
            val recentDocs by adminViewModel.getAdminDocuments().collectAsState(initial = emptyList())
            AdminHomeScreen(
                recentDocuments = recentDocs,
                onAddNewDocumentClick = {
                    adminViewModel.resetDraftFlow()
                    signerViewModel.isDemoDocSigned = false
                    signerViewModel.signedSignerNames.clear()
                    navController.navigate("documentSetup")
                },
                onRegisterSignerClick = { navController.navigate("registerSigner") },
                onDocumentClick = { _, title ->
                    adminViewModel.currentDocumentTitle = title
                    navController.navigate("documentTracking")
                },
                onDeleteDocumentClick = { docId ->
                    adminViewModel.deleteDocument(docId)
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("registerSigner") {
            SignerRegistrationScreen(
                onSaveSigner = { name, email, phone, pass ->
                    adminViewModel.registerOrUpdateSigner(
                        fullName = name,
                        email = email,
                        phone = phone,
                        rawPassword = pass
                    )
                },
                onSaveClick = { navController.popBackStack() }
            )
        }

        composable("documentSetup") {
            val signers by adminViewModel.allSigners.collectAsState(initial = emptyList())
            DocumentSetupScreen(
                signersList = signers,
                onAddSignerUser = { name, email, phone, pass ->
                    adminViewModel.registerOrUpdateSigner(
                        fullName = name,
                        email = email,
                        phone = phone,
                        rawPassword = pass
                    )
                },
                onNextStepClick = { title, assignedSigners ->
                    adminViewModel.currentDocumentTitle = title
                    adminViewModel.currentSignersList.clear()
                    adminViewModel.currentSignersList.addAll(assignedSigners)
                    adminViewModel.currentSignersInfo = assignedSigners.joinToString("\n") { "• ${it.name} (${it.role})" }
                    navController.navigate("signaturePlacement")
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("signaturePlacement") {
            SignaturePlacementScreen(
                onSendDocument = { fieldsSummary ->
                    adminViewModel.currentSignaturePagesInfo = fieldsSummary
                    navController.navigate("documentReview")
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("documentReview") {
            DocumentReviewScreen(
                contractName = adminViewModel.currentDocumentTitle,
                signersInfo = adminViewModel.currentSignersInfo,
                signaturePagesInfo = adminViewModel.currentSignaturePagesInfo,
                onConfirmAndSend = { inviteMessage ->
                    adminViewModel.saveDocumentFlow(
                        context = context,
                        title = adminViewModel.currentDocumentTitle,
                        pdfUri = null,
                        signers = emptyList<SignerSelection>(),
                        fields = emptyList(),
                        onSuccess = {
                            navController.navigate("documentTracking") {
                                popUpTo("adminHome")
                            }
                        },
                        onError = {}
                    )
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("documentTracking") {
            DocumentTrackingScreen(
                documentTitle = adminViewModel.currentDocumentTitle,
                signersList = adminViewModel.currentSignersList.toList(),
                signedSignerNames = signerViewModel.signedSignerNames.toList(),
                isClientSigned = signerViewModel.isDemoDocSigned,
                onBackToHome = {
                    navController.popBackStack("adminHome", inclusive = false)
                }
            )
        }

        composable("signerHome") {
            val allSignerDocs by signerViewModel.getPendingDocumentsForSigner().collectAsState(initial = emptyList())
            SignerHomeScreen(
                documentsList = allSignerDocs,
                isSignedState = signerViewModel.isDemoDocSigned,
                signerName = signerViewModel.loggedInSignerName,
                onOpenDocumentToSign = {
                    navController.navigate("signingScreen")
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("signingScreen") {
            SigningScreen(
                onSignCompleted = {
                    navController.popBackStack()
                },
                onSaveSignature = { bitmap ->
                    val signerName = signerViewModel.loggedInSignerName.ifBlank { "Ana Aparecida Lisboa" }
                    signerViewModel.completeSignature(
                        context = context,
                        documentId = "doc_123",
                        signerName = signerName,
                        signatureBitmap = bitmap,
                        onComplete = {}
                    )
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
