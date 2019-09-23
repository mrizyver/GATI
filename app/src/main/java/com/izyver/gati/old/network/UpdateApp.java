package com.izyver.gati.old.network;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

import com.izyver.gati.BuildConfig;
import com.izyver.gati.R;
import com.izyver.gati.old.utils.GatiFileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class UpdateApp {
    private String fileName = "gati.apk";
    private Context context;
    private Thread thread;
    private Handler handler;

    public UpdateApp(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    public void startUpdate(String urlCheckVersion, String urlUpdate) {
        stop();
        thread = new Thread(() -> {
            if (hasNewVersion(urlCheckVersion)) {
                downloadFIle(urlUpdate, fileName);
                startInstall(fileName);
            } else {
                handler.post(this::toastHasLastVersion);
            }
        });
        thread.start();
    }

    public void checkVersion(String url) {
        stop();
        thread = new Thread(() -> {
            if (hasNewVersion(url)) {
                handler.post(() -> toast(R.string.update_is_exist));
            }
        });
        thread.start();
    }

    public void stop() {
        if (thread == null) return;
        thread.interrupt();
        thread = null;
    }

    private boolean hasNewVersion(String _url) {
        try {
            URL url = new URL(_url);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(5000);
            byte[] versionInByte = new byte[5];
            try (InputStream inputStream = urlConnection.getInputStream();) {
                inputStream.read(versionInByte);
            }
            float versionCode = BuildConfig.VERSION_CODE;
            String versionInString = new String(versionInByte);
            float versionCheck = Float.parseFloat(versionInString);
            if (versionCode < versionCheck)
                return true;
            else
                return false;
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void downloadFIle(String _url, String fileName) {
        FileOutputStream out = null;
        InputStream input = null;
        try {

            URL url = new URL(_url);
            URLConnection urlConnection = url.openConnection();

            if (context == null) {
                error();
                return;
            }
            Uri uri = getUri(new File(context.getFilesDir(), fileName));
            File outputFile =new File(context.getFilesDir(), fileName);
            if (!outputFile.exists()){
                outputFile.createNewFile();
            }
            out = new FileOutputStream(outputFile);
            if (outputFile.exists())
                input = urlConnection.getInputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
            error();
        } finally {
            try {
                if (input != null)
                    input.close();
                if (out != null)
                    out.close();
            } catch (IOException ignored) {
            }
        }
    }

    public void startInstall(String fileName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(context.getFilesDir(), fileName);
        Uri uri = getUri(file);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }


    private Uri getUri(File file) {
        return GatiFileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, file);
    }


    private void error() {
        handler.post(this::toastErrorToDownload);
    }

    private void toastHasLastVersion() {
        toast(R.string.toast_last_version);
    }

    private void toastErrorToDownload() {
        toast(R.string.toast_exception_download);
    }

    private void toast(int id) {
        Toast.makeText(context, id, Toast.LENGTH_LONG).show();
    }
}
