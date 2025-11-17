package com.example.devicehealth.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData
import com.example.devicehealth.data.repository.TipRepository
import kotlinx.coroutines.launch

class TipsViewModel(private val repo: TipRepository) : ViewModel() {

    val tipsLive = repo.tips.asLiveData()

    init {
        viewModelScope.launch {
            // sync once on init; Room provides offline fallback
            repo.syncTips()
        }
    }

    fun forceRefresh() {
        viewModelScope.launch {
            repo.refreshMultiple(3)
        }
    }
}
