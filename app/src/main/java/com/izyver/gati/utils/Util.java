package com.izyver.gati.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import com.izyver.gati.R;

public class Util {
    public static Point getScreenSize(Context context){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) return new Point(0,0);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static int getTitleId(int pos) {
        switch (pos) {
            case 0:
                return R.string.mon;
            case 1:
                return R.string.tue;
            case 2:
                return R.string.wed;
            case 3:
                return R.string.thu;
            case 4:
                return R.string.fri;
            case 5:
                return R.string.sat;
            default:
                return 0;
        }
    }
}
