package com.gatisnau.gati;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gatisnau.gati.cardview.RecyclerCardAdapter;

public class FragmentActivity extends AppCompatActivity {

    private Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        presenter = ApplicationData.presenter;
        presenter.setFragmentManager(getSupportFragmentManager());
        getLifecycle().addObserver(presenter.new ActivityLifecycleListener());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
    }

    public void attachRecyclerAdapter(RecyclerCardAdapter adapter) {
        presenter.attachRecyclerAdapter(adapter);
    }
}
