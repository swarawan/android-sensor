package com.swarawan.sensor.ui.sensor

import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.view.Surface
import android.widget.Toast
import com.google.gson.Gson
import com.swarawan.sensor.R
import com.swarawan.sensor.base.activity.SensorActivity
import com.swarawan.sensor.base.exstorage.ExternalStorageUtils
import com.swarawan.sensor.base.permission.PermissionGroup
import com.swarawan.sensor.base.permission.PermissionUtils
import com.swarawan.sensor.data.StepText
import com.swarawan.sensor.data.TiltText
import com.swarawan.sensor.databinding.ActivityTiltSpotBinding
import com.swarawan.sensor.ui.main.MainActivity
import kotlin.math.abs
import kotlin.math.round

class TiltSpotActivity : SensorActivity() {

    companion object {
        private const val FILENAME = "tilt-spot.txt"
        private const val VALUE_DRIFT = 0.05f
    }

    private val bindView: ActivityTiltSpotBinding by lazy {
        ActivityTiltSpotBinding.inflate(layoutInflater)
    }

    private var accelerometerData = FloatArray(3)
    private var magnetometerData = FloatArray(3)
    private var dataRecord = FloatArray(3)

    override fun onCreateView() {
        setContentView(bindView.root)
        setSupportActionBar(bindView.toolbarLayout.toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.title = intent?.getStringExtra(MainActivity.INTENT_TITLE)
        }

        bindView.buttonRecord.setOnClickListener {
            verifyPermissionToFileRecording()
        }
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

        dataRecord = getDisplayOrientation()
        renderDisplay()
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
                    SensorManager.AXIS_Y,
                    SensorManager.AXIS_MINUS_X,
                    rotationMatrixAdjusted
                )
            }

            Surface.ROTATION_180 -> {
                SensorManager.remapCoordinateSystem(
                    rotationMatrix,
                    SensorManager.AXIS_MINUS_X,
                    SensorManager.AXIS_MINUS_Y,
                    rotationMatrixAdjusted
                )
            }

            Surface.ROTATION_270 -> {
                SensorManager.remapCoordinateSystem(
                    rotationMatrix,
                    SensorManager.AXIS_MINUS_Y,
                    SensorManager.AXIS_X,
                    rotationMatrixAdjusted
                )
            }
        }

        val orientationValue = FloatArray(3)
        if (isRotationOk) {
            SensorManager.getOrientation(rotationMatrixAdjusted, orientationValue)
        }
        return orientationValue
    }

    private fun renderDisplay() {
        val azimuthValue = dataRecord[0]
        var pitchValue = dataRecord[1]
        var rollValue = dataRecord[2]

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

    private fun verifyPermissionToFileRecording() {
        if (permissionUtils.verifyPermissions(PermissionGroup.EXTERNAL_STORAGE)) {
            recordToFile()
        }
    }

    private fun recordToFile() {
        val tiltText = TiltText(
            azimuth = dataRecord[0],
            pitch = dataRecord[0],
            roll = dataRecord[0],
        )
        val fileCreated = externalStorageUtils.createFile(FILENAME, Gson().toJson(tiltText))
        if (fileCreated) {
            Toast.makeText(this, "File created", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when {
            grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED ->
                verifyPermissionToFileRecording()

            else -> Toast.makeText(
                this,
                "Change permission manually in Application Settings",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}