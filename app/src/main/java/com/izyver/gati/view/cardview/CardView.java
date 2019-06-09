package com.izyver.gati.view.cardview;

import android.content.Context;
import android.graphics.Bitmap;

public interface CardView {
    void updateCard(Bitmap bitmap, boolean isOld, int index);

    Context getContext();
}
