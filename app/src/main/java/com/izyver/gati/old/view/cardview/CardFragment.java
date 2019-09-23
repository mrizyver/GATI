package com.izyver.gati.old.view.cardview;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.izyver.gati.R;
import com.izyver.gati.old.listeners.OnImageClickListener;
import com.izyver.gati.old.listeners.OnImageLongClickListener;
import com.izyver.gati.old.model.ApplicationData;
import com.izyver.gati.old.presenter.CardPresenter;
import com.izyver.gati.old.view.FragmentActivity;
import com.izyver.gati.old.view.FragmentActivityChild;
import com.izyver.gati.old.view.FragmentImagePreview;

import org.jetbrains.annotations.NotNull;

import static com.izyver.gati.old.presenter.CardPresenter.REQUEST_CODE_READE_WRITE_TO_SHARE_IMAGE;

public final class CardFragment extends Fragment implements
        CardView, OnImageLongClickListener, OnImageClickListener,
        FragmentActivityChild {

    public static final String TAG = "CardFragment";
    private static final String KEY_TYPE_OF_SCHEDULE = "schedule_type";
    private static final int ITEM_SHARE_IMAGE = 165;
    private CardPresenter presenter;
    private RecyclerCardAdapter cardAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private int scheduleType = -1;
    private int indexLastImageClicked = -1;

    public static CardFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt(KEY_TYPE_OF_SCHEDULE, type);
        CardFragment fragment = new CardFragment();
        fragment.setArguments(args);
        return fragment;
    }

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
        recyclerCardList.setAnimationCacheEnabled(false);
        recyclerCardList.setLayoutManager(new LinearLayoutManager(context));
        recyclerCardList.setAdapter(cardAdapter);

        swipeRefresh = view.findViewById(R.id.card_refresh_view);
        swipeRefresh.setOnRefreshListener(() -> {
            presenter.updateImages(() -> swipeRefresh.setRefreshing(false));
        });

        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        FragmentActivity activity = (FragmentActivity) getActivity();
        if (activity != null) {
            activity.showToolbar();
        }

        if (savedInstanceState != null) {
            scheduleType = savedInstanceState.getInt(KEY_TYPE_OF_SCHEDULE, scheduleType);
        }

        createPresenter(scheduleType);
        cardAdapter.toPosition(presenter.currentDay);
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
            case REQUEST_CODE_READE_WRITE_TO_SHARE_IMAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.shareImage(indexLastImageClicked);
                } else {
                    presenter.shareFailure();
                }
                break;
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
    public void setVisibilitySaturday(boolean isExistSaturday) {
        cardAdapter.setVisibilitySaturday(isExistSaturday);
        FragmentActivity fragmentActivity = (FragmentActivity) getActivity();
        if (fragmentActivity != null)
            fragmentActivity.setVisibilitySaturday(isExistSaturday);
    }

    @Override
    public void onImageClicked(int index, View view) {
        FragmentImagePreview imagePreview = FragmentImagePreview.newInstance(index);
        imagePreview.setBitmap(presenter.getSchedule(index));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imagePreview.setEnterTransition(new Fade());
            setExitTransition(new Fade());
        }

        FragmentActivity activity = (FragmentActivity) getActivity();
        if (activity == null) return;
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, imagePreview)
                .addToBackStack(null)
                .commit();
        activity.hideToolbar();
    }

    @Override
    public void setItem(int index) {
        cardAdapter.toPosition(index);
    }


    /* ----------internal logic---------- */

    private void createPresenter(int scheduleType) {
        Object restoredPresenter = ApplicationData.cache.get(CardPresenter.getKey(scheduleType));
        if (restoredPresenter != null) {
            this.presenter = (CardPresenter) restoredPresenter;
        } else {
            this.presenter = CardPresenter.getInstance(scheduleType);
        }
        this.presenter.attachView(this);
    }
}
