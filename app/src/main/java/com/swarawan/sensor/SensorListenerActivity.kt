package com.swarawan.sensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.swarawan.sensor.base.SensorActivity
import com.swarawan.sensor.databinding.ActivitySensorListenerBinding

class SensorListenerActivity : SensorActivity() {

    private val bindView: ActivitySensorListenerBinding by lazy {
        ActivitySensorListenerBinding.inflate(layoutInflater)
    }

    override fun onCreateView() {
        setContentView(bindView.root)

        sensorLight?.let {
            bindView.sensorLight.text = getString(R.string.error_no_sensor)
        }
        sensorProximity?.let {
            bindView.sensorProximity.text = getString(R.string.error_no_sensor)
        }
    }

    override fun onStartSensor() {
        sensorLight?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        sensorProximity?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChangeEvent(event: SensorEvent?) {
        val sensorType = event?.sensor?.type
        val currentValue = event?.values?.get(0)

        when (sensorType) {
            Sensor.TYPE_LIGHT -> {
                bindView.sensorLight.text = getString(R.string.label_light, currentValue)
            }

            Sensor.TYPE_PROXIMITY -> {
                bindView.sensorProximity.text =
                    getString(R.string.label_proximity, currentValue)
            }
        }
    }

}