package com.fh.msd.assettracker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fh.msd.assettracker.composables.DropdownField
import com.fh.msd.assettracker.ui.theme.AssetTrackerTheme
import com.fh.msd.assettracker.viewmodel.AddAssetViewModel

class AddAssetActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AssetTrackerTheme {
                AddAssetScreen(onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAssetScreen(viewModel: AddAssetViewModel = viewModel(), onBack: () -> Unit) {
    val context = LocalContext.current
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Laptop") }
    var status by remember { mutableStateOf("In Use") }
    var currency by remember { mutableStateOf("EUR") }

    val categories = listOf("Laptop", "Phone", "Board", "Camera", "lens", "Cable", "Software")
    val statuses = listOf("On Shelf", "In Use", "Loaned", "Maintaining", "Lost/Retired")
    val currencies = listOf("EUR", "USD")

    LaunchedEffect(saveSuccess) {
        if (saveSuccess == true) {
            Toast.makeText(context, context.getString(R.string.msg_asset_saved), Toast.LENGTH_SHORT).show()
            onBack()
        } else if (saveSuccess == false) {
            Toast.makeText(context, context.getString(R.string.err_save_failed), Toast.LENGTH_SHORT).show()
            viewModel.resetSaveState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_asset_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DropdownField(
                label = stringResource(R.string.asset_category),
                options = categories,
                selectedOption = category,
                onOptionSelected = { category = it }
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.asset_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text(stringResource(R.string.asset_price)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            DropdownField(
                label = stringResource(R.string.asset_currency),
                options = currencies,
                selectedOption = currency,
                onOptionSelected = { currency = it }
            )
            DropdownField(
                label = stringResource(R.string.asset_status),
                options = statuses,
                selectedOption = status,
                onOptionSelected = { status = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.btn_cancel))
                }
                Button(
                    onClick = {
                        val priceValue = price.toDoubleOrNull() ?: 0.0
                        viewModel.saveAsset(name, category, priceValue, currency, status)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.actionButtonColor))
                ) {
                    Text(stringResource(R.string.btn_save))
                }
            }
        }
    }
}
