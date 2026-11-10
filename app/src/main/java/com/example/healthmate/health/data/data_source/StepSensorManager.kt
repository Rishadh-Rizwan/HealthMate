package com.example.healthmate.health.data.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
//package com.example.healthmate.health.data.sensor
class StepSensorManager(context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    val stepCountFlow: Flow<Int> = callbackFlow {
        if (stepSensor == null) {
            close()
            return@callbackFlow
        }

        var initialSteps = -1

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
                    val totalStepsSinceReboot = event.values[0].toInt()

                    if (initialSteps == -1) {
                        initialSteps = totalStepsSinceReboot
                    }

                    val currentSteps = totalStepsSinceReboot - initialSteps
                    trySend(currentSteps)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }
        }

        sensorManager.registerListener(listener, stepSensor, SensorManager.SENSOR_DELAY_UI)

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}