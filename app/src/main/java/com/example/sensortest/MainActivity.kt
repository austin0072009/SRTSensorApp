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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Btn_listener()
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


    fun Btn_listener()
    {
        Btn_1.setOnClickListener {
            val intent = Intent();
            intent.setClass( this , Raw_data_function::class.java)
            startActivity(intent)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
    }
}


