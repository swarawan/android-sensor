package com.swarawan.sensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.view.Surface
import com.swarawan.sensor.base.SensorActivity
import com.swarawan.sensor.databinding.ActivityTiltSpotBinding
import kotlin.math.abs

class TiltSpotActivity : SensorActivity() {

    private val bindView: ActivityTiltSpotBinding by lazy {
        ActivityTiltSpotBinding.inflate(layoutInflater)
    }

    private var accelerometerData = FloatArray(3)
    private var magnetometerData = FloatArray(3)

    companion object {
        private const val VALUE_DRIFT = 0.05f
    }

    override fun onCreateView() {
        setContentView(bindView.root)
    }

    override fun onStartSensor() {
        sensorAccelerometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        sensorMagnetometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChangeEvent(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> accelerometerData = event.values.clone()
            Sensor.TYPE_MAGNETIC_FIELD -> magnetometerData = event.values.clone()
        }

        val orientationValue = getDisplayOrientation()
        renderDisplay(orientationValue)
    }

    private fun getDisplayOrientation(): FloatArray {
        val rotationMatrix = FloatArray(9)
        val isRotationOk = SensorManager.getRotationMatrix(
            rotationMatrix, null, accelerometerData, magnetometerData
        )

        var rotationMatrixAdjusted = FloatArray(9)
        when (activityDisplay?.rotation) {
            Surface.ROTATION_0 -> {
                rotationMatrixAdjusted = rotationMatrix.clone()
            }

            Surface.ROTATION_90 -> {
                SensorManager.remapCoordinateSystem(
                    rotationMatrix,
                    SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, rotationMatrixAdjusted
                )
            }

            Surface.ROTATION_180 -> {
                SensorManager.remapCoordinateSystem(
                    rotationMatrix,
                    SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y, rotationMatrixAdjusted
                )
            }

            Surface.ROTATION_270 -> {
                SensorManager.remapCoordinateSystem(
                    rotationMatrix,
                    SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X, rotationMatrixAdjusted
                )
            }
        }

        val orientationValue = FloatArray(3)
        if (isRotationOk) {
            SensorManager.getOrientation(rotationMatrixAdjusted, orientationValue)
        }
        return orientationValue
    }

    private fun renderDisplay(orientationValue: FloatArray) {
        val azimuthValue = orientationValue[0]
        var pitchValue = orientationValue[1]
        var rollValue = orientationValue[2]

        if (abs(pitchValue) < VALUE_DRIFT) {
            pitchValue = 0f
        }
        if (abs(rollValue) < VALUE_DRIFT) {
            rollValue = 0f
        }

        with(bindView) {
            textAzimuthValue.text = resources.getString(R.string.value_format, azimuthValue)
            textPitchValue.text = resources.getString(R.string.value_format, pitchValue)
            textRollValue.text = resources.getString(R.string.value_format, rollValue)

            spotTop.alpha = 0f
            spotBottom.alpha = 0f
            spotRight.alpha = 0f
            spotLeft.alpha = 0f

            when {
                pitchValue > 0f -> spotBottom.alpha = pitchValue
                else -> spotTop.alpha = abs(pitchValue)
            }

            when {
                rollValue > 0f -> spotLeft.alpha = pitchValue
                else -> spotRight.alpha = abs(pitchValue)
            }
        }
    }

}