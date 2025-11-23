package com.example.devicehealth.ui.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState

import com.example.devicehealth.ServiceLocator
import com.example.devicehealth.ui.home.TipsViewModel
import com.example.devicehealth.ui.home.TipsViewModelFactory


@Composable
fun TipsCard() {
    // ensure ServiceLocator.init(context) was called somewhere (AppRoot/MainActivity)
    val repo = ServiceLocator.tipRepository
    val factory = TipsViewModelFactory(repo)
    val vm: TipsViewModel = viewModel(factory = factory)

    val tips by vm.tipsLive.observeAsState(emptyList())

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Optimization Tips", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = { vm.forceRefresh() }) {
                    Text("Refresh")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (tips.isEmpty()) {
                Text("No tips yet. Pulling from the network...", style = MaterialTheme.typography.bodyMedium)
            } else {
                tips.take(1).forEach { tip ->
                    Text("• ${tip.message}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}
