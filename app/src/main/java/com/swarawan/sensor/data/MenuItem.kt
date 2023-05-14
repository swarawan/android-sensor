package com.swarawan.sensor.data

import com.swarawan.sensor.base.permission.PermissionGroup

data class MenuItem(
    val name: String,
    val targetClass: Class<*>,
    val permissionGroup: PermissionGroup? = null
)