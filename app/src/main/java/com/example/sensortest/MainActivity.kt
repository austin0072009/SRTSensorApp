package com.example.sensortest

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var processState = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BindViews()
    }

    private fun BindViews() {
        btn_start.setOnClickListener {
            if (processState) {
                val startIntent = Intent(this@MainActivity, SensorRecord::class.java)
                startService(startIntent)
                btn_start.text = "停止"
            } else {
                val stopIntent = Intent(this@MainActivity, SensorRecord::class.java)
                stopService(stopIntent)
                btn_start.text = "开始记录数据"
            }
            processState = !processState
        }


    }

    override fun onDestroy() {
        super.onDestroy()
    }
}