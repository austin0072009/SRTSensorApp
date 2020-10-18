package com.example.sensortest

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

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
        Btn_2.setOnClickListener {
            val intent = Intent();
            intent.setClass( this , MotionFunction::class.java)
            startActivity(intent)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
    }
}


