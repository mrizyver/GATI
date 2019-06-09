package com.izyver.gati.view;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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

import com.google.android.material.navigation.NavigationView;
import com.izyver.gati.R;
import com.izyver.gati.presenter.PresenterActivity;
import com.izyver.gati.utils.GatiPreferences;
import com.izyver.gati.utils.StackFragment;
import com.izyver.gati.view.cardview.CardFragment;

import static com.izyver.gati.model.ApplicationData.CORRESPONDENCE_SCHEDULE;
import static com.izyver.gati.model.ApplicationData.FULL_SCHEDULE;

public class FragmentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private PresenterActivity presenter;

    private Toolbar toolbar;
    private TextView tvForm;
    private TextView tvTypeForm;
    private NavigationView navigationView;

    private StackFragment stackFragment;

    private int ANIMATION_TIME = 250;

    public void hideToolbar() {
        AlphaAnimation fade = createFade(1, 0, () -> toolbar.setVisibility(View.GONE));
        toolbar.setAnimation(fade);
    }

    public void showToolbar() {
        toolbar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);

        presenter = new PresenterActivity();
        stackFragment = new StackFragment(getSupportFragmentManager(), R.id.fragment_container);

        int type = GatiPreferences.getTypeSchedule(this);
        stackFragment.addFragment(CardFragment.newInstance(type));

        presenter.attachActivity(this);
        tvForm = findViewById(R.id.tv_word_form);
        tvTypeForm = findViewById(R.id.tv_form_study);
        toolbar = findViewById(R.id.toolbar);

        initNavigationView();
        initSwitchButton(type);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        navigationView.setCheckedItem(id);

        if (id == R.id.monday_button) {
//            presenter.setItem(0);
//        } else if (id == R.id.tuesday_button) {
//            presenter.setItem(1);
//        } else if (id == R.id.wednesday_button) {
//            presenter.setItem(2);
//        } else if (id == R.id.thursday_button) {
//            presenter.setItem(3);
//        } else if (id == R.id.friday_button) {
//            presenter.setItem(4);
//        } else if (id == R.id.saturday_button) {
//            presenter.setItem(5);
        } else if (id == R.id.inst_button) {
            startActivity("com.instagram.android", "https://www.instagram.com/gati_snau.official/");
        } else if (id == R.id.youtube_button) {
            startActivity("com.youtube.android", "https://www.youtube.com/channel/UC0Ccnd5F7fTyQ60C3LeyhFw");
        } else if (id == R.id.facebook_button) {
            startActivity("com.facebook.katana", "https://www.facebook.com/official.gatisnau/");
        } else if (id == R.id.site_button) {
            startActivity("com.android.chrome/com.android.chrome.Main", "http://gatisnau.sumy.ua/");
        } else if (id == R.id.update_app) {
            presenter.updateApp();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    public StackFragment getStackFragment() {
        return stackFragment;
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
    protected void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }

    /* ----------internal logic---------- */

    private void initNavigationView() {
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

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getHeaderView(0).findViewById(R.id.header_text).setOnClickListener(v -> sendEmail(((TextView) v).getText().toString()));
        navigationView.bringToFront();
        navigationView.setCheckedItem(R.id.monday_button);
    }

    private void initSwitchButton(final int type) {
        ImageView switchScheduleButton = findViewById(R.id.switch_schedule_button);

        if (type == FULL_SCHEDULE) {
            tvTypeForm.setText(R.string.daytime);
        } else {
            tvTypeForm.setText(R.string.correspondence_time);
        }

        switchScheduleButton.setOnClickListener(new View.OnClickListener() {
            int previewType = type;

            @Override
            public void onClick(View v) {
                RotateAnimation rotateAnimation = new RotateAnimation(0, 180, v.getWidth() / 2f, v.getHeight() / 2f);
                rotateAnimation.setDuration(ANIMATION_TIME);
                v.startAnimation(rotateAnimation);

                if (previewType == CORRESPONDENCE_SCHEDULE) {
                    changeSchedule(FULL_SCHEDULE, FragmentActivity.this);
                    previewType = FULL_SCHEDULE;
                    tvTypeForm.setText(R.string.daytime);
                } else if (previewType == FULL_SCHEDULE) {
                    changeSchedule(CORRESPONDENCE_SCHEDULE, FragmentActivity.this);
                    previewType = CORRESPONDENCE_SCHEDULE;
                    tvTypeForm.setText(R.string.correspondence_time);
                }
            }
        });
    }

    private void changeSchedule(int type, Context context) {
        GatiPreferences.setTypeSchedule(context, type);
        if (type == FULL_SCHEDULE) {
            stackFragment.setAnimation(R.animator.slide_in_right_start, R.animator.slide_in_right_end);
        } else {
            stackFragment.setAnimation(R.animator.slide_in_left_start, R.animator.slide_in_left_end);
        }
        stackFragment.replaceFragment(CardFragment.newInstance(type), CardFragment.TAG, true);
    }


    private void startActivity(String packageActivity, String uri) {
        Uri link = Uri.parse(uri);
        Intent activityIntent = new Intent(Intent.ACTION_VIEW, link);
        activityIntent.setPackage(packageActivity);
        try {
            startActivity(activityIntent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, link));
        }
    }

    private void sendEmail(String email) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));

        startActivity(Intent.createChooser(intent, getString(R.string.send_email)));
    }

    private AlphaAnimation createFade(float from, float to, Runnable endAction) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(from, to);
        alphaAnimation.setDuration(500);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                endAction.run();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return alphaAnimation;
    }
}
