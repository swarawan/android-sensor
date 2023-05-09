package com.swarawan.sensor.base

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.Display
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

abstract class SensorActivity : AppCompatActivity(), SensorEventListener {

    lateinit var sensors: List<Sensor>

    var sensorManager: SensorManager? = null
    var sensorLight: Sensor? = null
    var sensorProximity: Sensor? = null
    var sensorAccelerometer: Sensor? = null
    var sensorMagnetometer: Sensor? = null
    var activityDisplay: Display? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager?.let {
            sensors = it.getSensorList(Sensor.TYPE_ALL)
            sensorLight = it.getDefaultSensor(Sensor.TYPE_LIGHT)
            sensorProximity = it.getDefaultSensor(Sensor.TYPE_PROXIMITY)
            sensorAccelerometer = it.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            sensorMagnetometer = it.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        }

        activityDisplay = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> display
            else -> (getSystemService(WINDOW_SERVICE) as WindowManager).defaultDisplay
        }

        onCreateView()
    }

    override fun onStart() {
        super.onStart()
        onStartSensor()
    }

    override fun onStop() {
        super.onStop()
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        onSensorChangeEvent(event)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    abstract fun onCreateView()
    abstract fun onStartSensor()
    abstract fun onSensorChangeEvent(event: SensorEvent?)

}