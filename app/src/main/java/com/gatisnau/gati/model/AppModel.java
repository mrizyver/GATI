package com.gatisnau.gati.model;

import android.graphics.Bitmap;

import com.gatisnau.gati.listeners.OnImageDownloaded;
import com.gatisnau.gati.model.ScheduleObject.Schedule;
import com.gatisnau.gati.model.db.ImageEntity;
import com.gatisnau.gati.model.db.ImagesDAO;
import com.gatisnau.gati.presenter.Presenter;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.gatisnau.gati.model.ApplicationData.BASE_URL;
import static com.gatisnau.gati.model.ApplicationData.PREFIX;

public class AppModel implements Model {

    public static final String TAG = "AppModel";

    @Override
    public ScheduleObject getExistingSchedule() throws IOException {
        return ApplicationData.gatiApi.getSchedulers().execute().body();
    }

    @Override
    public ScheduleObject getLocalSchedule(){
        ImagesDAO imagesDAO = getImagesDAO();
        List<ImageEntity> localImage = imagesDAO.getAll();
        List<Schedule> localDays = new ArrayList<>();
        List<Schedule> localZaos = new ArrayList<>();
        ScheduleObject sObject = new ScheduleObject();
        for (ImageEntity imageEntity : localImage) {
            Schedule schedule = sObject.new Schedule();
            schedule.setId(imageEntity.id);
            schedule.setDate(imageEntity.key);
            schedule.setType(imageEntity.type);

            if (imageEntity.type == Presenter.FULL_SCHEDULE)
                localDays.add(schedule);
            else if(imageEntity.type == Presenter.CORRESPONDENCE_SCHEDULE)
                localZaos.add(schedule);
        }

        ScheduleObject scheduleObject = new ScheduleObject();
        scheduleObject.setDay(localDays);
        scheduleObject.setZao(localDays);
        return scheduleObject;
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
        return PREFIX + BASE_URL + "/images/schedule/" + imageName + ".png";
    }
}
