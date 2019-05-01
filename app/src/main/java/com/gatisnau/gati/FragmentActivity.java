package com.gatisnau.gati;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gatisnau.gati.cardview.RecyclerCardAdapter;
import com.gatisnau.gati.model.ApplicationData;

public class FragmentActivity extends AppCompatActivity {

    private Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        presenter = ApplicationData.presenter;
        presenter.setFragmentManager(getSupportFragmentManager());
        getLifecycle().addObserver(presenter.new ActivityLifecycleListener());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);

        initSwitchButton();
    }


    /* ----------interface---------- */

    public void attachRecyclerAdapter(RecyclerCardAdapter adapter) {
        presenter.attachRecyclerAdapter(adapter);
    }


    /* ----------internal logic---------- */

    private void initSwitchButton() {
        Switch switchScheduleButton = findViewById(R.id.switch_schedule_button);

        boolean isCheckedSwitch = Presenter.FULL_SCHEDULE == GatiPreferences.getTypeSchedule(this);
        switchScheduleButton.setChecked(isCheckedSwitch);

        switchScheduleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            GatiPreferences.setSwitchScheduleButtonState(this, isChecked);
            if (isChecked){
                presenter.changeSchedule(Presenter.FULL_SCHEDULE);
            }else {
                presenter.changeSchedule(Presenter.CORRESPONDENCE_SCHEDULE);
            }
        });
    }

}
