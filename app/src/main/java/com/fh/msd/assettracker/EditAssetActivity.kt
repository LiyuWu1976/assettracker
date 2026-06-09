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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fh.msd.assettracker.composables.DropdownField
import com.fh.msd.assettracker.ui.theme.AssetTrackerTheme
import com.fh.msd.assettracker.viewmodel.CategoryViewModel
import com.fh.msd.assettracker.viewmodel.EditAssetViewModel

class EditAssetActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val assetId = intent.getStringExtra("ASSET_ID") ?: ""
        val assetName = intent.getStringExtra("ASSET_NAME") ?: ""
        val assetPrice = intent.getDoubleExtra("ASSET_PRICE", 0.0)
        val assetCategory = intent.getStringExtra("ASSET_CATEGORY") ?: "Laptop"
        val assetStatus = intent.getStringExtra("ASSET_STATUS") ?: "In Use"
        val assetCurrency = intent.getStringExtra("ASSET_CURRENCY") ?: "EUR"

        setContent {
            AssetTrackerTheme {
                EditAssetScreen(
                    assetId = assetId,
                    initialName = assetName,
                    initialPrice = assetPrice,
                    initialCategory = assetCategory,
                    initialStatus = assetStatus,
                    initialCurrency = assetCurrency,
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAssetScreen(
    viewModel: EditAssetViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel(),
    assetId: String,
    initialName: String,
    initialPrice: Double,
    initialCategory: String,
    initialStatus: String,
    initialCurrency: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val updateSuccess by viewModel.updateSuccess.collectAsState()
    val deleteSuccess by viewModel.deleteSuccess.collectAsState()

    var name by remember { mutableStateOf(initialName) }
    var price by remember { mutableStateOf(initialPrice.toString()) }
    var category by remember { mutableStateOf(initialCategory) }
    var status by remember { mutableStateOf(initialStatus) }
    var currency by remember { mutableStateOf(initialCurrency) }

    var showCancelDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    //val categories = listOf("Laptop", "phone", "Board", "Camera", "lens", "Cable", "Software")
    val categories by categoryViewModel.categories.collectAsState()
    val statuses = listOf("On Shelf", "In Use", "Loaned", "Maintaining", "Lost/Retired")
    val currencies = listOf("EUR", "USD")

    LaunchedEffect(updateSuccess) {
        if (updateSuccess == true) {
            Toast.makeText(context, context.getString(R.string.msg_asset_updated), Toast.LENGTH_SHORT).show()
            onBack()
        } else if (updateSuccess == false) {
            Toast.makeText(context, context.getString(R.string.err_update_failed), Toast.LENGTH_SHORT).show()
            viewModel.resetStates()
        }
    }

    LaunchedEffect(deleteSuccess) {
        if (deleteSuccess == true) {
            Toast.makeText(context, context.getString(R.string.msg_asset_deleted), Toast.LENGTH_SHORT).show()
            onBack()
        } else if (deleteSuccess == false) {
            Toast.makeText(context, context.getString(R.string.err_delete_failed), Toast.LENGTH_SHORT).show()
            viewModel.resetStates()
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text(stringResource(R.string.confirm_cancel_title)) },
            text = { Text(stringResource(R.string.confirm_cancel_msg)) },
            confirmButton = {
                TextButton(onClick = { 
                    showCancelDialog = false
                    onBack()
                }) {
                    Text(stringResource(R.string.btn_yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text(stringResource(R.string.btn_no))
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.confirm_delete_title)) },
            text = { Text(stringResource(R.string.confirm_delete_msg)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteAsset(assetId)
                }) {
                    Text(stringResource(R.string.btn_yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.btn_no))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_asset_title)) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (name != initialName || price != initialPrice.toString() || category != initialCategory || status != initialStatus || currency != initialCurrency) {
                            showCancelDialog = true
                        } else {
                            onBack()
                        }
                    }) {
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

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val priceValue = price.toDoubleOrNull() ?: 0.0
                    viewModel.updateAsset(assetId, name, category, priceValue, currency, status)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.actionButtonColor))
            ) {
                Text(stringResource(R.string.btn_save))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (name != initialName || price != initialPrice.toString() || category != initialCategory || status != initialStatus || currency != initialCurrency) {
                            showCancelDialog = true
                        } else {
                            onBack()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.btn_cancel))
                }
                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.btn_delete))
                }
            }
        }
    }
}
