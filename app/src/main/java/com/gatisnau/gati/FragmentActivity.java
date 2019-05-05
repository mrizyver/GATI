package com.gatisnau.gati;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.gatisnau.gati.cardview.RecyclerCardAdapter;
import com.gatisnau.gati.model.ApplicationData;
import com.google.android.material.navigation.NavigationView;

public class FragmentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        presenter = ApplicationData.presenter;
        presenter.setFragmentManager(getSupportFragmentManager());
        getLifecycle().addObserver(presenter.new ActivityLifecycleListener());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);

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
        navigationView.bringToFront();
        navigationView.setCheckedItem(R.id.monday_button);
    }

    private void initSwitchButton() {
        Switch switchScheduleButton = findViewById(R.id.switch_schedule_button);

        boolean isCheckedSwitch = Presenter.FULL_SCHEDULE == GatiPreferences.getTypeSchedule(this);
        switchScheduleButton.setChecked(isCheckedSwitch);

        switchScheduleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                presenter.changeSchedule(Presenter.FULL_SCHEDULE);
            } else {
                presenter.changeSchedule(Presenter.CORRESPONDENCE_SCHEDULE);
            }
        });
    }
}
