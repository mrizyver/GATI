package com.izyver.gati.old.model;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.izyver.gati.old.listeners.OnImageDownloaded;
import com.izyver.gati.old.model.entity.ScheduleObject;
import com.izyver.gati.old.model.entity.ScheduleObject.Schedule;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import static com.izyver.gati.old.model.ApplicationData.BASE_URL;
import static com.izyver.gati.old.model.ApplicationData.PREFIX;

public class AppModel implements Model {

    public static final String TAG = "AppModel";

    @Override
    public ScheduleObject getExistingSchedule() throws IOException {
        return ApplicationData.gatiApi.getSchedulers().execute().body();
    }

    @NonNull
    @Override
    public List getLocalImages(int type){
        return null;
    }

    @Override
    public void loadImage(Schedule schedule, OnImageDownloaded downloadedListener) throws ParseException {
//        ScheduleDAO scheduleDAO = getImagesDAO();
//        if (schedule.getDayWeek() == null || schedule.getType() == 0) return;
//        String imageKey = schedule.getDayWeek() + schedule.getType();
//        ImageEntity entity = scheduleDAO.getEntityByKey(imageKey);
//        Bitmap bitmap;
//        try {
//            bitmap = entity.getImageBitmap();
//        } catch (NullPointerException e) {
//            if (BuildConfig.DEBUG){
//                throw e;
//            }
//            e.printStackTrace();
//            return;
//        }
//        downloadedListener.itemDownloaded(bitmap, schedule);
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


    private void saveImageToDb(Schedule schedule, Bitmap bitmap) {
//        ScheduleDAO scheduleDAO = getImagesDAO();
//        if (scheduleDAO != null && bitmap != null){
//            ImageEntity imageEntity = new ImageEntity(
//                    schedule.getId(),
//                    schedule.getDate(),
//                    schedule.getType(),
//                    bitmap,
//                    schedule.getTitle(),
//                    schedule.getDayWeek()
//            );
//            scheduleDAO.putImageEntity(imageEntity);
//        }else {
//            if (BuildConfig.DEBUG){
//                throw new NullPointerException("can not use nullable image dao");
//            }
//        }
    }

    private String createUrl(String imageName) {
        return PREFIX + BASE_URL + "/images/schedule/" + imageName + ".png";
    }
}
