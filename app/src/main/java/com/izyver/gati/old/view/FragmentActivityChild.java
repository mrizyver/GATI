package com.izyver.gati.old.view;

import androidx.annotation.IntRange;

public interface FragmentActivityChild {
    void setItem(@IntRange(from = 0, to = 5) int index);
}
