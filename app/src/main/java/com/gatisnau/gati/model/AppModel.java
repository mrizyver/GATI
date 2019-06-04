package com.gatisnau.gati.model;

import android.graphics.Bitmap;

import com.gatisnau.gati.BuildConfig;
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
            schedule.setDate(imageEntity.date);
            schedule.setType(imageEntity.type);
            schedule.setTitle(imageEntity.title);
            schedule.setDayWeek(imageEntity.dayWeek);

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
    public void loadImage(Schedule schedule, OnImageDownloaded downloadedListener) throws ParseException {
        ImagesDAO imagesDAO = getImagesDAO();
        if (schedule.getDayWeek() == null || schedule.getType() == null) return;
        String imageKey = schedule.getDayWeek() + schedule.getType();
        ImageEntity entity = imagesDAO.getEntityByKey(imageKey);
        Bitmap bitmap;
        try {
            bitmap = entity.getImageBitmap();
        } catch (NullPointerException e) {
            if (BuildConfig.DEBUG){
                throw e;
            }
            e.printStackTrace();
            return;
        }
        downloadedListener.itemDownloaded(bitmap, schedule);
    }

    @Override
    public void downloadImage(Schedule schedule, OnImageDownloaded downloadListener) throws IOException, ParseException {
        String url = createUrl(schedule.getImage());
        Bitmap bitmap = Picasso.get()
                .load(url)
                .get();
        downloadListener.itemDownloaded(bitmap, schedule);
        saveImageToDb(schedule, bitmap);
    }


    /* ----------internal logic---------- */

    private ImagesDAO getImagesDAO() {
        return ApplicationData.database.imageDao();
    }

    private void saveImageToDb(Schedule schedule, Bitmap bitmap) {
        ImagesDAO imagesDAO = getImagesDAO();
        if (imagesDAO != null && bitmap != null){
            ImageEntity imageEntity = new ImageEntity(
                    schedule.getId(),
                    schedule.getDate(),
                    schedule.getType(),
                    bitmap,
                    schedule.getTitle(),
                    schedule.getDayWeek()
            );
            imagesDAO.putImageEntity(imageEntity);
        }else {
            if (BuildConfig.DEBUG){
                throw new NullPointerException("can not use nullable image dao");
            }
        }
    }

    private String createUrl(String imageName) {
        return PREFIX + BASE_URL + "/images/schedule/" + imageName + ".png";
    }
}
