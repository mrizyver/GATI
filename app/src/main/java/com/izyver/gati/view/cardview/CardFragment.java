package com.izyver.gati.view.cardview;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.izyver.gati.listeners.OnImageClickListener;
import com.izyver.gati.listeners.OnImageLongClickListener;
import com.izyver.gati.model.ApplicationData;
import com.izyver.gati.presenter.CardPresenter;
import com.izyver.gati.presenter.PresenterActivity;
import com.izyver.gati.view.FragmentActivity;
import com.izyver.gati.R;
import com.izyver.gati.view.FragmentImagePreview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public final class CardFragment extends Fragment implements
        CardView, OnImageLongClickListener, OnImageClickListener {

    public static final String TAG = "CardFragment";
    private static final String KEY_TYPE_OF_SCHEDULE = "schedule_type";
    private static final int ITEM_SHARE_IMAGE = 165;

    public static CardFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt(KEY_TYPE_OF_SCHEDULE, type);
        CardFragment fragment = new CardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private CardPresenter presenter;
    private RecyclerCardAdapter cardAdapter;

    private int scheduleType = -1;
    private int indexLastImageClicked = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            scheduleType = getArguments().getInt(KEY_TYPE_OF_SCHEDULE, scheduleType);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.card_fragment, container, false);

        Context context = getActivity();
        if (context == null) return view;

        cardAdapter = new RecyclerCardAdapter(context);
        cardAdapter.setImageClickListener(this);
        cardAdapter.setImageLongClickListener(this);
        RecyclerView recyclerCardList = view.findViewById(R.id.recycler_card);
        recyclerCardList.setLayoutManager(new LinearLayoutManager(context));
        recyclerCardList.setAdapter(cardAdapter);

        if (!(getActivity() instanceof FragmentActivity)) return view;

        return view;
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            scheduleType = savedInstanceState.getInt(KEY_TYPE_OF_SCHEDULE, scheduleType);
        }

        createPresenter(scheduleType);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_TYPE_OF_SCHEDULE, scheduleType);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        ApplicationData.cache.put(CardPresenter.getKey(scheduleType), presenter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PresenterActivity.REQUEST_CODE_READE_WRITE_TO_SHARE_IMAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.shareImage(indexLastImageClicked);
                } else {
                    presenter.shareFailure();
                }
        }
    }

    /* ----------context menu---------- */

    @Override
    public void onCreateContextMenu(ContextMenu menu, @NotNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, ITEM_SHARE_IMAGE, Menu.NONE, R.string.share);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ITEM_SHARE_IMAGE:
                presenter.shareImage(indexLastImageClicked);
        }
        return true;
    }

    @Override
    public void onViewLongClick(View view, int position, float x, float y) {
        indexLastImageClicked = position;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view.showContextMenu(x, y);
        } else {
            view.showContextMenu();
        }
    }


    /* ----------interface---------- */

    @Override
    public void updateCard(Bitmap bitmap, boolean isOld, int index) {
        cardAdapter.updateItem(bitmap, isOld, index);
    }

    @Override
    public void onImageClicked(int index) {
        FragmentImagePreview fragmentImage = FragmentImagePreview.newInstance();
        fragmentImage.setBitmap(presenter.getSchedule(index));
        //      stackFragment.addToStackFragment(fragmentImage);
    }

    /* ----------internal logic---------- */

    private void createPresenter(int scheduleType) {
        Object restoredPresenter = ApplicationData.cache.get(CardPresenter.getKey(scheduleType));
        if (restoredPresenter != null){
            this.presenter = (CardPresenter) restoredPresenter;
        }else {
            this.presenter = CardPresenter.getInstance(scheduleType);
        }

        this.presenter.attachView(this);
    }
}
