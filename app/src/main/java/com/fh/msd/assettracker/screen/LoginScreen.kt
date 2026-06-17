package com.fh.msd.assettracker.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.fh.msd.assettracker.R
import com.fh.msd.assettracker.composables.*
import com.fh.msd.assettracker.ui.theme.AssetTrackerTheme
import com.fh.msd.assettracker.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val toastMessage by authViewModel.toastMessage.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Logo",
        )
        Text(
            text = stringResource(R.string.tv_login_title_text),
            modifier = Modifier
                .padding(top = dimensionResource(R.dimen.tv_title_margintop))
                .padding(dimensionResource(R.dimen.tv_padding)),
            fontSize = dimensionResource(R.dimen.tv_textsize).value.sp,
            fontWeight = FontWeight.Bold
        )

        EditText(
            email,
            { email = it },
            dimensionResource(R.dimen.et_padding),
            stringResource(R.string.et_login_email_hint),
            IconData(Icons.Filled.Email, "Email Icon")
        )

        PasswordText(
            password,
            { password = it },
            dimensionResource(R.dimen.et_padding),
            stringResource(R.string.et_login_password_hint)
        )

        ActionButton(
            { authViewModel.login(email, password) },
            stringResource(R.string.btn_login_login_text)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = dimensionResource(R.dimen.tv_title_margintop))
                .padding(dimensionResource(R.dimen.tv_padding)),
        ) {
            Text(
                stringResource(R.string.tv_login_donthaveaccount_text),
                fontSize = dimensionResource(R.dimen.tv_textsize).value.sp
            )
            SecondaryButton(
                { navController.navigate("register") },
                stringResource(R.string.tv_login_register_text),
                Modifier.align(Alignment.CenterVertically)
            )
        }

        SecondaryButton(
            { navController.navigate("forgot_password") },
            stringResource(R.string.tv_login_forgotpassword_text),
            Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    AssetTrackerTheme {
        LoginScreen(navController = rememberNavController())
    }
}
