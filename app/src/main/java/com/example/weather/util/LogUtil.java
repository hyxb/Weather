package com.example.weather.util;

import android.util.Log;

public class LogUtil {

    public static  Boolean isLog = true;
    public static final String TAG = "Weather Log";

    public static void logVerbose(String msg){
        if (isLog){
          Log.v(TAG,msg);
        }
    }
    public static void logVerbose(String TAG,String msg){
        if (isLog){
            Log.v(TAG,msg);
        }
    }

    public static void logDebug(String msg){
        if (isLog){
            Log.d(TAG,msg);
        }
    }
    public static void logDebug(String TAG,String msg){
        if (isLog){
            Log.d(TAG,msg);
        }
    }

    public static void logInfo(String msg){
        if (isLog){
            Log.i(TAG,msg);
        }
    }
    public static void logInfo(String TAG,String msg){
        if (isLog){
            Log.i(TAG,msg);
        }
    }

    public static void logWarn(String msg){
        if (isLog){
            Log.w(TAG,msg);
        }
    }

    public static void logWarn(String TAG,String msg){
        if (isLog){
            Log.w(TAG,msg);
        }
    }

    public static void logError(String msg){
        if (isLog){
            Log.e(TAG,msg);
        }
    }
    public static void logError(String TAG,String msg){
        if (isLog){
            Log.e(TAG,msg);
        }
    }

}
