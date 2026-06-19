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
import androidx.compose.ui.res.colorResource
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
import com.fh.msd.assettracker.viewmodel.AddAssetViewModel
import com.fh.msd.assettracker.viewmodel.CategoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAssetScreen(
    navController: NavController,
    viewModel: AddAssetViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel()
) {
    val context = LocalContext.current
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    val initialStatus = stringResource(R.string.status_in_use)
    val initialCurrency = stringResource(R.string.currency_eur)

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("laptop") }
    var status by remember { mutableStateOf(initialStatus) }
    var currency by remember { mutableStateOf(initialCurrency) }
    var warrantyExpiry by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

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

    val msgAssetSaved = stringResource(R.string.msg_asset_saved)
    val errSaveFailed = stringResource(R.string.err_save_failed)

    LaunchedEffect(saveSuccess) {
        if (saveSuccess == true) {
            Toast.makeText(context, msgAssetSaved, Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        } else if (saveSuccess == false) {
            Toast.makeText(context, errSaveFailed, Toast.LENGTH_SHORT).show()
            viewModel.resetSaveState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_asset_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
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
                                imageVector = Icons.Default.CalendarMonth,
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.btn_cancel))
                }
                Button(
                    onClick = {
                        val priceValue = price.toDoubleOrNull() ?: 0.0
                        viewModel.saveAsset(name, category, priceValue, currency, status, warrantyExpiry)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)
                ) {
                    Text(stringResource(R.string.btn_save))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddAssetPreview() {
    AssetTrackerTheme {
        AddAssetScreen(navController = rememberNavController())
    }
}
