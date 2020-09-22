package com.example.sensortest

import android.content.Context
import android.os.Environment
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import threeDvector.Vec3D
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/*
 * 这里定义的是一个文件保存的方法，写入到文件中，所以是输出流
 * */
@Throws(Exception::class)
fun VecDatasave(mContext: Context, filecontent: ArrayList<Vec3D>, vectorname: String) {
    //这里我们使用私有模式,创建出来的文件只能被本应用访问,还会覆盖原文件哦
    if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
        var myExternalFile: File = File(getExternalFilesDir(null), "SensorDataRecord")
        val output = FileOutputStream(myExternalFile)
        val jsonArray = JSONArray()
        for (item in filecontent.iterator()) {
            val json = JSONObject()
            json.put(vectorname + "_X", item[0])
            json.put(vectorname + "_Y", item[1])
            json.put(vectorname + "_Z", item[2])
            jsonArray.put(json)
        }
        val s = jsonArray.toString()
        output.write(s.toByteArray())
        output.close() //关闭输出流
    } else Toast.makeText(mContext, "SD卡不存在或者不可读写", Toast.LENGTH_SHORT).show()
}

/*
 * 这里定义的是文件读取的方法
 * */
@Throws(IOException::class, FileNotFoundException::class)
fun VecDatasave(mContext: Context, filename: String?): String {
    //打开文件输入流
    val input = mContext!!.openFileInput(filename)
    val temp = ByteArray(1024)
    val sb = StringBuilder("")
    var len = 0
    //读取文件内容:
    while (input.read(temp).also { len = it } > 0) {
        sb.append(String(temp, 0, len))
    }
    //关闭输入流
    input.close()
    return sb.toString()
}
