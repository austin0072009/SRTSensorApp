package com.example.sensortest

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import threeDvector.Vec3D
import java.text.DecimalFormat
import java.util.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sManager: SensorManager
    private lateinit var mSensorAccelerometer: Sensor
    private lateinit var mGyroscope: Sensor

    //private var mContext: Context? = null

    //用来存数据
    //private Context mContext;
    private var step = 0 //步数
    private val sensorData_Acc = ArrayList<Vec3D>()
    private val sensorData_Gry = ArrayList<Vec3D>()
    private val oriValue = 0.0 //原始值
    private val lstValue = 0.0 //上次的值
    private var curValue = 0.0 //当前值
    private val motiveState = true //是否处于运动状态
    private var processState = false //标记当前是否已经在计步
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Register for the sensor
        sManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mSensorAccelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mGyroscope = sManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        sManager.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_UI)
        sManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_UI)
        //Data sending construct
        BindViews()
    }

    private fun BindViews() {
        btn_start.setOnClickListener{
            step = 0
            tv_step.text = "X: " + 0
            tv_step2.text = "Y: " + 0
            tv_step3.text = "Z: " + 0
            tv_Gstep.text = "X: " + 0
            tv_Gstep2.text = "Y: " + 0
            tv_Gstep3.text = "Z: " + 0
            if (processState == true) {
                btn_start.text = "开始记录数据"
                processState = false

                //写入数据
                this.FileSave(fileContent = serialize(sensorData_Acc, sensorData_Gry), filename = "SensorDataRecord.JSON")
            } else {
                btn_start.text = "停止"
                processState = true
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        val range = 1.0 //设定一个精度范围
        val sensor = event.sensor
        if (sensor.type == Sensor.TYPE_ACCELEROMETER) {
            //Data Receive from sensor
            val tmpVec = Vec3D(event.values)
            curValue = tmpVec.magnitude //计算当前的模
            val df = DecimalFormat("######0.00") //print two decimal number
            if (processState == true) {
                tv_step.text = "X: " + df.format(tmpVec.x)
                tv_step2.text = "Y: " + df.format(tmpVec.y)
                tv_step3.text = "Z: " + df.format(tmpVec.z)
                sensorData_Acc.add(tmpVec)
            }
            /*
        //向上加速的状态
        if (motiveState == true) {
            if (curValue >= lstValue) lstValue = curValue;
            else {
                //检测到一次峰值
                if (Math.abs(curValue - lstValue) > range) {
                    oriValue = curValue;
                    motiveState = false;
                }
            }
        }
        //向下加速的状态
        if (motiveState == false) {
            if (curValue <= lstValue) lstValue = curValue;
            else {
                if (Math.abs(curValue - lstValue) > range) {
                    //检测到一次峰值
                    oriValue = curValue;
                    if (processState == true) {
                        step++;  //步数 + 1
                        if (processState == true) {
                            tv_step.setText(step + "");    //读数更新
                        }
                    }
                    motiveState = true;
                }
            }
        }


        */
        } else if (sensor.type == Sensor.TYPE_GYROSCOPE) {
            val tmpVec = Vec3D(event.values)
            curValue = tmpVec.magnitude //计算当前的模
            val df = DecimalFormat("######0.00") //print two decimal number
            if (processState == true) {
                tv_Gstep.text = "X: " + df.format(tmpVec.x)
                tv_Gstep2.text = "Y: " + df.format(tmpVec.y)
                tv_Gstep3.text = "Z: " + df.format(tmpVec.z)
                sensorData_Gry.add(tmpVec)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sManager.unregisterListener(this)
    }
}