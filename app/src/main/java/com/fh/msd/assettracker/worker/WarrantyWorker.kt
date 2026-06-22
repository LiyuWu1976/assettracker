package com.fh.msd.assettracker.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fh.msd.assettracker.model.Asset
import com.fh.msd.assettracker.utils.NotificationHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.concurrent.TimeUnit

class WarrantyWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d("WarrantyWorker", "Background work started to check for expiring warranties")
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid

        if (uid == null) {
            Log.d("WarrantyWorker", "User not logged in, skipping check")
            return Result.success()
        }

        try {
            // Find the user document
            val userQuery = db.collection("users")
                .whereEqualTo("uid", uid)
                .get()
                .await()

            if (!userQuery.isEmpty) {
                val userDocId = userQuery.documents[0].id

                // Get all assets
                val assetsQuery = db.collection("users")
                    .document(userDocId)
                    .collection("assets")
                    .get()
                    .await()

                val assets = assetsQuery.toObjects(Asset::class.java)
                val notificationHelper = NotificationHelper(applicationContext)

                val today = Calendar.getInstance()

                for (asset in assets) {

                    val expiry = asset.warrantyExpiry ?: continue

                    val diff = expiry.time - today.timeInMillis
                    val daysUntilExpiry = TimeUnit.MILLISECONDS.toDays(diff)

                    Log.d("WarrantyWorker", "Asset '${asset.name}' expires in $daysUntilExpiry days")

                    // Send notification if it expires in exactly 1 day (or close to it)
                    // We check if it's between 0 and 1 day to be safe, depending on when the worker runs.
                    if (daysUntilExpiry <= 1L) {
                        notificationHelper.sendWarrantyExpiryNotification(asset.name)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("WarrantyWorker", "Error checking for expiring warranties", e)
            return Result.retry()
        }

        return Result.success()
    }
}