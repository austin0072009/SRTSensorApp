package com.example.sensortest;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

//检查Service是否已经在运行
public class ServiceCheckUtil {
    public static boolean isRunning(Context context, String serviceName) {
        // 获取Activity管理者对象
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        // 获取正在运行的服务（此处设置最多取1000个）
        List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(1000);
        // 遍历，若存在名字和传入的serviceName的一致则说明存在
        for (int i = 0; i < runningServices.size(); i++) {
            if (runningServices.get(i).service.getClassName().toString().equals(serviceName))
                return true;
        }
        return false;
    }
}
