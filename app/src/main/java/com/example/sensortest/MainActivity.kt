package com.example.sensortest

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var firstFragment: Fragment

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
    fun deleteFragment() {
        supportFragmentManager.beginTransaction().remove(firstFragment).commit()
        Btn_zc.visibility = View.VISIBLE
    }

    fun Btn_listener() {
        Btn_1.setOnClickListener {
            val intent = Intent();
            intent.setClass(this, Raw_data_function::class.java)
            startActivity(intent)
        }
        Btn_2.setOnClickListener {
            val intent = Intent();
            intent.setClass(this, MotionFunction::class.java)
            startActivity(intent)
        }
        Btn_zc.setOnClickListener {
            firstFragment = ZeroCalibration()
            Btn_zc.visibility = View.INVISIBLE
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}


