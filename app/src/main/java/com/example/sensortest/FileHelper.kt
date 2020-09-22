package com.example.sensortest

import android.content.Context
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/*
 * 这里定义的是一个文件保存的方法，写入到文件中，所以是输出流
 * */
@Throws(Exception::class)
inline fun Filesave(mContext: Context, fileContent: String, filepath:String?=null,filename: String?) {
    //这里我们使用私有模式,创建出来的文件只能被本应用访问,还会覆盖原文件哦
    if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
        val myExternalFile: File = File(mContext.getExternalFilesDir(filepath), filename)
        try {
            val fileOutPutStream = FileOutputStream(myExternalFile)
            fileOutPutStream.write(fileContent.toByteArray())
            fileOutPutStream.close()
            Toast.makeText(mContext, "数据写入成功", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(mContext, "数据写入失败", Toast.LENGTH_SHORT).show()
        }
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
