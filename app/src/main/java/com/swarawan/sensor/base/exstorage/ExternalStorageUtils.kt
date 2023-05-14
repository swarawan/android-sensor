package com.swarawan.sensor.base.exstorage

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ExternalStorageUtils(private val context: Context) {

    fun createFile(fileName: String, content: String): Boolean {
        val file = File(getAppExternalFilesDir(context), fileName)

        if (!file.exists()) {
            file.createNewFile()
        }

        return try {
            val outputStream = FileOutputStream(file)
            outputStream.write(content.toByteArray())
            outputStream.close()
            true
        } catch (ex: IOException) {
            ex.printStackTrace()
            false
        }
    }

    private fun getAppExternalFilesDir(context: Context): File? {
        val file = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        if (file != null && !file.exists()) {
            file.mkdirs()
        }
        return file
    }
}