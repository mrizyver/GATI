package com.izyver.gati.old.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class StackFragment {
    private FragmentManager fragmentManager;
    private int fragmentContainer;
    private int animationEnd = -1;
    private int animationStart = -1;

    public StackFragment(FragmentManager fragmentManager, int defaultFragmentContainer) {
        this.fragmentManager = fragmentManager;
        this.fragmentContainer = defaultFragmentContainer;
    }

    public void replaceFragment(Fragment fragment, String tag, boolean isAnimation){
        if (fragmentManager.isStateSaved()) return;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (isAnimation && animationStart != -1 && animationEnd != -1){
            transaction.setCustomAnimations(animationStart, animationEnd);
        }
        transaction
                .replace(fragmentContainer, fragment, tag)
                .commit();
    }

    public void addFragment(Fragment fragment){
        if (fragmentManager.isStateSaved()) return;
        fragmentManager.beginTransaction()
                .add(fragmentContainer, fragment)
                .commit();
    }

    public void addToStackFragment(Fragment fragment) {
        if (fragmentManager.isStateSaved()) return;
        fragmentManager.beginTransaction()
                .replace(fragmentContainer, fragment)
                .addToBackStack(fragment.getTag())
                .commit();
    }

    public void setAnimation(int idStart, int idEnd){
        animationEnd = idEnd;
        animationStart = idStart;
    }
}
