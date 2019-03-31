package com.gatisnau.gati;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class FragmentActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);

    }
}
