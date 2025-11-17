package com.example.devicehealth.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.devicehealth.data.repository.TipRepository

class TipsViewModelFactory(private val repo: TipRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TipsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TipsViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown VM class")
    }
}
