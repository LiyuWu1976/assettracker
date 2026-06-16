package com.fh.msd.assettracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fh.msd.assettracker.model.Asset
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AddAssetViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _saveSuccess = MutableStateFlow<Boolean?>(null)
    val saveSuccess = _saveSuccess.asStateFlow()

    fun saveAsset(name: String, category: String, price: Double, currency: String, status: String, warrantyExpiry: Long?) {
        val uid = auth.currentUser?.uid ?: return
        
        val asset = hashMapOf(
            "name" to name,
            "category" to category,
            "price" to price,
            "currency" to currency,
            "status" to status,
            "location" to "", // Added field as per latest structure
            "warrantyExpiry" to if (warrantyExpiry != null) java.util.Date(warrantyExpiry) else null
        )

        viewModelScope.launch {
            try {
                // 1. Find the user document using the 'uid' field
                val userQuery = db.collection("users")
                    .whereEqualTo("uid", uid)
                    .get()
                    .await()

                if (!userQuery.isEmpty) {
                    // 2. Get the document ID of the existing user document
                    val userDocId = userQuery.documents[0].id
                    
                    // 3. Create the 'assets' collection under this specific document
                    db.collection("users")
                        .document(userDocId)
                        .collection("assets")
                        .add(asset)
                        .await()
                    _saveSuccess.value = true
                } else {
                    // User document not found with this uid
                    _saveSuccess.value = false
                }
            } catch (e: Exception) {
                _saveSuccess.value = false
            }
        }
    }

    fun resetSaveState() {
        _saveSuccess.value = null
    }
}
