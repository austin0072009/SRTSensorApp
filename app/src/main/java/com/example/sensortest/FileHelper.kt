package com.example.sensortest

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.toast
import java.io.*

/*
 * 使用GSON将对象JSON序列化以及反序列化
 * */
fun serialize(src:Any): String = Gson().toJson(src)
fun serialize(vararg srcs: Any): String = Gson().toJson(srcs)
inline fun <reified T> deserialize(json: String): T = Gson().fromJson<T>(json, object : TypeToken<T>() {}.type)

/*
 * 这里定义的是一个文件保存的方法，写入到文件中，所以是输出流
 * */
fun Context.FileSave(fileContent: String, filepath: String? = null, filename: String) {
    //这里我们使用私有模式,创建出来的文件只能被本应用访问,还会覆盖原文件哦
    if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
        val myExternalFile: File = File(getExternalFilesDir(filepath), filename)
        try {
            //verifyStoragePermissions()
            val fileOutPutStream = FileOutputStream(myExternalFile)
            fileOutPutStream.write(fileContent.toByteArray())
            fileOutPutStream.close()
            applicationContext.toast("数据写入成功")      //直接省略写toast即this.toast也是可以的，但是可能会内存外泄
        } catch (e: IOException) {
            e.printStackTrace()
            applicationContext.toast("数据写入失败")
        }
    } else applicationContext.toast("SD卡不存在或者不可读写")
}

/*
 * 这里定义的是文件读取的方法
 * */
fun Context.FileLoad(filepath: String? = null, filename: String): String? {
    if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
        val myExternalFile: File = File(getExternalFilesDir(filepath), filename)
        try {
            //verifyStoragePermissions()
            var fileInputStream = FileInputStream(myExternalFile)
            var inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder: StringBuilder = StringBuilder()
            var text: String? = null
            while ({ text = bufferedReader.readLine(); text }() != null) {
                stringBuilder.append(text)
            }
            fileInputStream.close()
            return stringBuilder.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    } else applicationContext.toast("SD卡不存在或者不可读写")
    return null
}
