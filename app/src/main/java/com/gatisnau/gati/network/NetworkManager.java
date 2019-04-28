package com.gatisnau.gati.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.gatisnau.gati.model.ApplicationData;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkManager {

    private static final String TAG = "NetworkManager";

    public boolean isNetworkAvailable(Context context) {
        if (context == null) return false;
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        if (connectivityManager != null) {
            return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
        }else {
            return false;
        }
    }

    public boolean isInternetAvailable() {
        try {
            final InetAddress address = InetAddress.getByName(ApplicationData.BASE_URL);
            return !address.equals("");
        } catch (UnknownHostException e) {
            Log.e(TAG, "isInternetAvailable: ", e);
        }
        return false;
    }
}
