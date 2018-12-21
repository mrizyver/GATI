package com.gatisnau.gati;

import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.github.chrisbanes.photoview.PhotoView;

public class FragmentPageImage extends Fragment {
    private final String STATE_SCALE = "scale";

    private int day;
    private float scale;
    private static PhotoView photoView;
    private ImageManager imageManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        day = getArguments().getInt(Schedule.KEY_DAY_OF_WEEK);
        return inflater.inflate(
                R.layout.content_fragment,
                container,
                false);
    }

    @Override
    public void onViewCreated( View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        photoView = (PhotoView) view.findViewById(R.id.photo_view);

        if (getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE){
            photoView.setMaximumScale(6);
            photoView.setMediumScale(4.5f);
            photoView.setMinimumScale(3);
            photoView.setScale(3);
        }

        imageManager = new ImageManager(getActivity().getApplicationContext());
        if (imageManager.setImageFromDb(day)){
           imageManager.startDownloadImage(day, true);
        }
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
    }

    public static void setImageToPhotoViewFromDb(byte[] image){
        if(photoView!=null){
            photoView.setImageBitmap(BitmapFactory
                    .decodeByteArray(image, 0, image.length));
        }else {
            Log.e("setImageToPhotoView", "photoView==null) ");
        }
    }
        public static void setImageNotFount(){
        if(photoView!=null){
            Log.e("setImageNotFount", "start");
            photoView.setImageResource(R.drawable.page_not_found);
            photoView.setZoomable(false);
        }
    }
}
