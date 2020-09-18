package com.example.sensortest;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

public class FileHelper {

    private Context mContext;

    public FileHelper() {
    }

    public FileHelper(Context mContext) {
        super();
        this.mContext = mContext;
    }

    /*
     * 这里定义的是一个文件保存的方法，写入到文件中，所以是输出流
     * */
    public void save(Vector<float[]> filecontent) throws Exception {
        //这里我们使用私有模式,创建出来的文件只能被本应用访问,还会覆盖原文件哦
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            String filename = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + "SensorDataRecord";
            FileOutputStream output = new FileOutputStream(filename);

            JSONArray jsonArray = new JSONArray();

            for (int i = 0; i < filecontent.size(); i++) {
                JSONObject json = new JSONObject();
                json.put("Acceleration_X", filecontent.get(i)[0]);
                json.put("Acceleration_Y", filecontent.get(i)[1]);
                json.put("Acceleration_Z", filecontent.get(i)[2]);
                jsonArray.put(json);


            }

            String s = jsonArray.toString();
            output.write(s.getBytes());
            output.close();         //关闭输出流
        }
        else Toast.makeText(mContext, "SD卡不存在或者不可读写", Toast.LENGTH_SHORT).show();
    }

    /*
     * 这里定义的是文件读取的方法
     * */
    public String read(String filename) throws IOException, FileNotFoundException {
        //打开文件输入流
        FileInputStream input = mContext.openFileInput(filename);
        byte[] temp = new byte[1024];
        StringBuilder sb = new StringBuilder("");
        int len = 0;
        //读取文件内容:
        while ((len = input.read(temp)) > 0) {
            sb.append(new String(temp, 0, len));
        }
        //关闭输入流
        input.close();
        return sb.toString();
    }

}