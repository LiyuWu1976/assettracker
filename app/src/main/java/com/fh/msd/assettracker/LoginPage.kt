package com.fh.msd.assettracker

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.fh.msd.assettracker.composables.ActionButton
import com.fh.msd.assettracker.composables.EditText
import com.fh.msd.assettracker.composables.IconData
import com.fh.msd.assettracker.composables.PasswordText
import com.fh.msd.assettracker.composables.SecondaryButton
import com.fh.msd.assettracker.ui.theme.AssetTrackerTheme

@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    toRegister: () -> Unit,
    toForgotPassword: () -> Unit,
    toastMessage: String?,
    login: (email: String, password: String) -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Logo",
        )
        Text(
            text = stringResource(R.string.tv_login_title_text),
            modifier = Modifier
                .padding(
                    top = dimensionResource(R.dimen.tv_title_margintop)
                )
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
            { login(email, password) }, stringResource(R.string.btn_login_login_text)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(
                    top = dimensionResource(R.dimen.tv_title_margintop)
                )
                .padding(dimensionResource(R.dimen.tv_padding)),
        ) {
            Text(
                stringResource(R.string.tv_login_donthaveaccount_text),
                fontSize = dimensionResource(R.dimen.tv_textsize).value.sp
            )
            SecondaryButton(
                toRegister,
                stringResource(R.string.tv_login_register_text),
                Modifier.align(Alignment.CenterVertically)
            )
        }

        SecondaryButton(
            toForgotPassword,
            stringResource(R.string.tv_login_forgotpassword_text),
            Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPagePreview() {
    AssetTrackerTheme {
        LoginPage(
            toRegister = {},
            toForgotPassword = {},
            toastMessage = null,
            login = { _, _ -> }
        )
    }
}
