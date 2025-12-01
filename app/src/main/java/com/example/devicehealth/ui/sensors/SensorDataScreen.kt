package com.example.devicehealth.ui.sensors

import android.hardware.Sensor
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorDataScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: SensorDataViewModel = viewModel(
        factory = SensorDataViewModelFactory(context)
    )
    val state by viewModel.uiState.collectAsState()

    DisposableEffect(Unit) {
        viewModel.startListening()
        onDispose {
            viewModel.stopListening()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sensor Data") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Toggles
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Active Sensors", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        SensorToggleRow("Accelerometer", Sensor.TYPE_ACCELEROMETER, state, viewModel)
                        SensorToggleRow("Gyroscope", Sensor.TYPE_GYROSCOPE, state, viewModel)
                        SensorToggleRow("Light", Sensor.TYPE_LIGHT, state, viewModel)
                        SensorToggleRow("Proximity", Sensor.TYPE_PROXIMITY, state, viewModel)
                    }
                }
            }

            // Accelerometer Graph
            if (state.enabledSensors.contains(Sensor.TYPE_ACCELEROMETER) && state.isAccelerometerAvailable) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Accelerometer Magnitude (Last 5s)", style = MaterialTheme.typography.titleSmall)
                            Spacer(modifier = Modifier.height(8.dp))
                            AccelerometerGraph(history = state.accelerometerHistory)
                        }
                    }
                }
            }

            // Sensor Cards
            if (state.enabledSensors.contains(Sensor.TYPE_ACCELEROMETER)) {
                item {
                    SensorCard(
                        title = "Accelerometer",
                        isAvailable = state.isAccelerometerAvailable,
                        data = state.accelerometer?.let { "X: ${it.first.format(2)}\nY: ${it.second.format(2)}\nZ: ${it.third.format(2)}" }
                    )
                }
            }
            if (state.enabledSensors.contains(Sensor.TYPE_GYROSCOPE)) {
                item {
                    SensorCard(
                        title = "Gyroscope",
                        isAvailable = state.isGyroscopeAvailable,
                        data = state.gyroscope?.let { "X: ${it.first.format(2)}\nY: ${it.second.format(2)}\nZ: ${it.third.format(2)}" }
                    )
                }
            }
            if (state.enabledSensors.contains(Sensor.TYPE_LIGHT)) {
                item {
                    SensorCard(
                        title = "Light Sensor",
                        isAvailable = state.isLightAvailable,
                        data = state.light?.let { "$it lux" }
                    )
                }
            }
            if (state.enabledSensors.contains(Sensor.TYPE_PROXIMITY)) {
                item {
                    SensorCard(
                        title = "Proximity Sensor",
                        isAvailable = state.isProximityAvailable,
                        data = state.proximity?.let { "$it cm" }
                    )
                }
            }
        }
    }
}

@Composable
fun SensorToggleRow(
    name: String,
    type: Int,
    state: SensorUiState,
    viewModel: SensorDataViewModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(name, style = MaterialTheme.typography.bodyMedium)
        Switch(
            checked = state.enabledSensors.contains(type),
            onCheckedChange = { viewModel.toggleSensor(type, it) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}

@Composable
fun AccelerometerGraph(history: List<Float>) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(8.dp)
    ) {
        if (history.isEmpty()) return@Canvas

        val maxVal = history.maxOrNull() ?: 1f
        val minVal = history.minOrNull() ?: 0f
        val range = (maxVal - minVal).coerceAtLeast(1f)
        
        val widthPerPoint = size.width / (history.size - 1).coerceAtLeast(1)
        
        val path = Path()
        history.forEachIndexed { index, value ->
            val x = index * widthPerPoint
            val y = size.height - ((value - minVal) / range * size.height)
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = Color(0xFF00FF66), // Matrix Green
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

@Composable
fun SensorCard(
    title: String,
    isAvailable: Boolean,
    data: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            if (isAvailable) {
                Text(
                    text = data ?: "Waiting for data...",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Text(
                    text = "Not Supported",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

private fun Float.format(digits: Int) = "%.${digits}f".format(this)
