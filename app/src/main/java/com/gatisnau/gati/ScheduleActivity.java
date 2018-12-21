package com.gatisnau.gati;

import android.Manifest;
import android.content.ActivityNotFoundException;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static com.gatisnau.gati.Schedule.*;

public class ScheduleActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String STATE_COUNT_PAGE = "count_page";
    private final String STATE_THREAD_WORKED = "update_check";
    private final String STATE_SATURDAY_IS_VISIBLE = "saturday_visible";

    private static ProgressBar progressBar;
    private static PagerAdapter pagerAdapter;
    private NavigationView navigationView;
    private ViewPagerFixed viewPager;
    private ImageManager imageManager;
    private boolean saturdayIsVisible;
    public static int countPage;
    public static final int PERMISSION_REQUEST_CODE = 1;
    private static TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        countPage = savedInstanceState==null ?
                5 : savedInstanceState.getInt(STATE_COUNT_PAGE);
        saturdayIsVisible = savedInstanceState==null ?
                false : savedInstanceState.getBoolean(STATE_SATURDAY_IS_VISIBLE);

        title = (TextView) findViewById(R.id.toolbar_title);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        setVisibleProgressBar(View.GONE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageManager = new ImageManager(getApplicationContext());

        viewPager = (ViewPagerFixed) findViewById(R.id.view_pager);
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPagerFixed.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {}
            @Override
            public void onPageSelected(int position) {invalidateOptionsMenu();}
            @Override
            public void onPageScrollStateChanged(int i) {}
        });
        viewPager.setCurrentItem(imageManager.smartGetDay() - 2);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState!= null && savedInstanceState.getBoolean(
                STATE_THREAD_WORKED, true)){
            for (int i = 0; i<6; i++){
                imageManager.startDownloadImage(i+2);
            }
            UpdateApp.checkUpdate(
                    URL_VERSION_CONTROL, ScheduleActivity.this);
            new Thread(()->setVisibleSaturdayItem()).start();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(STATE_COUNT_PAGE, countPage);
        savedInstanceState.putBoolean(STATE_THREAD_WORKED, false);
        savedInstanceState.putBoolean(STATE_SATURDAY_IS_VISIBLE, saturdayIsVisible);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if (saturdayIsVisible)
            navigationView.getMenu()
                    .findItem(R.id.saturday_button)
                    .setVisible(saturdayIsVisible);
        int position = viewPager.getCurrentItem();
        navigationView.getMenu().getItem(position).setChecked(true);

        if (position==0){
            setTitle(R.string.monday);
        } else if (position==1){
            setTitle(R.string.tuesday);
        }else if (position==2){
            setTitle(R.string.wednesday);
        }else if (position==3){
            setTitle(R.string.thursday);
        }else if (position==4){
            setTitle(R.string.friday);
        }else if (position==5){
            setTitle(R.string.saturday);
        }
         return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.schedule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.update_button){
            Perms perms = new Perms();
            if (perms.hasPermissions(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                    perms.hasPermissions(READ_EXTERNAL_STORAGE)){

                new UpdateApp(ScheduleActivity.this)
                        .execute(URL_TO_NEW_APP, URL_VERSION_CONTROL);
            }else {
                perms.requestPerm(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE);
            }

        }else if (id == R.id.update_image) {
            new ImageManager(ScheduleActivity.this)
                    .startDownloadImage(
                            viewPager.getCurrentItem()+2,
                            true);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.monday_button) {
            viewPager.setCurrentItem(0);

        } else if (id == R.id.tuesday_button) {
            viewPager.setCurrentItem(1);

        } else if (id == R.id.wednesday_button) {
            viewPager.setCurrentItem(2);

        } else if (id == R.id.thursday_button) {
            viewPager.setCurrentItem(3);

        } else if (id == R.id.friday_button) {
            viewPager.setCurrentItem(4);

        } else if (id == R.id.saturday_button) {
            viewPager.setCurrentItem(5);

        }else if (id == R.id.inst_button  ){
            startOtherActivity("com.instagram.android", "https://www.instagram.com/gati_snau.official/");
        }else if (id == R.id.youtube_button){
            startOtherActivity("com.youtube.android", "https://www.youtube.com/channel/UC0Ccnd5F7fTyQ60C3LeyhFw");
        }else if (id == R.id.facebook_button){
            startOtherActivity("com.facebook.katana", "https://www.facebook.com/official.gatisnau/");
        }else if (id == R.id.site_button){
            startOtherActivity("com.android.chrome/com.android.chrome.Main", "http://gatisnau.sumy.ua/");
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setVisibleSaturdayItem(){
        try {
            URL url = new URL(Schedule.url[5])  ;
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode()==HttpURLConnection.HTTP_OK) {
                saturdayIsVisible = true;
                countPage = 6;
                try {
                    if (pagerAdapter!=null)
                        pagerAdapter.notifyDataSetChanged();
                    //НЕЛЬЗЯ ОБРАЩАТЬСЯ ИЗ АСИНХРОН МЕТОДА К ГПИ
                }catch (Exception e){//ЙОБАНА, ЧТО ЗА УБОЖЕСТВО???

                }
            }
            invalidateOptionsMenu();
        } catch (IOException e) {

        }
    }

    public static void setVisibleProgressBar(int visibleProgressBar){
        if(progressBar!=null)
            progressBar.setVisibility(visibleProgressBar);
    }

    public static void updateFragments(){pagerAdapter.notifyDataSetChanged();}

    public void startOtherActivity(String packageActivity, String uri){
        Uri link = Uri.parse(uri);
        Intent activityIntent = new Intent(Intent.ACTION_VIEW, link);
        activityIntent.setPackage(packageActivity);
        try{
            startActivity(activityIntent);
        }catch (ActivityNotFoundException e){
            startActivity(new Intent(Intent.ACTION_VIEW, link));
        }
    }

    public void setTitle(int id){
        title.setText(id);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE){
            new UpdateApp(ScheduleActivity.this)
                    .execute(URL_TO_NEW_APP, URL_VERSION_CONTROL);
        }
    }

    private class Perms{
        public boolean hasPermissions(String permission){
            int res = 0;
            res = PermissionChecker.checkCallingOrSelfPermission(ScheduleActivity.this, permission);
            if(res == PackageManager.PERMISSION_GRANTED){
                Log.i("Permissions: ", permission + " allowed");
                return true;
            } else {
                Log.i("Permissions: ", permission + " denied");
                return false;
            }
        }

        public void requestPerm(String... permission){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                ActivityCompat.requestPermissions(ScheduleActivity.this, permission, PERMISSION_REQUEST_CODE);
        }
    }


}
class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

        @Override
    public Fragment getItem(int i) {
        FragmentPageImage fragment = new FragmentPageImage();
        Bundle args = new Bundle();
        args.putInt(KEY_DAY_OF_WEEK, i+2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemPosition(Object object) {return POSITION_NONE;}

    @Override
    public int getCount() {
        return ScheduleActivity.countPage;
    }

}