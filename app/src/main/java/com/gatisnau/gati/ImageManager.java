package com.gatisnau.gati;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.gatisnau.gati.Schedule.ImageDatabase.cols;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class ImageManager  {
    private Context context;
    SQLiteDatabase database;
    private static final int EXCEPTION = 0, ENDED_DOWNLOAD = 1, NOT_NEED_DOWNLOAD = 2;
    public ImageManager(Context context) {
        this.context = context;
        database = new DatabaseHelper(context).getWritableDatabase();
    }

    private class SetImageAT extends AsyncTask<Void, Void, Integer> {
        private int dayOfWeek;
        private boolean setToPhotoView = false;

        public SetImageAT(int day) {
            dayOfWeek = day;
        }

        public SetImageAT(int day, boolean setTo) {
            dayOfWeek = day;
            setToPhotoView = setTo;
        }

        @Override
        protected void onPreExecute() {
            ScheduleActivity.setVisibleProgressBar(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Void... v) {
            if (!Schedule.threadIsAlive[dayOfWeek - 2]) {
                try {
                    Schedule.threadIsAlive[dayOfWeek - 2] = true;

                    return downloadImageToDb(Schedule.url[dayOfWeek - 2], dayOfWeek);

                } catch (IOException e) {
                    Log.i("Download ", "exception " + dayOfWeek);
                    return EXCEPTION;
                } finally {
                    Schedule.threadIsAlive[dayOfWeek - 2] = false;
                }
            }
            return ENDED_DOWNLOAD;
        }

        @Override
        protected void onPostExecute(Integer result) {
            ScheduleActivity.setVisibleProgressBar(View.GONE);
            if (setToPhotoView) {
                if (result == 1) {
                    //--DANGERS--\\
                    ScheduleActivity.updateFragments();//dohera updates, performance degradation
                    //--DANGERS--\\
                    Log.i("onPostExecute: ", "set img po pv " + dayOfWeek);
                }
            }
        }
    }
        /**
         * * Скачивает картинку из интернета по указаному URL
         * и сохраняет ее в БАЗУ ДАННЫХ
         *
         * @param imgUrl    - URL для загрузки изображения
         * @param dayOfWeek
         */
        public int downloadImageToDb(String imgUrl, int dayOfWeek) throws IOException {
            Log.i("Download ", "start " + dayOfWeek);
            Cursor cursor = getCursor(dayOfWeek);
            try {
                URL url = new URL(imgUrl);
                URLConnection urlConnection = url.openConnection();
                urlConnection.setConnectTimeout(5000);
                int sizeContent = urlConnection.getContentLength();
                cursor.moveToFirst();
                int sizeBlob = cursor.getInt(cols.SIZE_INT);

                if (sizeBlob != sizeContent && !(dateIsSame(cursor.getLong(cols.DATE_INT), new Date().getTime()))) {
                    BufferedInputStream bis = new BufferedInputStream(urlConnection.getInputStream());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int readByte;
                    byte[] buffer = new byte[1024];
                    while ((readByte = bis.read(buffer)) > 0)
                        baos.write(buffer, 0, readByte);
                    bis.close();
                    baos.close();

                    ContentValues values = new ContentValues();
                    values.put(cols.IMAGE, baos.toByteArray());
                    values.put(cols.DATE, generateDate(dayOfWeek));
                    values.put(cols.SIZE, sizeContent);

                    database.update(Schedule.ImageDatabase.TABLE_NAME, values,
                            cols.DAY_OF_WEEK + " = ?",
                            new String[]{Integer.toString(dayOfWeek)});
                    return ENDED_DOWNLOAD;
                }
                Log.i("Download ", "end " + dayOfWeek);
            }finally {
                cursor.close();
            }
            return NOT_NEED_DOWNLOAD;
        }


        public int smartGetDay() {
            Calendar timeAtTheMoment = new GregorianCalendar();
            int dayOfWeek = timeAtTheMoment.get(Calendar.DAY_OF_WEEK) > 1 ? timeAtTheMoment.get(Calendar.DAY_OF_WEEK) : 2;

            Date fiveOclock = new Date();
            Date newDate = new Date();
            fiveOclock.setHours(17);

            if (newDate.after(fiveOclock)) {
                return dayOfWeek + 1;
            } else {
                return dayOfWeek;
            }
        }

        /**
         * method can set image from database to photoView
         * @param dayOfWeek - day, which we need fill
         * @return true, if db does not empty and false, if db is empty
         */
        public boolean setImageFromDb(int dayOfWeek) {
            Log.i("setImageFromDb:", " start " + (dayOfWeek) );
            Cursor cursor = getCursor(dayOfWeek);
             try {
                 cursor.moveToFirst();
                 byte[] bytes = cursor.getBlob(cols.IMAGE_INT);
                 if (bytes!=null) {
                     FragmentPageImage.setImageToPhotoViewFromDb(bytes);
                     return true;
                 } else {
                     FragmentPageImage.setImageNotFount();
                     new SetImageAT(dayOfWeek, true).execute();
                     return false;
                 }
             } finally {
                 cursor.close();
             }
        }

        public void startDownloadImage(int dayOfWeek) {
            new SetImageAT(dayOfWeek).execute();
        }
        public void startDownloadImage(int dayOfWeek, boolean setTo) {
            new SetImageAT(dayOfWeek, setTo).execute();
        }

        public boolean dateIsSame(long oldDate, long newDate) {
            Calendar oldCalendar = new GregorianCalendar();
            Calendar newCalendar = new GregorianCalendar();
            oldCalendar.setTime(new Date(oldDate));
            newCalendar.setTime(new Date(newDate));
            return newCalendar.get(Calendar.DAY_OF_WEEK) == oldCalendar.get(Calendar.DAY_OF_WEEK) &&
                    newCalendar.get(Calendar.MONTH) == oldCalendar.get(Calendar.MONTH) &&
                    newCalendar.get(Calendar.YEAR) == oldCalendar.get(Calendar.YEAR);

        }

        public Cursor getCursor(int dayOfWeek) {
            return database.query(
                    Schedule.ImageDatabase.TABLE_NAME,
                    null,
                    Schedule.ImageDatabase.cols.DAY_OF_WEEK + " = ?",
                    new String[]{Integer.toString(dayOfWeek)},
                    null,
                    null,
                    null
            );
        }

        public long generateDate(int dayOfWeek) {
            Calendar dateFromImage = new GregorianCalendar();
            int different = dateFromImage.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
            dateFromImage.add(Calendar.DAY_OF_MONTH, -different + dayOfWeek - 2);
            return dateFromImage.getTime().getTime();
        }
}

