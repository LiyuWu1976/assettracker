package com.fh.msd.assettracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fh.msd.assettracker.model.Asset
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AssetViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var assetsListener: ListenerRegistration? = null

    private val _assets = MutableStateFlow<List<Asset>>(emptyList())
    val assets: StateFlow<List<Asset>> = _assets.asStateFlow()

    init {
        startListeningToAssets()
    }

    private fun startListeningToAssets() {
        val uid = auth.currentUser?.uid ?: return
        
        viewModelScope.launch {
            try {
                // 1. Find the user document using the 'uid' field
                val userQuery = db.collection("users")
                    .whereEqualTo("uid", uid)
                    .get()
                    .await()

                if (!userQuery.isEmpty) {
                    val userDocId = userQuery.documents[0].id
                    
                    // 2. Set up a real-time listener for the assets sub-collection
                    assetsListener?.remove()
                    assetsListener = db.collection("users")
                        .document(userDocId)
                        .collection("assets")
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                // Handle error
                                return@addSnapshotListener
                            }
                            
                            if (snapshot != null) {
                                val assetList = snapshot.toObjects(Asset::class.java)
                                _assets.value = assetList
                            }
                        }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        assetsListener?.remove()
    }

    fun fetchAssets() {
        startListeningToAssets()
    }

    private fun loadMockData() {
        _assets.value = listOf(
            Asset(name = "Sony 50mm f/1.8 Lens", category = "Lenses", status = "On Shelf"),
            Asset(name = "Raspberry Pi 4", category = "Single Board Computer", status = "In Use"),
            Asset(name = "Anker USB-C Cable", category = "Cables", status = "In Storage"),
            Asset(name = "Adobe CC License", category = "Software", status = "Active")
        )
    }
}
