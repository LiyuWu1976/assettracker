package com.fh.msd.assettracker.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.fh.msd.assettracker.R
import com.fh.msd.assettracker.composables.DropdownField
import com.fh.msd.assettracker.ui.theme.AssetTrackerTheme
import com.fh.msd.assettracker.ui.theme.BrandTeal
import com.fh.msd.assettracker.viewmodel.CategoryViewModel
import com.fh.msd.assettracker.viewmodel.EditAssetViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAssetScreen(
    navController: NavController,
    assetId: String,
    initialName: String,
    initialPrice: Double,
    initialCategory: String,
    initialStatus: String,
    initialCurrency: String,
    initialWarrantyExpiry: Long?,
    viewModel: EditAssetViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel()
) {
    val context = LocalContext.current
    val updateSuccess by viewModel.updateSuccess.collectAsState()
    val deleteSuccess by viewModel.deleteSuccess.collectAsState()

    var name by remember { mutableStateOf(initialName) }
    var price by remember { mutableStateOf(initialPrice.toString()) }
    var category by remember { mutableStateOf(initialCategory) }
    var status by remember { mutableStateOf(initialStatus) }
    var currency by remember { mutableStateOf(initialCurrency) }
    var warrantyExpiry by remember { mutableStateOf(initialWarrantyExpiry) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialWarrantyExpiry)
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    var showCancelDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val categories by categoryViewModel.categories.collectAsState()
    val statuses = listOf(
        stringResource(R.string.status_on_shelf),
        stringResource(R.string.status_in_use),
        stringResource(R.string.status_loaned),
        stringResource(R.string.status_maintaining),
        stringResource(R.string.status_lost_retired)
    )
    val currencies = listOf(
        stringResource(R.string.currency_eur),
        stringResource(R.string.currency_usd)
    )

    val isModified = name != initialName || 
                     price != initialPrice.toString() || 
                     category != initialCategory || 
                     status != initialStatus || 
                     currency != initialCurrency ||
                     warrantyExpiry != initialWarrantyExpiry

    val msgAssetUpdated = stringResource(R.string.msg_asset_updated)
    val errUpdateFailed = stringResource(R.string.err_update_failed)
    val msgAssetDeleted = stringResource(R.string.msg_asset_deleted)
    val errDeleteFailed = stringResource(R.string.err_delete_failed)

    LaunchedEffect(updateSuccess) {
        if (updateSuccess == true) {
            Toast.makeText(context, msgAssetUpdated, Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        } else if (updateSuccess == false) {
            Toast.makeText(context, errUpdateFailed, Toast.LENGTH_SHORT).show()
            viewModel.resetStates()
        }
    }

    LaunchedEffect(deleteSuccess) {
        if (deleteSuccess == true) {
            Toast.makeText(context, msgAssetDeleted, Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        } else if (deleteSuccess == false) {
            Toast.makeText(context, errDeleteFailed, Toast.LENGTH_SHORT).show()
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
                    navController.popBackStack()
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
                        if (isModified) {
                            showCancelDialog = true
                        } else {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_desc))
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

            OutlinedTextField(
                value = warrantyExpiry?.let { dateFormatter.format(Date(it)) } ?: "",
                onValueChange = { },
                label = { Text(stringResource(R.string.asset_warranty_expiry)) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = stringResource(R.string.select_date_desc)
                        )
                    }
                }
            )

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            warrantyExpiry = datePickerState.selectedDateMillis
                            showDatePicker = false
                        }) {
                            Text(stringResource(R.string.btn_ok))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text(stringResource(R.string.btn_cancel))
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val priceValue = price.toDoubleOrNull() ?: 0.0
                    viewModel.updateAsset(assetId, name, category, priceValue, currency, status, warrantyExpiry)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)
            ) {
                Text(stringResource(R.string.btn_save))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (isModified) {
                            showCancelDialog = true
                        } else {
                            navController.popBackStack()
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

@Preview(showBackground = true)
@Composable
fun EditAssetPreview() {
    AssetTrackerTheme {
        EditAssetScreen(
            navController = rememberNavController(),
            assetId = "1",
            initialName = "MacBook Pro",
            initialPrice = 2500.0,
            initialCategory = "Laptop",
            initialStatus = "In Use",
            initialCurrency = "EUR",
            initialWarrantyExpiry = 1735689600000L
        )
    }
}
