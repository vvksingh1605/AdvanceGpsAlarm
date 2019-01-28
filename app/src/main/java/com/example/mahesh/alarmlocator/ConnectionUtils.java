package com.example.mahesh.alarmlocator;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by SIS312 on 11/16/2016.
 */

public class ConnectionUtils {



    public static boolean isInternetConnected(Context context) {
        NetworkInfo networkInfo = null;
        ConnectivityManager connectivityManager = null;
        if (context != null) {
            connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void showconnectionerror(Context act){
        String text="Please check internet connection";
        Toast.makeText(act,text,Toast.LENGTH_LONG).show();
    }
}
