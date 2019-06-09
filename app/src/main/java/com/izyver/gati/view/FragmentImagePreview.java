package com.izyver.gati.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.izyver.gati.R;
import com.github.chrisbanes.photoview.PhotoView;

public class FragmentImagePreview extends Fragment {

    private Bitmap bitmap;

    public static FragmentImagePreview newInstance() {
        return new FragmentImagePreview();
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_preview_fragment, container, false);
        PhotoView photoView = view.findViewById(R.id.scaled_photo_view);
        photoView.setImageBitmap(bitmap);
        return view;
    }
}