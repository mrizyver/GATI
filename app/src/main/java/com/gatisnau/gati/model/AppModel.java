package com.gatisnau.gati.model;

import android.graphics.Bitmap;

import com.gatisnau.gati.listeners.OnImageDownloaded;
import com.gatisnau.gati.model.db.ImageEntity;
import com.gatisnau.gati.model.db.ImagesDAO;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class AppModel implements Model {

    public static final String TAG = "AppModel";

    @Override
    public List<ScheduleObject.Schedule> getExistingSchedule() throws IOException {
        ScheduleObject body = ApplicationData.gatiApi.getSchedulers().execute().body();

        return body != null ? body.getSchedule() : new ArrayList<>();
    }

    @Override
    public void downloadImage(ScheduleObject.Schedule schedule, OnImageDownloaded downloadListener) throws IOException, ParseException {
        ImagesDAO imagesDAO = ApplicationData.database.imageDao();
        ImageEntity entity = imagesDAO.getEntityByKey(schedule.getDate());

        Bitmap bitmap;
        if (entity == null || entity.image == null){
            String url = createUrl(schedule.getImage());
            bitmap = Picasso.get()
                    .load(url)
                    .get();
            imagesDAO.putImageEntity(new ImageEntity(schedule.getId(), schedule.getDate(), bitmap));
        }else {
            bitmap = entity.getImageBitmap();
        }

        downloadListener.itemDownloaded(bitmap, schedule);
    }

    private String createUrl(String imageName) {
        return ApplicationData.BASE_URL + "images/schelude/" + imageName + ".png";
    }
}
