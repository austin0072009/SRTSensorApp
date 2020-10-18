package com.example.sensortest

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_motion_function.*
import org.jetbrains.anko.toast

class MotionFunction : AppCompatActivity() {

    private lateinit var locationManager: LocationManager
    var currentLon = 0.0
    var currentLat = 0.0

    var distance = 0.0

    //定义一个权限COde，用来识别Location权限
    private val LOCATION_PERMISSION = 1

    //使用匿名内部类创建了LocationListener的实例
    val locationListener = object : LocationListener {
        override fun onProviderDisabled(provider: String) {
            toast("关闭了GPS")
            motion4.text = "关闭了GPS"
        }

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onProviderEnabled(provider: String) {
            toast("打开了GPS")
            showLocation(motion4, locationManager)
            showSpeed(motion3, locationManager)
            showBearing(motion1, locationManager)
        }

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onLocationChanged(location: Location) {
            toast("变化了")
            showLocation(motion4, locationManager)
            showSpeed(motion3, locationManager)
            showBearing(motion1, locationManager)
            //临时定义两个location 来计算距离





        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_motion_function)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                //requestPermissions是异步执行的
                requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_PERMISSION)
            }
            else {
                showLocation(motion4, locationManager)
                showSpeed(motion3, locationManager)
            }
        }
        else {
            showLocation(motion4, locationManager)
            showSpeed(motion3, locationManager)
        }



    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onPause() {
        super.onPause()
        val hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        if ((locationManager != null) && ((hasLocationPermission == PackageManager.PERMISSION_GRANTED))) {
            locationManager.removeUpdates(locationListener)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        //挂上LocationListener, 在状态变化时刷新位置显示，因为requestPermissionss是异步执行的，所以要先确认是否有权限
        super.onResume()
        val hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        if ((locationManager != null) && ((hasLocationPermission == PackageManager.PERMISSION_GRANTED))) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0F, locationListener)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0F, locationListener)
            showLocation(motion4, locationManager)
        }
    }

    //申请下位置权限后，要刷新位置信息
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toast("获取了位置权限")
                showLocation(motion4, locationManager)
                showSpeed(motion3, locationManager)
                showBearing(motion1, locationManager)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun showLocation(textview: TextView, locationManager: LocationManager) {
        textview.text = getLocation(locationManager).toString()
    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun showSpeed(textview: TextView, locationManager: LocationManager) {
        textview.text = "SPEED: " + getLocation(locationManager)?.getSpeed().toString()
    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun showBearing(textview: TextView, locationManager: LocationManager) {
        textview.text = "BEARING: " + getLocation(locationManager)?.getBearing().toString()
    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun showAltitude(textview: TextView, locationManager: LocationManager) {
        textview.text = "ALTITUDE: " + getLocation(locationManager)?.getAltitude().toString()
    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun showLatitude(textview: TextView, locationManager: LocationManager) {
        textview.text = "LATITUDE: " + getLocation(locationManager)?.getLatitude().toString()
    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun showLongitude(textview: TextView, locationManager: LocationManager) {
        textview.text = "SPEED: " + getLocation(locationManager)?.getLongitude().toString()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun showDistance(textview: TextView, locationManager: LocationManager) {
        var loc:Location
        var loc2:Location

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!

        textview.text = "SPEED: " + getLocation(locationManager)?.getLongitude().toString()
    }

    //获取位置信息
    @RequiresApi(Build.VERSION_CODES.M)
    fun getLocation(locationManager: LocationManager): Location? {
        var location: Location? = null
        if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            toast("没有位置权限")
        }
        else if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            toast("没有打开GPS")
        }
        else {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location == null) {
                toast("位置信息为空")
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (location == null) {
                    toast("网络位置信息也为空")

                }
                else {
                    toast("当前使用网络位置")
                }
            }
        }
        return location
    }
}


