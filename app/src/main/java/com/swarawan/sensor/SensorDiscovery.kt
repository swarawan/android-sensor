package com.swarawan.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.swarawan.sensor.adapter.RVAdapter
import com.swarawan.sensor.databinding.ActivityRvBinding

class SensorDiscovery : AppCompatActivity() {

    private val bindView: ActivityRvBinding by lazy {
        ActivityRvBinding.inflate(layoutInflater)
    }


    private val linearLayoutManager =
        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindView.root)

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
        val rvAdapter = RVAdapter<Sensor>(items = sensors,
            itemViewHolderId = R.layout.item_sensor_discovery,
            onCreateItem = { view, data ->
                view.findViewById<TextView>(R.id.sensor_item).text = data.name
            })

        bindView.recyclerView.apply {
            adapter = rvAdapter
            layoutManager = linearLayoutManager
        }
    }
}