package com.fh.msd.assettracker.composables

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.fh.msd.assettracker.R

@Composable
fun SecondaryButton(onClick: () -> Unit, text: String, modifier: Modifier = Modifier) {
    TextButton(
        onClick,
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
        modifier = modifier
    ) {
        Text(
            text,
            textDecoration = TextDecoration.Underline,
            fontSize = dimensionResource(R.dimen.tv_textsize).value.sp,
        )
    }
}