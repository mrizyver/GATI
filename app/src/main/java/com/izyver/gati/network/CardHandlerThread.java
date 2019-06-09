package com.izyver.gati.network;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.izyver.gati.presenter.CardPresenter;

public class CardHandlerThread extends HandlerThread {

    public CardHandlerThread(String name) {
        super(name);
    }

    private Handler requestHandler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        requestHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == CardPresenter.MESSAGE_DOWNLOAD_IMAGE){
                    resizeImage((Bitmap) msg.obj, msg.arg1);
                }else if (msg.what == CardPresenter.MESSAGE_RESIZE_IMAGE){

                }
            }
        };
    }

    public void queueResize(Bitmap bitmap, int index){
        requestHandler.obtainMessage(
                CardPresenter.MESSAGE_RESIZE_IMAGE,
                index,
                -1,
                bitmap
        ).sendToTarget();
    }

    private void resizeImage(Bitmap bitmap, int index){

    }
}
