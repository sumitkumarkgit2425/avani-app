package com.example.navya.ui.screens.lightmeter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navya.data.local.entity.PlantEntity
import com.example.navya.data.repository.NavyaRepository
import com.example.navya.data.sensor.LightSensorManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LightMeterUiState(
        val currentLux: Float = 0f,
        val recommendedPlants: List<PlantEntity> = emptyList(),
        val lightCondition: String = "Waiting for sensor...",
        val isSensorAvailable: Boolean = true,
        val isScanning: Boolean = false,
        val scanProgress: Float = 0f,
        val scanError: String? = null,
        val isResultAvailable: Boolean = false
)

@HiltViewModel
class LightMeterViewModel
@Inject
constructor(
        private val lightSensorManager: LightSensorManager,
        private val repository: NavyaRepository
) : ViewModel() {

    private val _isScanning = MutableStateFlow(false)
    private val _scanProgress = MutableStateFlow(0f)
    private val _scanError = MutableStateFlow<String?>(null)
    private val _finalLux = MutableStateFlow<Float?>(null)


    private val _currentSensorLux =
            lightSensorManager.sensorValues.stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    0f
            )


    private val _scanState =
            combine(_isScanning, _scanProgress, _scanError, _finalLux) {
                    isScanning,
                    progress,
                    error,
                    finalLux ->
                ScanState(isScanning, progress, error, finalLux)
            }

    val uiState: StateFlow<LightMeterUiState> =
            combine(_currentSensorLux, repository.getPlants(), _scanState) {
                        currentLux,
                        plants,
                        scanState ->
                        
                        val displayLux = scanState.finalLux ?: currentLux
                        val condition = getLightCondition(displayLux)


                        val filteredPlants =
                                if (scanState.finalLux != null) {
                                    plants.filter { plant ->
                                        val min = plant.min_lux ?: 0
                                        val max = plant.max_lux ?: Int.MAX_VALUE
                                        displayLux >= min && displayLux <= max
                                    }
                                } else {
                                    emptyList()
                                }

                        LightMeterUiState(
                                currentLux = displayLux,
                                recommendedPlants = filteredPlants,
                                lightCondition = condition,
                                isScanning = scanState.isScanning,
                                scanProgress = scanState.progress,
                                scanError = scanState.error,
                                isResultAvailable = scanState.finalLux != null
                        )
                    }
                    .stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(5000),
                            initialValue = LightMeterUiState()
                    )


    private data class ScanState(
            val isScanning: Boolean,
            val progress: Float,
            val error: String?,
            val finalLux: Float?
    )

    fun startScan() {
        if (_isScanning.value) return

        viewModelScope.launch {
            _isScanning.value = true
            _scanError.value = null
            _finalLux.value = null
            _scanProgress.value = 0f

            val recordedLuxValues = mutableListOf<Float>()
            val scanDurationMillis = 5000L
            val intervalMillis = 100L
            val steps = (scanDurationMillis / intervalMillis).toInt()

            for (i in 1..steps) {
                delay(intervalMillis)
                _scanProgress.value = i.toFloat() / steps
                recordedLuxValues.add(_currentSensorLux.value)
            }


            if (recordedLuxValues.isNotEmpty()) {
                val min = recordedLuxValues.minOrNull() ?: 0f
                val max = recordedLuxValues.maxOrNull() ?: 0f
                val avg = recordedLuxValues.average().toFloat()


                val diff = max - min
                val isStable = diff < 500f || (diff / avg) < 0.3f

                if (isStable) {
                    _finalLux.value = avg
                } else {
                    _scanError.value = "Light source unstable. Please hold the phone steady."
                }
            } else {
                _scanError.value = "No sensor data received."
            }

            _isScanning.value = false
        }
    }

    fun resetScan() {
        _finalLux.value = null
        _scanError.value = null
        _scanProgress.value = 0f
        _isScanning.value = false
    }


    private fun getLightCondition(lux: Float): String {
        return when {
            lux < 50 -> "Very Low Light (Pitch Dark)"
            lux < 200 -> "Low Light (Shadows)"
            lux < 500 -> "Medium Light (Office)"
            lux < 1000 -> "Bright Indirect Light (Near Window)"
            lux < 2000 -> "Bright Direct Light (Sunny Window)"
            else -> "Full Sun (Outdoors)"
        }
    }
}
