package com.example.sensortest

import android.R
import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import threeDvector.Vec3D
import java.util.*


class SensorRecord : Service(), SensorEventListener {
    private val binder= LocalBinder()
    private lateinit var sensorManager: SensorManager
    private lateinit var powerManager: PowerManager
    private lateinit var m_wkik: PowerManager.WakeLock

    private val sensorData_Acc = ArrayList<Vec3D>()
    private val sensorData_GRV = ArrayList<Vec3D>()
    private var i = 0
    val currentAcc: MutableLiveData<Vec3D> by lazy {
        MutableLiveData<Vec3D>()
    }
    val currentGRV: MutableLiveData<Vec3D> by lazy {
        MutableLiveData<Vec3D>()
    }

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): SensorRecord = this@SensorRecord
    }

    override fun onCreate() {
        super.onCreate()
        sensorManager = applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensorAcc = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        val sensorGRV = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)
        sensorManager.registerListener(this, sensorAcc, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, sensorGRV, SensorManager.SENSOR_DELAY_NORMAL)

        powerManager = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        m_wkik = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, SensorRecord::class.qualifiedName)
        m_wkik.acquire()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val range = 1.0 //设定一个精度范围
        val sensor = event.sensor
        when (sensor.type) {
            Sensor.TYPE_LINEAR_ACCELERATION -> {
                //Data Receive from sensor
                val tmpVec = Vec3D(event.values)
                currentAcc.value = tmpVec
                sensorData_Acc.add(tmpVec)
                if (sensorData_Acc.size == 300) {
                    FileSave(fileContent = serialize(sensorData_Acc), filename = "SensorRecord${i}.JSON")
                    sensorData_Acc.clear()
                    i += 1
                }
            }
            Sensor.TYPE_GAME_ROTATION_VECTOR -> {
                val tmpVec = Vec3D(event.values)
                currentAcc.value = tmpVec
                sensorData_GRV.add(tmpVec)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        m_wkik.release()
    }
}