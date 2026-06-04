package com.fh.msd.assettracker.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import com.fh.msd.assettracker.R

@Composable
fun ActionButton(onClick: () -> Unit, text: String) {
    Button(
        colors = ButtonColors(
            containerColor = colorResource(R.color.actionButtonColor),
            contentColor = Color.Red,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.Black
        ),
        onClick = { onClick() },
        modifier = Modifier
            .padding(dimensionResource(R.dimen.btn_margin))
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.btn_padding))
    ) {
        Text(text, color = Color.White, fontWeight = FontWeight.Bold)
    }
}