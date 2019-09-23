package com.izyver.gati.old.transitions;

import android.annotation.TargetApi;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class DetailScheduleTransition extends TransitionSet {

    public DetailScheduleTransition() {
        init();
    }

    private void init() {
        setOrdering(ORDERING_TOGETHER);
        addTransition(new ChangeBounds());
        addTransition(new ChangeTransform());
        addTransition(new ChangeImageTransform());
    }
}
