package com.fh.msd.assettracker.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fh.msd.assettracker.R

@Composable
fun Header(onArrowClick: () -> Unit, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.tv_title_margintop)),
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            stringResource(R.string.back_desc),
            modifier = Modifier
                .clickable(onClick = onArrowClick)
                .padding(start = 12.dp)
        )
        Title(text)
    }
}

@Composable
fun Title(text: String) {
    Text(
        text,
        fontSize = dimensionResource(R.dimen.tv_header_textsize).value.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 56.dp)
    )
}