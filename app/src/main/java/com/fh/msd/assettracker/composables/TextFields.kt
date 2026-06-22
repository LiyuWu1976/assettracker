package com.fh.msd.assettracker.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.fh.msd.assettracker.R
import com.fh.msd.assettracker.ui.theme.BrandTeal
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp

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
            focusedBorderColor = BrandTeal,
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
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandTeal,
            unfocusedBorderColor = Color.Gray
        ),
        maxLines = 1,
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        leadingIcon = {
            val image =
                if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            val description = if (passwordVisibility) stringResource(R.string.hide_password_desc) else stringResource(R.string.show_password_desc)

            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                Icon(imageVector = image, contentDescription = description)
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
