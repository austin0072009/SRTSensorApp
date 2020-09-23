package com.example.sensortest

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

private val REQUEST_EXTERNAL_STORAGE = 1
private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)

fun verifyStoragePermissions(activity: Activity?) {
    // Check if we have write permission
    if (ActivityCompat.checkSelfPermission(activity!!,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        // We don't have permission so prompt the user
        ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE)
    }
}