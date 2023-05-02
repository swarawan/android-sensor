package com.swarawan.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.swarawan.sensor.databinding.ActivitySensorListenerBinding

class SensorListener : AppCompatActivity(), SensorEventListener {

    private val bindView: ActivitySensorListenerBinding by lazy {
        ActivitySensorListenerBinding.inflate(layoutInflater)
    }

    private var sensorManager: SensorManager? = null
    private var sensorLight: Sensor? = null
    private var sensorProximity: Sensor? = null

    private val linearLayoutManager =
        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindView.root)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager?.let {
            sensorLight = it.getDefaultSensor(Sensor.TYPE_LIGHT)
            sensorProximity = it.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        }

        sensorLight?.let {
            bindView.sensorLight.text = getString(R.string.error_no_sensor)
        }
        sensorProximity?.let {
            bindView.sensorProximity.text = getString(R.string.error_no_sensor)
        }
    }

    override fun onStart() {
        super.onStart()
        if (sensorLight != null) {
            sensorManager?.registerListener(
                this,
                sensorLight,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        if (sensorProximity != null) {
            sensorManager?.registerListener(
                this,
                sensorProximity,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onStop() {
        super.onStop()
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
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

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}