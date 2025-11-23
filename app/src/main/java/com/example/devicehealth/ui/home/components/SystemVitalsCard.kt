package com.example.devicehealth.ui.home.components

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.File
import kotlin.math.roundToInt

@Composable
fun SystemVitalsCard() {
    val context = LocalContext.current
    var batteryLevel by remember { mutableStateOf(0) }
    var storageUsedPercent by remember { mutableStateOf(0f) }
    var storageText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        // Battery
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }
        val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        batteryLevel = if (level != -1 && scale != -1) {
            (level * 100 / scale.toFloat()).roundToInt()
        } else {
            0
        }

        // Storage
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val availableBlocks = stat.availableBlocksLong

        val totalSize = totalBlocks * blockSize
        val availableSize = availableBlocks * blockSize
        val usedSize = totalSize - availableSize

        storageUsedPercent = if (totalSize > 0) usedSize.toFloat() / totalSize else 0f
        
        val freeGb = availableSize / (1024 * 1024 * 1024)
        val totalGb = totalSize / (1024 * 1024 * 1024)
        storageText = "$freeGb GB Free / $totalGb GB Total"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), // Match TipsCard padding
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("System Vitals", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            // Battery
            Text("Battery: $batteryLevel%", style = MaterialTheme.typography.bodyMedium)
            LinearProgressIndicator(
                progress = { batteryLevel / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Storage
            Text("Storage: $storageText", style = MaterialTheme.typography.bodyMedium)
            LinearProgressIndicator(
                progress = { storageUsedPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}
