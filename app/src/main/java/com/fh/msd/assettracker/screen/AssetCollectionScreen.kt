package com.fh.msd.assettracker.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.fh.msd.assettracker.R
import com.fh.msd.assettracker.model.Asset
import com.fh.msd.assettracker.ui.theme.*
import com.fh.msd.assettracker.viewmodel.AssetViewModel
import com.fh.msd.assettracker.viewmodel.AuthViewModel
import com.fh.msd.assettracker.viewmodel.CategoryViewModel
import com.fh.msd.assettracker.viewmodel.SettingsViewModel
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetCollectionScreen(
    navController: NavController,
    assetViewModel: AssetViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val assets by assetViewModel.assets.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
    val currentLanguage by settingsViewModel.currentLanguage.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    
    var searchQuery by remember { mutableStateOf("") }
    val allCategoryLabel = stringResource(R.string.cat_all)
    var selectedCategory by remember { mutableStateOf(allCategoryLabel) }

    // Derived state for filtered assets
    val filteredAssets by remember {
        derivedStateOf {
            assets.filter { asset ->
                val matchesCategory = if (selectedCategory == allCategoryLabel) true else asset.category == selectedCategory
                val matchesSearch = if (searchQuery.isEmpty()) true else asset.name.contains(searchQuery, ignoreCase = true)
                matchesCategory && matchesSearch
            }
        }
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(stringResource(R.string.dialog_select_language)) },
            text = {
                Column {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.lang_en)) },
                        onClick = {
                            settingsViewModel.changeLanguage("en")
                            showLanguageDialog = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.lang_de)) },
                        onClick = {
                            settingsViewModel.changeLanguage("de")
                            showLanguageDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.asset_collection_title)) },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = stringResource(R.string.menu_setup)
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            // Theme Toggle
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (isDarkMode) stringResource(R.string.menu_day_mode)
                                        else stringResource(R.string.menu_night_mode)
                                    )
                                },
                                onClick = {
                                    settingsViewModel.toggleTheme()
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        if (isDarkMode) Icons.Default.WbSunny else Icons.Default.Nightlight,
                                        contentDescription = null
                                    )
                                }
                            )
                            
                            // Language Selection
                            DropdownMenuItem(
                                text = {
                                    Text("${stringResource(R.string.menu_language)}: ${if (currentLanguage == "en") stringResource(R.string.lang_en) else stringResource(R.string.lang_de)}")
                                },
                                onClick = {
                                    showLanguageDialog = true
                                    showMenu = false
                                }
                            )
                            
                            HorizontalDivider()
                            
                            // Logout
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        stringResource(R.string.logout_desc),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    authViewModel.logout()
                                    navController.navigate("login") {
                                        popUpTo(0)
                                    }
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Logout,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("add_asset")
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_asset_desc))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.search_hint)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Categories
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val categoriesWithAll = listOf(allCategoryLabel) + categories
                items(categoriesWithAll) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Asset List
            if (filteredAssets.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (assets.isEmpty()) stringResource(R.string.no_assets_msg) else stringResource(R.string.no_matches_msg),
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredAssets) { asset ->
                        AssetCard(asset, onEdit = {
                            val warrantyTime = asset.warrantyExpiry?.time ?: -1L
                            navController.navigate("edit_asset/${asset.id}/${asset.name}/${asset.price}/${asset.category}/${asset.status}/${asset.currency}/$warrantyTime")
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun AssetCard(asset: Asset, onEdit: () -> Unit) {
    val context = LocalContext.current
    val categoryIcon = remember(asset.category) {
        val name = asset.category.lowercase().trim()
        context.resources.getIdentifier(name, "drawable", context.packageName)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon or Placeholder
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (categoryIcon != 0) {
                    Image(
                        painter = painterResource(id = categoryIcon),
                        contentDescription = asset.category,
                        modifier = Modifier.size(100.dp)
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = Color.DarkGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Asset Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = asset.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = asset.category,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = asset.status,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomEnd) {
                    TextButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            stringResource(R.string.edit_button),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AssetCollectionPreview() {
    AssetTrackerTheme {
        AssetCollectionScreen(navController = rememberNavController())
    }
}
