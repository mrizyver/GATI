package com.gatisnau.gati;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.util.Log;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.List;

public class Model {

    public static final String TAG = "Model";
    public static final String BASE_URL = "http://gatisnau.sumy.ua/";

    public List<ScheduleObject.Schedule> getExistingSchedule() throws IOException {

        return ApplicationData.gatiApi.getSchedulers().execute().body().getSchedule();

    }

    public void downloadImage(ScheduleObject.Schedule schedule, OnImageDownloaded downloadListener) throws IOException, ParseException {
        String url = createUrl(schedule.getImage());
        Picasso.get().setIndicatorsEnabled(true);
        Bitmap bitmap = Picasso.get()
                .load(url)
                .get();
        downloadListener.itemDownloaded(bitmap, schedule);
    }

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
            final InetAddress address = InetAddress.getByName(BASE_URL);
            return !address.equals("");
        } catch (UnknownHostException e) {
            Log.e(TAG, "isInternetAvailable: ", e);
        }
        return false;
    }

    private String createUrl(String imageName) {
        return BASE_URL + "images/schelude/" + imageName + ".png";
    }

}
