package com.example.sensortest

import android.app.ActivityManager
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
    var processState = false


    private lateinit var mService: SensorRecord
    private var mBound: Boolean = false
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as SensorRecord.LocalBinder
            mService = binder.getService()
            mService.currentAcc.observe(this@MainActivity, AccObserver)
            mService.currentGRV.observe(this@MainActivity, GRVObserver)
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (ServiceCheckUtil.isRunning(applicationContext, SensorRecord::class.qualifiedName)) {
            processState = true
            val intent = Intent(this, SensorRecord::class.java)
            intent.setAction("com.example.server.SensorRecord")
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
        BindViews()
    }

    /*override fun onStart() {
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
    }*/

    private fun BindViews() {
        btn_start.text = if (processState) "停止" else "开始记录数据"
        btn_start.setOnClickListener {
            if (processState) {
                val intent = Intent(this, SensorRecord::class.java)
                intent.setAction("com.example.server.SensorRecord")
                unbindService(connection)
                stopService(intent)
                btn_start.text = "开始记录数据"
                mBound = false
            } else {
                val intent = Intent(this, SensorRecord::class.java)
                intent.setAction("com.example.server.SensorRecord")
                startService(intent)
                bindService(intent, connection, Context.BIND_AUTO_CREATE)

                btn_start.text = "停止"
            }
            processState = !processState
        }


    }

    override fun onDestroy() {
        super.onDestroy()
    }
}