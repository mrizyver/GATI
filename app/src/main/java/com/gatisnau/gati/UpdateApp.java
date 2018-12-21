package com.gatisnau.gati;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;


public class UpdateApp extends AsyncTask<String, Void, Integer> {
    private String fileName = "app.apk";
    private static Context context;

    public UpdateApp(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        ScheduleActivity.setVisibleProgressBar(View.VISIBLE);
    }

    @Override
    protected Integer doInBackground(String... strings) {

        if (hasNewVersion(strings[1])){
            FileOutputStream out = null;
            InputStream input = null;
            try {
                Log.v("UpdateApp:", " download start");

                URL url = new URL(strings[0]);
                URLConnection urlConnection = (URLConnection) url.openConnection();

                File file = new File(Schedule.PATH_TO_DOWNLOAD_FOLDER);
                file.mkdirs();
                File outputFile = new File(file, fileName);
                out = new FileOutputStream(outputFile);
                if (outputFile.exists())
                    input = urlConnection.getInputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = input.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }

                Log.v("UpdateApp:", " download end");

            } catch (IOException e) {
                e.printStackTrace();
                Log.v("UpdateApp:", " download error");
                return 0;

            } finally {
                try {
                    if (input != null)
                        input.close();
                    if (out != null)
                        out.close();
                } catch (IOException e) {
                }
            }
            return 1;
        }else
            return 2;
    }

    @Override
    protected void onPostExecute(Integer result) {
        ScheduleActivity.setVisibleProgressBar(View.GONE);

        if(result==1)
            startActivityInstall();
        else if (result==2)
            Toast.makeText(context, R.string.toast_last_version, Toast.LENGTH_LONG).show();
        else if (result==0){
            Toast.makeText(context, R.string.toast_exception_download, Toast.LENGTH_LONG).show();
        }
    }

    public static boolean hasNewVersion(String _url) {
        try {
            URL url = new URL(_url);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(5000);
            byte[] versionInByte = new byte[3];
            try (InputStream inputStream = urlConnection.getInputStream();) {
                inputStream.read(versionInByte);
            }
            float versionCode = BuildConfig.VERSION_CODE;
            String versionInString = new String(versionInByte);
            float versionCheck = Float.parseFloat(versionInString);
            if (versionCode<versionCheck)
                return true;
            else
                return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void checkUpdate(String _url, Context context){
        Calendar calendar = Calendar.getInstance();
        class StartCheck extends AsyncTask<String, Void, Boolean>{

            @Override
            protected Boolean doInBackground(String... strings) {
                if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY)
                    return hasNewVersion(strings[0]);
                else
                    return false;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if(aBoolean){
                    Toast.makeText(context, R.string.update_is_exist, Toast.LENGTH_LONG).show();
                }
            }
        }
        new StartCheck().execute(_url);
    }

    public void startActivityInstall(){
        Intent intent = new Intent();
        intent.setDataAndType(
                Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/"+fileName)),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
