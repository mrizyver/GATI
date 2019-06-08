package com.izyver.gati.view.cardview;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.fragment.app.Fragment;

public interface CardView {
    void updateCard(Bitmap bitmap, int index);
    Context getContext();
}
