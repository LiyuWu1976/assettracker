package com.fh.msd.assettracker

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.fh.msd.assettracker.composables.ActionButton
import com.fh.msd.assettracker.composables.EditText
import com.fh.msd.assettracker.composables.Header
import com.fh.msd.assettracker.ui.theme.AssetTrackerTheme

@Composable
fun ForgotPasswordPage(
    modifier: Modifier = Modifier,
    toastMessage: String?,
    resetPassword: (email: String) -> Unit,
    back: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Header(back, stringResource(R.string.tv_forgotpassword_header_text))
        Text(
            stringResource(R.string.tv_forgotpassword_description_text),
            modifier = Modifier
                .padding(
                    top = dimensionResource(R.dimen.tv_title_margintop)
                )
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
            { resetPassword(email) },
            stringResource(R.string.btn_forgotpassword_submit_text)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordPagePreview() {
    AssetTrackerTheme {
        ForgotPasswordPage(
            toastMessage = null,
            resetPassword = {},
            back = {}
        )
    }
}