package com.example.sensortest

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import threeDvector.Vec3D
import java.text.DecimalFormat


class MainActivity : AppCompatActivity() {

    private lateinit var firstFragment:Fragment

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


    fun Btn_listener() {
        Btn_1.setOnClickListener {
            val intent = Intent();
            intent.setClass(this, Raw_data_function::class.java)
            startActivity(intent)
        }
        Btn_zc.setOnClickListener{
            firstFragment = ZeroCalibration()
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit()
        }
    }

    fun deleteFragment() = supportFragmentManager.beginTransaction().remove(firstFragment).commit()

    override fun onDestroy() {
        super.onDestroy()
    }
}


