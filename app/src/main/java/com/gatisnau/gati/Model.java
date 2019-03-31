package com.gatisnau.gati;

import android.graphics.Bitmap;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class Model {

    public List<ScheduleObject.Schedule> getExistingSchedule() throws IOException {

        return ApplicationData.gatiApi.getSchedulers().execute().body().getSchedule();

    }

    public void downloadImage(ScheduleObject.Schedule schedule, Presenter.OnImageDownloaded downloadListener) throws IOException {
        String url = createUrl(schedule.getImage());
        Picasso.get().setIndicatorsEnabled(true);
        Bitmap bitmap = Picasso.get()
                .load(url)
                .get();
        downloadListener.itemDownloaded(bitmap, schedule);
    }

    private String createUrl(String imageName) {
        return "http://gatisnau.sumy.ua/images/schelude/" + imageName + ".png";
    }
}
