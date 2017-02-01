package org.fablabsantiago.smartcities.app.appmobile.Utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;

public class ServiceUtils
{
    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
