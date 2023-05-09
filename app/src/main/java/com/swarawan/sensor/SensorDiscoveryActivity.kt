package com.swarawan.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.swarawan.sensor.adapter.RVAdapter
import com.swarawan.sensor.base.SensorActivity
import com.swarawan.sensor.databinding.ActivityRvBinding

class SensorDiscoveryActivity : SensorActivity() {

    private val bindView: ActivityRvBinding by lazy {
        ActivityRvBinding.inflate(layoutInflater)
    }


    private val linearLayoutManager =
        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    override fun onCreateView() {
        setContentView(bindView.root)

        val rvAdapter = RVAdapter(items = sensors,
            itemViewHolderId = R.layout.item_sensor_discovery,
            onCreateItem = { view, data ->
                view.findViewById<TextView>(R.id.sensor_item).text = data.name
            })

        bindView.recyclerView.apply {
            adapter = rvAdapter
            layoutManager = linearLayoutManager
        }
    }

    override fun onStartSensor() {
    }

    override fun onSensorChangeEvent(event: SensorEvent?) {
    }
}