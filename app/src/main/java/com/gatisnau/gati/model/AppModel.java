package com.gatisnau.gati.model;

import android.graphics.Bitmap;

import com.gatisnau.gati.listeners.OnImageDownloaded;
import com.gatisnau.gati.model.ScheduleObject.Schedule;
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
    public List<Schedule> getExistingSchedule() throws IOException {
        ScheduleObject body = ApplicationData.gatiApi.getSchedulers().execute().body();

        return body != null ? body.getSchedule() : new ArrayList<>();
    }

    @Override
    public List<Schedule> getLocalSchedule(){
        ImagesDAO imagesDAO = getImagesDAO();
        List<ImageEntity> localImage = imagesDAO.getAll();
        List<Schedule> localSchedule = new ArrayList<>();
        ScheduleObject sObject = new ScheduleObject();
        for (ImageEntity imageEntity : localImage) {
            Schedule schedule = sObject.new Schedule();
            schedule.setId(imageEntity.id);
            schedule.setDate(imageEntity.key);
            schedule.setType(imageEntity.type);
            localSchedule.add(schedule);
        }

        return localSchedule;
    }

    @Override
    public void downloadImage(Schedule schedule, OnImageDownloaded downloadListener) throws IOException, ParseException {
        ImagesDAO imagesDAO = getImagesDAO();
        ImageEntity entity = imagesDAO.getEntityByKey(schedule.getDate());

        Bitmap bitmap;
        if (entity == null || entity.image == null){
            String url = createUrl(schedule.getImage());
            bitmap = Picasso.get()
                    .load(url)
                    .get();
            imagesDAO.putImageEntity(new ImageEntity(schedule.getId(), schedule.getDate(), schedule.getType(), bitmap));
        }else {
            bitmap = entity.getImageBitmap();
        }

        downloadListener.itemDownloaded(bitmap, schedule);
    }

    /* ----------internal logic---------- */

    private ImagesDAO getImagesDAO() {
        return ApplicationData.database.imageDao();
    }


    private String createUrl(String imageName) {
        return ApplicationData.BASE_URL + "/images/schelude/" + imageName + ".png";
    }
}
