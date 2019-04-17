package com.gatisnau.gati;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class StackFragment {
    private FragmentManager fragmentManager;
    private int fragmentContainer;

    public StackFragment(FragmentManager fragmentManager, int defaultFragmentContainer) {
        this.fragmentManager = fragmentManager;
        this.fragmentContainer = defaultFragmentContainer;
    }

    public void addFragment(Fragment fragment){
        fragmentManager.beginTransaction()
                .add(fragmentContainer, fragment)
                .commit();
    }

    public void addToStackFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(fragmentContainer, fragment)
                .addToBackStack(fragment.getTag())
                .commit();
    }
}
