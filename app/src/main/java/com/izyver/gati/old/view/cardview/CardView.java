package com.izyver.gati.old.view.cardview;

import android.content.Context;
import android.graphics.Bitmap;

public interface CardView {
    void updateCard(Bitmap bitmap, boolean isOld, int index);

    Context getContext();

    void setVisibilitySaturday(boolean isExistSaturday);
}
