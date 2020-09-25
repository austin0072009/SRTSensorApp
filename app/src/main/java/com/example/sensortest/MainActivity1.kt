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

class MainActivity : AppCompatActivity(), View.OnClickListener, SensorEventListener {
    private var sManager: SensorManager? = null
    private var mSensorAccelerometer: Sensor? = null
    private var mGyroscope: Sensor? = null
    private var mMagnetic: Sensor? = null
    private var mContext: Context? = null
    private var tv_step: TextView? = null
    private var tv_step2: TextView? = null
    private var tv_step3: TextView? = null
    private var tv_Gstep: TextView? = null
    private var tv_Gstep2: TextView? = null
    private var tv_Gstep3: TextView? = null
    private var tv_Mstep: TextView? = null
    private var tv_Mstep2: TextView? = null
    private var tv_Mstep3: TextView? = null

    //用来存数据
    //private Context mContext;
    private var btn_start: Button? = null
    private var step = 0 //步数
    private val sensorData_Acc: ArrayList<Vec3D> = ArrayList<Vec3D>()
    private val sensorData_Gry: ArrayList<Vec3D> = ArrayList<Vec3D>()
    private val sensorData_Mag: ArrayList<Vec3D> = ArrayList<Vec3D>()
    private val oriValue = 0.0 //原始值
    private val lstValue = 0.0 //上次的值
    private var curValue = 0.0 //当前值
    private val motiveState = true //是否处于运动状态
    private var processState = false //标记当前是否已经在计步
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = applicationContext

        //Register for the sensor
        sManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mSensorAccelerometer = sManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mGyroscope = sManager!!.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        mMagnetic = sManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)


        //Data sending construct
        bindViews()
    }

    private fun bindViews() {


        //用于前端界面的数据展示
        //for the UI design
        tv_step = findViewById<View>(R.id.tv_step) as TextView
        tv_step2 = findViewById<View>(R.id.tv_step2) as TextView
        tv_step3 = findViewById<View>(R.id.tv_step3) as TextView
        tv_Gstep = findViewById<View>(R.id.tv_Gstep1) as TextView
        tv_Gstep2 = findViewById<View>(R.id.tv_Gstep2) as TextView
        tv_Gstep3 = findViewById<View>(R.id.tv_Gstep3) as TextView
        tv_Mstep = findViewById<View>(R.id.tv_Mstep1) as TextView
        tv_Mstep2 = findViewById<View>(R.id.tv_Mstep2) as TextView
        tv_Mstep3 = findViewById<View>(R.id.tv_Mstep3) as TextView
        btn_start = findViewById<View>(R.id.btn_start) as Button
        btn_start!!.setOnClickListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val range = 1.0 //设定一个精度范围
        val sensor = event.sensor
        if (sensor.type == Sensor.TYPE_ACCELEROMETER) {
            //Data Receive from sensor
            val value = event.values
            val tmpVec = Vec3D(value[0], value[1], value[2])
            curValue = tmpVec.magnitude //计算当前的模
            val df = DecimalFormat("######0.00") //print two decimal number
            if (processState == true) {
                tv_step!!.text = "X: " + df.format(tmpVec.x)
                tv_step2!!.text = "Y: " + df.format(tmpVec.y)
                tv_step3!!.text = "Z: " + df.format(tmpVec.z)
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
            val value = event.values
            val tmpVec = Vec3D(value[0], value[1], value[2])
            val df = DecimalFormat("######0.00") //print two decimal number
            if (processState == true) {
                tv_Gstep!!.text = "X: " + df.format(tmpVec.x)
                tv_Gstep2!!.text = "Y: " + df.format(tmpVec.y)
                tv_Gstep3!!.text = "Z: " + df.format(tmpVec.z)
                sensorData_Gry.add(tmpVec)
            }
        } else if (sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            val value = event.values
            val tmpVec = Vec3D(value[0], value[1], value[2])
            val df = DecimalFormat("######0.00") //print two decimal number
            if (processState == true) {
                tv_Mstep!!.text = "X: " + df.format(tmpVec.x)
                tv_Mstep2!!.text = "Y: " + df.format(tmpVec.y)
                tv_Mstep3!!.text = "Z: " + df.format(tmpVec.z)
                sensorData_Mag.add(tmpVec)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    @RequiresApi(api = Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    override fun onClick(v: View) {
        step = 0
        tv_step!!.text = "X: " + 0
        tv_step2!!.text = "Y: " + 0
        tv_step3!!.text = "Z: " + 0
        tv_Gstep!!.text = "X: " + 0
        tv_Gstep2!!.text = "Y: " + 0
        tv_Gstep3!!.text = "Z: " + 0
        tv_Mstep!!.text = "X: " + 0
        tv_Mstep2!!.text = "Y: " + 0
        tv_Mstep3!!.text = "Z: " + 0
        if (processState == true) {
            btn_start!!.text = "开始记录数据"
            processState = false

            //写入数据
            this.FileSave(serialize(sensorData_Acc, sensorData_Gry, sensorData_Mag), null, "SensorDataRecord.JSON")

            //关闭传感器，停止记录数据
            sManager!!.unregisterListener(this)
            sensorData_Acc.clear()
            sensorData_Gry.clear()
            sensorData_Mag.clear()
        } else {
            sManager!!.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_UI)
            sManager!!.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_UI)
            sManager!!.registerListener(this, mMagnetic, SensorManager.SENSOR_DELAY_UI)
            btn_start!!.text = "停止"
            processState = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sManager!!.unregisterListener(this)
    }
}