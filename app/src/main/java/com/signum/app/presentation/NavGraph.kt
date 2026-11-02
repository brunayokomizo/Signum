package com.signum.app.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.signum.app.presentation.admin.*
import com.signum.app.presentation.auth.LoginScreen
import com.signum.app.presentation.signer.SignerHomeScreen
import com.signum.app.presentation.signer.SigningScreen

@Composable
fun SignumNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onLoginSuccess = { isAdmin ->
                    if (isAdmin) {
                        navController.navigate("adminHome") { popUpTo("login") { inclusive = true } }
                    } else {
                        navController.navigate("signerHome") { popUpTo("login") { inclusive = true } }
                    }
                }
            )
        }

        composable("adminHome") {
            AdminHomeScreen(
                onAddNewDocumentClick = { navController.navigate("documentSetup") },
                onRegisterSignerClick = { navController.navigate("registerSigner") },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("registerSigner") {
            SignerRegistrationScreen(
                onSaveClick = { navController.popBackStack() }
            )
        }

        composable("documentSetup") {
            DocumentSetupScreen(
                onNextStepClick = { navController.navigate("signaturePlacement") },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("signaturePlacement") {
            SignaturePlacementScreen(
                onSendDocument = { navController.navigate("documentReview") },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("documentReview") {
            DocumentReviewScreen(
                onConfirmAndSend = {
                    navController.navigate("documentTracking") {
                        popUpTo("adminHome")
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("documentTracking") {
            DocumentTrackingScreen(
                onBackToHome = {
                    navController.popBackStack("adminHome", inclusive = false)
                }
            )
        }

        composable("signerHome") {
            SignerHomeScreen(
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
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
