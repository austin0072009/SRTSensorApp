package com.example.sensortest

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import threeDvector.Vec3D

class ZeroCalibration : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {
    private lateinit var sensorManager: SensorManager
    private var count = 0
    private val sum = Vec3D()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val mView = inflater.inflate(R.layout.fragment_zc, container, false)
        launch {
            delay(10000L)
            val ans = sum * (1.0 / count)
            activity!!.applicationContext.FileSave(fileContent = serialize(ans), filename = "Avg.JSON")
            val fatherActivity=this@ZeroCalibration.activity as MainActivity //这样可以调用MainActivity里自定义的部分
            fatherActivity.deleteFragment()
        }
        launch {
            delay(2000L)
            Bindviews()
        }
        return mView
    }

    private fun Bindviews() {
        sensorManager = activity!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensorAcc = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        sensorManager.registerListener(AccRecorder, sensorAcc, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private val AccRecorder = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent) {
            val sensor = event.sensor

            when (sensor.type) {
                Sensor.TYPE_LINEAR_ACCELERATION -> {
                    //Data Receive from sensor
                    val tmpVec = Vec3D(event.values)
                    sum += tmpVec
                    count++
                }
            }
        }
    }

    @InternalCoroutinesApi
    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(AccRecorder)
        NonCancellable.cancel()
    }
}