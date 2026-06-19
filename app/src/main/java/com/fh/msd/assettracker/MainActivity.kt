package com.fh.msd.assettracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.*
import com.fh.msd.assettracker.screen.*
import com.fh.msd.assettracker.ui.theme.AssetTrackerTheme
import com.fh.msd.assettracker.viewmodel.AuthViewModel
import com.fh.msd.assettracker.worker.WarrantyWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            val permissionName = it.key
            val isGranted = it.value
            if (isGranted) {
                Log.d("MainActivity", "$permissionName granted")
            } else {
                Log.d("MainActivity", "$permissionName denied")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkNotificationPermission()
        scheduleWarrantyCheck()
        setContent {
            AssetTrackerTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()
                val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
                var isSplashFinished by remember { mutableStateOf(false) }

                LaunchedEffect(isLoggedIn, isSplashFinished) {
                    Log.d("MainActivity", "isLoggedIn change detected: $isLoggedIn, splashFinished: $isSplashFinished")
                    if (!isSplashFinished) return@LaunchedEffect

                    if (isLoggedIn) {
                        triggerImmediateWarrantyCheck()
                        if (navController.currentDestination?.route != "collection") {
                            navController.navigate("collection") {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    } else {
                        if (navController.currentDestination?.route != "login" &&
                            navController.currentDestination?.route != "register" &&
                            navController.currentDestination?.route != "forgot_password") {
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.White,
                    contentColor = Color.Black
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "splash",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("splash") {
                            SplashScreen(onAnimationFinished = {
                                isSplashFinished = true
                            })
                        }
                        composable("login") {
                            LoginScreen(navController, authViewModel)
                        }
                        composable("register") {
                            RegisterScreen(navController, authViewModel)
                        }
                        composable("forgot_password") {
                            ForgotPasswordScreen(navController, authViewModel)
                        }
                        composable("collection") {
                            AssetCollectionScreen(navController, authViewModel = authViewModel)
                        }
                        composable("add_asset") {
                            AddAssetScreen(navController)
                        }
                        composable(
                            route = "edit_asset/{id}/{name}/{price}/{category}/{status}/{currency}/{warrantyTime}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.StringType },
                                navArgument("name") { type = NavType.StringType },
                                navArgument("price") { type = NavType.FloatType },
                                navArgument("category") { type = NavType.StringType },
                                navArgument("status") { type = NavType.StringType },
                                navArgument("currency") { type = NavType.StringType },
                                navArgument("warrantyTime") { type = NavType.LongType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: ""
                            val name = backStackEntry.arguments?.getString("name") ?: ""
                            val price = backStackEntry.arguments?.getFloat("price")?.toDouble() ?: 0.0
                            val category = backStackEntry.arguments?.getString("category") ?: ""
                            val status = backStackEntry.arguments?.getString("status") ?: ""
                            val currency = backStackEntry.arguments?.getString("currency") ?: ""
                            val warrantyTime = backStackEntry.arguments?.getLong("warrantyTime") ?: -1L
                            val warrantyExpiry = if (warrantyTime != -1L) warrantyTime else null

                            EditAssetScreen(
                                navController = navController,
                                assetId = id,
                                initialName = name,
                                initialPrice = price,
                                initialCategory = category,
                                initialStatus = status,
                                initialCurrency = currency,
                                initialWarrantyExpiry = warrantyExpiry
                            )
                        }
                    }
                }
            }
        }
    }

    private fun scheduleWarrantyCheck() {
        val warrantyWorkRequest = PeriodicWorkRequestBuilder<WarrantyWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(5, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "WarrantyExpiryCheck",
            ExistingPeriodicWorkPolicy.KEEP,
            warrantyWorkRequest
        )
    }

    private fun triggerImmediateWarrantyCheck() {
        val immediateRequest = OneTimeWorkRequestBuilder<WarrantyWorker>()
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "ImmediateWarrantyCheck",
            ExistingWorkPolicy.REPLACE,
            immediateRequest
        )
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionsLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
            }
        }
    }
}
