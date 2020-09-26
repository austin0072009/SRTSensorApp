package com.example.sensortest

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import threeDvector.Vec3D
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    val df = DecimalFormat("####0.00")
    val AccObserver = Observer<Vec3D> {
        tv_step.text = "X: " + df.format(it.x)
        tv_step2.text = "Y: " + df.format(it.y)
        tv_step3.text = "Z: " + df.format(it.z)
    }
    val GRVObserver = Observer<Vec3D> {
        tv_Gstep.text = "X: " + df.format(it.x)
        tv_Gstep2.text = "Y: " + df.format(it.y)
        tv_Gstep3.text = "Z: " + df.format(it.z)
    }
    var processState = true


    private lateinit var mService: SensorRecord
    private var mBound: Boolean = false
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as SensorRecord.LocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //BindViews()
    }

    override fun onStart() {
        super.onStart()
        // Bind to LocalService
        Intent(this, SensorRecord::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
        mService.currentAcc.observe(this,AccObserver)
        mService.currentGRV.observe(this,GRVObserver)
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mBound = false
    }

    private fun BindViews() {
        btn_start.setOnClickListener {
            if (processState) {
                Intent(this, SensorRecord::class.java).also { intent ->
                    bindService(intent, connection, Context.BIND_AUTO_CREATE)
                }
                //startService(startIntent)
                mService.currentAcc.observe(this,AccObserver)
                mService.currentGRV.observe(this,GRVObserver)
                btn_start.text = "停止"
            } else {
                //val stopIntent = Intent(this, SensorRecord::class.java)
                //stopService(stopIntent)
                unbindService(connection)
                btn_start.text = "开始记录数据"
                mBound = false
            }
            processState = !processState
        }


    }

    override fun onDestroy() {
        super.onDestroy()
    }
}