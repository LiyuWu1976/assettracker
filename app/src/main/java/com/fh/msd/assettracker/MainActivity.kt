package com.fh.msd.assettracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fh.msd.assettracker.ui.theme.AssetTrackerTheme
import com.fh.msd.assettracker.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AssetTrackerTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize(), containerColor = Color.White, contentColor = Color.Black
                ) { innerPadding ->
                    Root(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

enum class Pages {
    LOGIN,
    REGISTER,
    FORGOT_PASSWORD
}

@Composable
fun Root(modifier: Modifier = Modifier) {
    val authViewModel: AuthViewModel = viewModel()
    val toastMessage by authViewModel.toastMessage.collectAsState()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            val intent = Intent(context, AssetCollectionActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }

    LaunchedEffect(Unit) {
        authViewModel.navigateToHome.collect { navigate ->
            if (navigate) {
                val intent = Intent(context, AssetCollectionActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            }
        }
    }

    val list = remember { mutableStateListOf(Pages.LOGIN) }
    val currentPage = list.last()

    when (currentPage) {
        Pages.LOGIN -> LoginPage(
            modifier,
            toRegister = { list.add(Pages.REGISTER) },
            toForgotPassword = { list.add(Pages.FORGOT_PASSWORD) },
            toastMessage = toastMessage,
            login = { email, password -> authViewModel.login(email, password) }
        )


        Pages.REGISTER -> RegisterPage(
            modifier,
            toLogin = { list.add(Pages.LOGIN) },
            toastMessage = toastMessage,
            register = { firstName, lastName, email, password, confirmPassword, agreeTermsAndConditions ->
                authViewModel.register(
                    firstName,
                    lastName,
                    email,
                    password,
                    confirmPassword,
                    agreeTermsAndConditions
                )
            },
            back = { list.removeAt(list.size - 1) }
        )

        Pages.FORGOT_PASSWORD -> ForgotPasswordPage(
            modifier,
            toastMessage = toastMessage,
            resetPassword = { email -> authViewModel.forgotPassword(email) },
            back = { list.removeAt(list.size - 1) }
        )
    }

}
