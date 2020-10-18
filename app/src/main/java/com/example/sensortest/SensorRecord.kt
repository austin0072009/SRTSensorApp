package com.example.sensortest

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import threeDvector.Rotate
import threeDvector.Slerp
import threeDvector.Vec3D
import threeDvector.Vec3D_t
import java.util.*
import kotlin.concurrent.timerTask


class SensorRecord : Service(), SensorEventListener {
    private val binder = LocalBinder()
    private lateinit var sensorManager: SensorManager
    private lateinit var powerManager: PowerManager
    private lateinit var m_wkik: PowerManager.WakeLock
    private val sensorData_Speed = ArrayList<Vec3D_t>()
    private var Acc0 = Vec3D()

    //private val sensorData_Acc = ArrayList<Vec3D>()
    //private val sensorData_GRV = ArrayList<Vec3D>()

    val currentAcc: MutableLiveData<Vec3D> by lazy {
        MutableLiveData<Vec3D>()
    }
    val currentGRV: MutableLiveData<Vec3D> by lazy {
        MutableLiveData<Vec3D>()
    }
    val currentSpeed: MutableLiveData<Vec3D> by lazy {
        MutableLiveData<Vec3D>()
    }

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): SensorRecord = this@SensorRecord
    }

    //速度计算
    private val SpeedCaculator = object {
        private var lastT_Acc: Long = 0
        private var lastT_GRV: Long = 0
        private var lastT_AccX: Long = 0
        private var T_AccX: Long = 0
        private lateinit var lastAcc: Vec3D
        private lateinit var lastAccX: Vec3D
        private lateinit var lastGRV: Vec3D
        private lateinit var AccX: Vec3D //已经转换坐标系的加速度
        private val Speed = Vec3D() //目前初始为0

        fun GRV_Update(time: Long, GRV: Vec3D) {
            if (this::lastGRV.isInitialized && this::lastAcc.isInitialized && lastT_GRV <= lastT_Acc) {
                AccX = lastAcc.Rotate(Slerp(lastGRV, GRV, (lastT_Acc - lastT_GRV).toDouble() / (time - lastT_GRV).toDouble()));
                AccX_Update(lastT_Acc, AccX)
            }
            lastT_GRV = time
            lastGRV = GRV
        }

        fun Acc_Update(time: Long, Acc: Vec3D) {
            lastT_Acc = time
            lastAcc = Acc
        }

        private fun AccX_Update(time: Long, AccX: Vec3D) {
            if (this::lastAccX.isInitialized) {
                Speed += (lastAccX + AccX) * ((time - lastT_AccX).toDouble() / 2e9)
            }
            lastT_AccX = time
            lastAccX = AccX
        }

        fun sample()= sensorData_Speed.add(Vec3D_t(Speed.copy(), lastT_AccX))
    }

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == 0x2739) {
                SpeedCaculator.sample()
            }
            super.handleMessage(msg)
        }
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
        //读取校零值
        val tmp = applicationContext.FileLoad(filename = "Avg.JSON")
        if (tmp != null) Acc0 = deserialize<Vec3D>(tmp)
        //定时采样
        Timer().schedule(timerTask { mHandler.sendEmptyMessage(0x2739) }, 3_000, 1_000)
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
                SpeedCaculator.Acc_Update(event.timestamp, tmpVec - Acc0)
            }
            Sensor.TYPE_GAME_ROTATION_VECTOR -> {
                val tmpVec = Vec3D(event.values)
                currentGRV.value = tmpVec
                SpeedCaculator.GRV_Update(event.timestamp, tmpVec)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        m_wkik.release()
        applicationContext.FileSave(serialize(sensorData_Speed), filename = "SpeedRecord.JSON")
    }
}