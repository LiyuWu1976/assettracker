package com.fh.msd.assettracker.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.fh.msd.assettracker.R
import com.fh.msd.assettracker.composables.*
import com.fh.msd.assettracker.ui.theme.AssetTrackerTheme
import com.fh.msd.assettracker.ui.theme.BrandTeal
import com.fh.msd.assettracker.viewmodel.AuthViewModel

private data class RegisterInput(
    val text: String,
    val onChange: (newText: String) -> Unit,
    val placeholderText: String,
    val isPassword: Boolean,
    val iconData: IconData? = null,
)

@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var agreeTermsAndConditions by rememberSaveable { mutableStateOf(false) }

    val toastMessage by authViewModel.toastMessage.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    val inputs = listOf(
        RegisterInput(
            firstName,
            { firstName = it },
            stringResource(R.string.til_register_firstname_hint),
            false,
            IconData(Icons.Filled.Person, stringResource(R.string.person_icon_desc))
        ),
        RegisterInput(
            lastName,
            { lastName = it },
            stringResource(R.string.til_register_lastname_hint),
            false,
            IconData(Icons.Filled.Person, stringResource(R.string.person_icon_desc))
        ),
        RegisterInput(
            email,
            { email = it },
            stringResource(R.string.et_login_email_hint),
            false,
            IconData(Icons.Filled.Email, stringResource(R.string.email_icon_desc))
        ),
        RegisterInput(
            password,
            { password = it },
            stringResource(R.string.et_login_password_hint),
            true,
        ),
        RegisterInput(
            confirmPassword,
            { confirmPassword = it },
            stringResource(R.string.til_register_confirmpassword_hint),
            true,
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header({ navController.popBackStack() }, stringResource(R.string.tv_register_header_text))
        inputs.forEach {
            if (it.isPassword) {
                PasswordText(
                    it.text,
                    it.onChange,
                    dimensionResource(R.dimen.et_padding),
                    it.placeholderText
                )
            } else {
                EditText(
                    it.text,
                    it.onChange,
                    dimensionResource(R.dimen.et_padding),
                    it.placeholderText,
                    it.iconData
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                agreeTermsAndConditions,
                { agreeTermsAndConditions = it },
                colors = CheckboxDefaults.colors().copy(
                    checkedBorderColor = BrandTeal,
                    checkedBoxColor = BrandTeal
                )
            )
            Text(stringResource(R.string.cb_register_termsandcondition_text).substring(0, 15))
            Text(
                stringResource(R.string.cb_register_termsandcondition_text).substring(15),
                textDecoration = TextDecoration.Underline
            )
        }

        ActionButton(
            {
                authViewModel.register(
                    firstName, lastName, email, password, confirmPassword, agreeTermsAndConditions
                )
            },
            stringResource(R.string.btn_register_register_text)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(dimensionResource(R.dimen.tv_padding)),
        ) {
            Text(stringResource(R.string.tv_register_alreadyhaveanaccount_text))
            SecondaryButton(
                { navController.navigate("login") { popUpTo("login") { inclusive = true } } },
                stringResource(R.string.tv_register_login_text),
                Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    AssetTrackerTheme {
        RegisterScreen(navController = rememberNavController())
    }
}
