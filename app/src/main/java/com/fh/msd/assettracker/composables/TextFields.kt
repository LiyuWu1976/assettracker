package com.fh.msd.assettracker.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import com.fh.msd.assettracker.R

data class IconData(val imageVector: ImageVector, val contentDescription: String)

@Composable
fun EditText(
    value: String,
    onChange: (text: String) -> Unit,
    padding: Dp,
    placeholderText: String,
    iconData: IconData? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onChange(it) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(padding),
        placeholder = { Text(placeholderText) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedPlaceholderColor = Color.Black,
            focusedBorderColor = colorResource(R.color.actionButtonColor),
            unfocusedBorderColor = Color.Gray
        ),
        leadingIcon = if (iconData != null) {
            {
                Icon(iconData.imageVector, iconData.contentDescription)
            }
        } else null)
}

@Composable
fun PasswordText(
    value: String, onChange: (text: String) -> Unit, padding: Dp, placeholderText: String
) {
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = { onChange(it) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(padding),
        placeholder = { Text(placeholderText) },
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedPlaceholderColor = Color.Black,
            focusedIndicatorColor = colorResource(R.color.actionButtonColor)
        ),
        maxLines = 1,
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        leadingIcon = {
            val image =
                if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            val description = if (passwordVisibility) "Passwort verbergen" else "Passwort anzeigen"

            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                Icon(imageVector = image, contentDescription = description)
            }
        },
    )
}