package com.gatisnau.gati.view;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.gatisnau.gati.utils.GatiPreferences;
import com.gatisnau.gati.presenter.Presenter;
import com.gatisnau.gati.R;
import com.gatisnau.gati.view.cardview.RecyclerCardAdapter;
import com.gatisnau.gati.model.ApplicationData;
import com.google.android.material.navigation.NavigationView;

public class FragmentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int ITEM_SHARE_IMAGE = 165;
    private Presenter presenter;

    private TextView tvForm;
    private TextView tvTypeForm;

    private int indexLastImageClicked = -1;
    private int ANIMATION_TIME = 250;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        presenter = ApplicationData.presenter;
        presenter.setFragmentManager(getSupportFragmentManager());
        getLifecycle().addObserver(presenter.new ActivityLifecycleListener());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);

        presenter.attachActivity(this);

        tvForm = findViewById(R.id.tv_word_form);
        tvTypeForm = findViewById(R.id.tv_form_study);

        initNavigationView();
        initSwitchButton();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.monday_button) {
            presenter.setItem(0);
        } else if (id == R.id.tuesday_button) {
            presenter.setItem(1);
        } else if (id == R.id.wednesday_button) {
            presenter.setItem(2);
        } else if (id == R.id.thursday_button) {
            presenter.setItem(3);
        } else if (id == R.id.friday_button) {
            presenter.setItem(4);
        } else if (id == R.id.saturday_button) {
            presenter.setItem(5);
        } else if (id == R.id.inst_button) {
            presenter.startActivity("com.instagram.android", "https://www.instagram.com/gati_snau.official/");
        } else if (id == R.id.youtube_button) {
            presenter.startActivity("com.youtube.android", "https://www.youtube.com/channel/UC0Ccnd5F7fTyQ60C3LeyhFw");
        } else if (id == R.id.facebook_button) {
            presenter.startActivity("com.facebook.katana", "https://www.facebook.com/official.gatisnau/");
        } else if (id == R.id.site_button) {
            presenter.startActivity("com.android.chrome/com.android.chrome.Main", "http://gatisnau.sumy.ua/");
        } else if (id == R.id.update_app) {
            presenter.updateApp();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Presenter.REQUEST_CODE_READE_WRITE_TO_SHARE_IMAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.shareImage(indexLastImageClicked);
                } else {
                    presenter.shareFailure();
                }
        }
    }

    /* ----------context menu---------- */

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
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

    /* ----------interface---------- */

    public void attachRecyclerAdapter(RecyclerCardAdapter adapter) {
        presenter.attachRecyclerAdapter(adapter);
    }


    /* ----------internal logic---------- */

    private void initNavigationView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getHeaderView(0).findViewById(R.id.tv_email).setOnClickListener(v -> presenter.sendEmail(((TextView) v).getText().toString()));

        navigationView.bringToFront();
        navigationView.setCheckedItem(R.id.monday_button);
    }

    private void initSwitchButton() {
        ImageView switchScheduleButton = findViewById(R.id.switch_schedule_button);

        int type = GatiPreferences.getTypeSchedule(this);
        if (type == Presenter.FULL_SCHEDULE) {
            tvTypeForm.setText(R.string.daytime);
        } else {
            tvTypeForm.setText(R.string.correspondence_time);
        }

        switchScheduleButton.setOnClickListener(new View.OnClickListener() {
            int previewType = -1;

            @Override
            public void onClick(View v) {
                RotateAnimation rotateAnimation = new RotateAnimation(0, 180, v.getWidth() / 2f, v.getHeight() / 2f);
                rotateAnimation.setDuration(ANIMATION_TIME);
                v.startAnimation(rotateAnimation);

                if (previewType == Presenter.CORRESPONDENCE_SCHEDULE) {
                    presenter.changeSchedule(Presenter.FULL_SCHEDULE);
                    previewType = Presenter.FULL_SCHEDULE;
                    tvTypeForm.setText(R.string.daytime);
                } else {
                    presenter.changeSchedule(Presenter.CORRESPONDENCE_SCHEDULE);
                    previewType = Presenter.CORRESPONDENCE_SCHEDULE;
                    tvTypeForm.setText(R.string.correspondence_time);
                }
            }
        });
    }

    public void onViewLongClick(View view, int position, float x, float y) {
        indexLastImageClicked = position;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view.showContextMenu(x, y);
        } else {
            view.showContextMenu();
        }
    }
}
