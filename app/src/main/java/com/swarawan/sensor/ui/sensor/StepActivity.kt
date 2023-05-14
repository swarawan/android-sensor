package com.swarawan.sensor.ui.sensor

import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.swarawan.sensor.R
import com.swarawan.sensor.base.activity.SensorActivity
import com.swarawan.sensor.base.permission.PermissionGroup
import com.swarawan.sensor.data.StepText
import com.swarawan.sensor.databinding.ActivityStepDetectorBinding
import com.swarawan.sensor.ui.main.MainActivity

class StepActivity : SensorActivity() {

    companion object {
        private const val FILENAME = "step-detector.txt"
    }

    private val bindView: ActivityStepDetectorBinding by lazy {
        ActivityStepDetectorBinding.inflate(layoutInflater)
    }

    private var stepRecorded = 0
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
        sensorStepCounter?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            bindView.textNoSensor.isVisible = false

        }
    }

    override fun onSensorChangeEvent(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_STEP_COUNTER -> counterRenderDisplay(event.values)
        }
    }

    private fun counterRenderDisplay(values: FloatArray) {
        if (stepRecorded < 1) {
            stepRecorded = values.first().toInt()
        }
        val stepTaken = values.first().toInt() - stepRecorded
        bindView.textStepCounter.text = getString(R.string.step_value, stepTaken.toString())
    }

    private fun verifyPermissionToFileRecording() {
        if (permissionUtils.verifyPermissions(PermissionGroup.EXTERNAL_STORAGE)) {
            recordToFile()
        }
    }

    private fun recordToFile() {
        val stepText = StepText(
            steps = stepRecorded
        )
        val fileCreated = externalStorageUtils.createFile(FILENAME, Gson().toJson(stepText))
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