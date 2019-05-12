package com.gatisnau.gati.model;

import android.graphics.Bitmap;

import com.gatisnau.gati.OnImageDownloaded;
import com.gatisnau.gati.ScheduleObject;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class AppModel implements Model {

    public static final String TAG = "AppModel";

    @Override
    public List<ScheduleObject.Schedule> getExistingSchedule() throws IOException {
        return ApplicationData.gatiApi.getSchedulers().execute().body().getSchedule();
    }

    @Override
    public void downloadImage(ScheduleObject.Schedule schedule, OnImageDownloaded downloadListener) throws IOException, ParseException {
        String url = createUrl(schedule.getImage());
        Bitmap bitmap = Picasso.get()
                .load(url)
                .get();
        downloadListener.itemDownloaded(bitmap, schedule);
    }

    private String createUrl(String imageName) {
        return ApplicationData.BASE_URL + "images/schelude/" + imageName + ".png";
    }

}
