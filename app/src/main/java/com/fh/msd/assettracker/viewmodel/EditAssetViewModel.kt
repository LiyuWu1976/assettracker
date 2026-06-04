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

class EditAssetViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _updateSuccess = MutableStateFlow<Boolean?>(null)
    val updateSuccess = _updateSuccess.asStateFlow()

    private val _deleteSuccess = MutableStateFlow<Boolean?>(null)
    val deleteSuccess = _deleteSuccess.asStateFlow()

    fun updateAsset(id: String, name: String, category: String, price: Double, currency: String, status: String) {
        val uid = auth.currentUser?.uid ?: return
        
        val updates = hashMapOf<String, Any>(
            "name" to name,
            "category" to category,
            "price" to price,
            "currency" to currency,
            "status" to status
        )

        viewModelScope.launch {
            try {
                val userQuery = db.collection("users")
                    .whereEqualTo("uid", uid)
                    .get()
                    .await()

                if (!userQuery.isEmpty) {
                    val userDocId = userQuery.documents[0].id
                    db.collection("users")
                        .document(userDocId)
                        .collection("assets")
                        .document(id)
                        .update(updates)
                        .await()
                    _updateSuccess.value = true
                } else {
                    _updateSuccess.value = false
                }
            } catch (e: Exception) {
                _updateSuccess.value = false
            }
        }
    }

    fun deleteAsset(id: String) {
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                val userQuery = db.collection("users")
                    .whereEqualTo("uid", uid)
                    .get()
                    .await()

                if (!userQuery.isEmpty) {
                    val userDocId = userQuery.documents[0].id
                    db.collection("users")
                        .document(userDocId)
                        .collection("assets")
                        .document(id)
                        .delete()
                        .await()
                    _deleteSuccess.value = true
                } else {
                    _deleteSuccess.value = false
                }
            } catch (e: Exception) {
                _deleteSuccess.value = false
            }
        }
    }

    fun resetStates() {
        _updateSuccess.value = null
        _deleteSuccess.value = null
    }
}
