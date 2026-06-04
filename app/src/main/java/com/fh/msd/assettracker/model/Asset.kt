package com.fh.msd.assettracker.model

import com.google.firebase.firestore.DocumentId

data class Asset(
    @DocumentId val id: String = "",
    val name: String = "",
    val category: String = "",
    val location: String = "",
    val price: Double = 0.0,
    val currency: String = "EUR",
    val status: String = "",
    val imageUrl: String? = null // For future use if images are added
)
