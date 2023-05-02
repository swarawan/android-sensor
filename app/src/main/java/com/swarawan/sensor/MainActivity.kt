package com.swarawan.sensor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.swarawan.sensor.adapter.RVAdapter
import com.swarawan.sensor.data.MenuItem
import com.swarawan.sensor.databinding.ActivityRvBinding

class MainActivity : AppCompatActivity() {

    private val bindView: ActivityRvBinding by lazy {
        ActivityRvBinding.inflate(layoutInflater)
    }

    private val menu = listOf(
        MenuItem("All Sensors", SensorDiscovery::class.java),
        MenuItem("Sensor Listener", SensorListener::class.java),
    )

    private fun goTo(targetClass: Class<*>) {
        val intent = Intent(this, targetClass)
        startActivity(intent)
    }

    private val linearLayoutManager =
        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindView.root)

        val rvAdapter = RVAdapter(items = menu,
            itemViewHolderId = R.layout.item_home,
            onCreateItem = { view, data ->
                view.findViewById<Button>(R.id.home_button).apply {
                    text = data.name
                    setOnClickListener {
                        goTo(data.targetClass)
                    }
                }
            })

        bindView.recyclerView.apply {
            adapter = rvAdapter
            layoutManager = linearLayoutManager
        }
    }
}