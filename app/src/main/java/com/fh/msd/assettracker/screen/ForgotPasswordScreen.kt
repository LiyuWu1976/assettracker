package com.fh.msd.assettracker.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
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
fun ForgotPasswordScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val toastMessage by authViewModel.toastMessage.collectAsState()
    var email by rememberSaveable { mutableStateOf("") }

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
        Header({ navController.popBackStack() }, stringResource(R.string.tv_forgotpassword_header_text))
        Text(
            stringResource(R.string.tv_forgotpassword_description_text),
            modifier = Modifier
                .padding(top = dimensionResource(R.dimen.tv_title_margintop))
                .padding(dimensionResource(R.dimen.tv_padding)),
            fontSize = dimensionResource(R.dimen.tv_textsize).value.sp,
        )

        EditText(
            email,
            { email = it },
            dimensionResource(R.dimen.et_padding),
            stringResource(R.string.et_login_email_hint)
        )
        ActionButton(
            { authViewModel.forgotPassword(email) },
            stringResource(R.string.btn_forgotpassword_submit_text)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    AssetTrackerTheme {
        ForgotPasswordScreen(navController = rememberNavController())
    }
}
