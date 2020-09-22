package com.example.sensortest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

import threeDvector.Vec3D;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    private SensorManager sManager;
    private Sensor mSensorAccelerometer;
    private Sensor mGyroscope;
    private Context mContext;

    private TextView tv_step;
    private TextView tv_step2;
    private TextView tv_step3;

    private TextView tv_Gstep;
    private TextView tv_Gstep2;
    private TextView tv_Gstep3;

    //用来存数据
    //private Context mContext;


    private Button btn_start;
    private int step = 0;   //步数
    private ArrayList<Vec3D> sensorData_Acc = new ArrayList();
    private ArrayList<Vec3D> sensorData_Gry = new ArrayList();


    private double oriValue = 0;  //原始值
    private double lstValue = 0;  //上次的值
    private double curValue = 0;  //当前值
    private boolean motiveState = true;   //是否处于运动状态
    private boolean processState = false;   //标记当前是否已经在计步


    //动态申请sd卡权限
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=getApplicationContext();

        //Register for the sensor
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mSensorAccelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = sManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        sManager.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_UI);
        sManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_UI);
        //Data sending construct
        bindViews();
    }

    private void bindViews() {

        tv_step = (TextView) findViewById(R.id.tv_step);
        tv_step2 = (TextView) findViewById(R.id.tv_step2);
        tv_step3 = (TextView) findViewById(R.id.tv_step3);

        tv_Gstep = (TextView) findViewById(R.id.tv_Gstep1);
        tv_Gstep2 = (TextView) findViewById(R.id.tv_Gstep2);
        tv_Gstep3 = (TextView) findViewById(R.id.tv_Gstep3);

        btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);
    }


    @Override

    public void onSensorChanged(SensorEvent event) {
        double range = 1;   //设定一个精度范围
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //Data Receive from sensor
            float[] value = event.values
            Vec3D tmpVec = new Vec3D(value[0], value[1], value[2]);
            curValue = tmpVec.getMagnitude();   //计算当前的模
            DecimalFormat df = new DecimalFormat("######0.00");  //print two decimal number
            if (processState == true) {
                tv_step.setText("X: " + df.format(tmpVec.getX()));
                tv_step2.setText("Y: " + df.format(tmpVec.getY()));
                tv_step3.setText("Z: " + df.format(tmpVec.getZ()));
                sensorData_Acc.add(tmpVec);
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
        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float[] value = event.values
            Vec3D tmpVec = new Vec3D(value[0], value[1], value[2]);
            curValue = tmpVec.getMagnitude();   //计算当前的模
            DecimalFormat df = new DecimalFormat("######0.00");  //print two decimal number
            if (processState == true) {
                tv_step.setText("X: " + df.format(tmpVec.getX()));
                tv_step2.setText("Y: " + df.format(tmpVec.getY()));
                tv_step3.setText("Z: " + df.format(tmpVec.getZ()));
                sensorData_Acc.add(tmpVec);
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        step = 0;

        tv_step.setText("X: " + 0);
        tv_step2.setText("Y: " + 0);
        tv_step3.setText("Z: " + 0);

        tv_Gstep.setText("X: " + 0);
        tv_Gstep2.setText("Y: " + 0);
        tv_Gstep3.setText("Z: " + 0);


        if (processState == true) {

            btn_start.setText("开始记录数据");
            processState = false;

            //写入数据
            try {
                verifyStoragePermissions(this);
                FileHelperKt.VecDatasave(mContext,sensorData_Acc,"Acceleration");
                FileHelperKt.VecDatasave(mContext,sensorData_Gry,"AngularSpeed");
                Toast.makeText(mContext, "数据写入成功", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mContext, "数据写入失败", Toast.LENGTH_SHORT).show();
            }


        } else {
            btn_start.setText("停止");
            processState = true;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sManager.unregisterListener(this);
    }
}