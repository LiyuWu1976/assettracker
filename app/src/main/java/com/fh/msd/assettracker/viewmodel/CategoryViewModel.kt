package com.fh.msd.assettracker.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoryViewModel : ViewModel() {
    private val _categories = MutableStateFlow(listOf("Phone", "Camera", "Laptop", "Lens", "Board", "Cable", "Software"))

    val categories: StateFlow<List<String>> = _categories.asStateFlow()
}
