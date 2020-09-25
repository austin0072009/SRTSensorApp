package com.example.sensortest

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/*
 * 使用GSON将对象JSON序列化以及反序列化
 * */
fun serialize(vararg srcs: Any): String = Gson().toJson(srcs)
inline fun <reified T> deserialize(json: String): T = Gson().fromJson<T>(json, object : TypeToken<T>() {}.type)

/*
 * 这里定义的是一个文件保存的方法，写入到文件中，所以是输出流
 * */
fun Activity.FileSave(fileContent: String, filepath: String? = null, filename: String) {
    //这里我们使用私有模式,创建出来的文件只能被本应用访问,还会覆盖原文件哦
    if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
        val myExternalFile: File = File(getExternalFilesDir(filepath), filename)
        try {
            verifyStoragePermissions()
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
/*@Throws(IOException::class, FileNotFoundException::class)
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
}*/
