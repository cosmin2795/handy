package com.handy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.handy.auth.AuthViewModel
import com.handy.ui.home.HomeScreen
import com.handy.ui.login.LoginScreen
import com.handy.ui.theme.HandyTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    HandyTheme {
        val navController = rememberNavController()
        val authViewModel: AuthViewModel = koinViewModel()
        val uiState by authViewModel.uiState.collectAsState()

        val startDestination = if (uiState.user != null) "home" else "login"

        NavHost(
            navController = navController,
            startDestination = startDestination,
        ) {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                )
            }
            composable("home") {
                HomeScreen(
                    onSignOut = {
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                )
            }
        }
    }
}
