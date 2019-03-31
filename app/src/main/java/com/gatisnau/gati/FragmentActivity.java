package com.gatisnau.gati;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.gatisnau.gati.cardview.CardFragment;
import com.gatisnau.gati.cardview.RecyclerCardAdapter;

public class FragmentActivity extends AppCompatActivity {

    private Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        presenter = ApplicationData.presenter;
        getLifecycle().addObserver(presenter.new LifecycleListener());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, CardFragment.newInstance())
                .commit();
    }

    public void attachRecyclerAdapter(RecyclerCardAdapter adapter) {
        presenter.attachRecyclerAdapter(adapter);
    }
}
