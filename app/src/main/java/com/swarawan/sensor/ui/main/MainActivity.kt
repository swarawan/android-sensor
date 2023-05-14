package com.swarawan.sensor.ui.main

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.swarawan.sensor.R
import com.swarawan.sensor.adapter.RVAdapter
import com.swarawan.sensor.base.permission.PermissionGroup
import com.swarawan.sensor.base.permission.PermissionUtils
import com.swarawan.sensor.data.MenuItem
import com.swarawan.sensor.databinding.ActivityRvBinding
import com.swarawan.sensor.ui.sensor.SensorDiscoveryActivity
import com.swarawan.sensor.ui.sensor.SensorListenerActivity
import com.swarawan.sensor.ui.sensor.StepActivity
import com.swarawan.sensor.ui.sensor.TiltSpotActivity

class MainActivity : AppCompatActivity() {

    companion object {
        const val INTENT_TITLE = "intent-title"
    }

    private val bindView: ActivityRvBinding by lazy {
        ActivityRvBinding.inflate(layoutInflater)
    }

    private lateinit var permissionUtils: PermissionUtils
    private lateinit var selectedMenuItem: MenuItem

    private val menu = listOf(
        MenuItem("All Sensors", SensorDiscoveryActivity::class.java),
        MenuItem("Sensor Listener", SensorListenerActivity::class.java),
        MenuItem("Tilt Spot", TiltSpotActivity::class.java),
        MenuItem("Step Sensor", StepActivity::class.java, PermissionGroup.ACTIVITY_RECOGNITION),
    )

    private val linearLayoutManager =
        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindView.root)

        permissionUtils = PermissionUtils(this)

        val rvAdapter = RVAdapter(items = menu,
            itemViewHolderId = R.layout.item_home,
            onCreateItem = { view, data ->
                view.findViewById<Button>(R.id.home_button).apply {
                    text = data.name
                    setOnClickListener { onSelectedMenuItem(data) }
                }
            })

        with(bindView) {
            setSupportActionBar(toolbarLayout.toolbar);
            recyclerView.apply {
                adapter = rvAdapter
                layoutManager = linearLayoutManager
            }
        }
    }

    private fun goTo(menu: MenuItem) {
        val intent = Intent(this, menu.targetClass).apply {
            putExtra(INTENT_TITLE, menu.name)
        }
        startActivity(intent)
    }

    private fun onSelectedMenuItem(data: MenuItem) {
        selectedMenuItem = data
        when (val permissionGroup = data.permissionGroup) {
            null -> goTo(data)
            else -> if (permissionUtils.verifyPermissions(permissionGroup)) {
                goTo(data)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when {
            grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                goTo(selectedMenuItem)
            }

            else -> Toast.makeText(
                this,
                "Change permission manually in Application Settings",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}