package com.fh.msd.assettracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Logout
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fh.msd.assettracker.model.Asset
import com.fh.msd.assettracker.ui.theme.AssetTrackerTheme
import com.fh.msd.assettracker.ui.theme.Teal700
import com.fh.msd.assettracker.ui.theme.Teal200
import com.fh.msd.assettracker.ui.theme.LightGrey
import com.fh.msd.assettracker.viewmodel.AssetViewModel

class AssetCollectionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AssetTrackerTheme {
                AssetCollectionScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetCollectionScreen(viewModel: AssetViewModel = viewModel()) {
    val context = LocalContext.current
    val assets by viewModel.assets.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf("All", "Lenses", "Boards", "Cables", "Software")
    var selectedCategory by remember { mutableStateOf("All") }

    LaunchedEffect(Unit) {
        viewModel.fetchAssets()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.asset_collection_title)) },
                actions = {
                    IconButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, AddAssetActivity::class.java)
                    context.startActivity(intent)
                },
                containerColor = Teal700,
                contentColor = Color.White,
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Asset")
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
                    focusedContainerColor = LightGrey,
                    unfocusedContainerColor = LightGrey,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Categories
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Teal700,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Asset List
            if (assets.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_assets_msg),
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(assets) { asset ->
                        AssetCard(asset, onEdit = {
                            val intent = Intent(context, EditAssetActivity::class.java).apply {
                                putExtra("ASSET_ID", asset.id)
                                putExtra("ASSET_NAME", asset.name)
                                putExtra("ASSET_PRICE", asset.price)
                                putExtra("ASSET_CATEGORY", asset.category)
                                putExtra("ASSET_STATUS", asset.status)
                                putExtra("ASSET_CURRENCY", asset.currency)
                            }
                            context.startActivity(intent)
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun AssetCard(asset: Asset, onEdit: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image Placeholder
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Teal200, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = Color.DarkGray
                    )
                    Text(
                        stringResource(R.string.image_placeholder),
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
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
                            tint = Teal700
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            stringResource(R.string.edit_button),
                            color = Teal700,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


