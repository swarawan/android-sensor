package com.swarawan.sensor.ui.sensor

import android.hardware.SensorEvent
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.swarawan.sensor.R
import com.swarawan.sensor.adapter.RVAdapter
import com.swarawan.sensor.base.activity.SensorActivity
import com.swarawan.sensor.databinding.ActivityRvBinding
import com.swarawan.sensor.ui.main.MainActivity

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

        with(bindView) {
            setSupportActionBar(toolbarLayout.toolbar)
            supportActionBar?.let {
                it.setDisplayHomeAsUpEnabled(true)
                it.title = intent?.getStringExtra(MainActivity.INTENT_TITLE)
            }

            recyclerView.apply {
                adapter = rvAdapter
                layoutManager = linearLayoutManager
            }
        }
    }

    override fun onStartSensor() {
    }

    override fun onSensorChangeEvent(event: SensorEvent?) {
    }
}