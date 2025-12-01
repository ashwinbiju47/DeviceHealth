package com.example.devicehealth.ui.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SensorUiState(
    val accelerometer: Triple<Float, Float, Float>? = null,
    val gyroscope: Triple<Float, Float, Float>? = null,
    val light: Float? = null,
    val proximity: Float? = null,
    val isAccelerometerAvailable: Boolean = false,
    val isGyroscopeAvailable: Boolean = false,
    val isLightAvailable: Boolean = false,
    val isProximityAvailable: Boolean = false,
    val enabledSensors: Set<Int> = setOf(
        Sensor.TYPE_ACCELEROMETER,
        Sensor.TYPE_GYROSCOPE,
        Sensor.TYPE_LIGHT,
        Sensor.TYPE_PROXIMITY
    ),
    val accelerometerHistory: List<Float> = emptyList() // For mini graph (magnitude)
)

class SensorDataViewModel(private val context: Context) : ViewModel(), SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val _uiState = MutableStateFlow(SensorUiState())
    val uiState: StateFlow<SensorUiState> = _uiState.asStateFlow()

    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val light: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    private val proximity: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

    init {
        _uiState.update {
            it.copy(
                isAccelerometerAvailable = accelerometer != null,
                isGyroscopeAvailable = gyroscope != null,
                isLightAvailable = light != null,
                isProximityAvailable = proximity != null
            )
        }
    }

    fun startListening() {
        registerListeners()
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    fun toggleSensor(sensorType: Int, isEnabled: Boolean) {
        _uiState.update { currentState ->
            val newEnabledSensors = if (isEnabled) {
                currentState.enabledSensors + sensorType
            } else {
                currentState.enabledSensors - sensorType
            }
            currentState.copy(enabledSensors = newEnabledSensors)
        }
        // Re-register to reflect changes immediately if we are already listening (simplified approach: unregister all, then register enabled)
        // In a more complex app, we might check if we are currently "started" before doing this.
        // Assuming this is called while on the screen:
        stopListening()
        registerListeners()
    }

    private fun registerListeners() {
        val enabled = _uiState.value.enabledSensors
        if (enabled.contains(Sensor.TYPE_ACCELEROMETER)) accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        if (enabled.contains(Sensor.TYPE_GYROSCOPE)) gyroscope?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        if (enabled.contains(Sensor.TYPE_LIGHT)) light?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        if (enabled.contains(Sensor.TYPE_PROXIMITY)) proximity?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        if (!_uiState.value.enabledSensors.contains(event.sensor.type)) return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val magnitude = kotlin.math.sqrt((x*x + y*y + z*z).toDouble()).toFloat()
                
                _uiState.update { 
                    val newHistory = (it.accelerometerHistory + magnitude).takeLast(50) // Keep last 50 points (~5 seconds at 10Hz)
                    it.copy(
                        accelerometer = Triple(x, y, z),
                        accelerometerHistory = newHistory
                    ) 
                }
            }
            Sensor.TYPE_GYROSCOPE -> {
                _uiState.update { it.copy(gyroscope = Triple(event.values[0], event.values[1], event.values[2])) }
            }
            Sensor.TYPE_LIGHT -> {
                _uiState.update { it.copy(light = event.values[0]) }
            }
            Sensor.TYPE_PROXIMITY -> {
                _uiState.update { it.copy(proximity = event.values[0]) }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }
}

class SensorDataViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SensorDataViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SensorDataViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
