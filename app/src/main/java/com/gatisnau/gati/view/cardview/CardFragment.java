package com.gatisnau.gati.view.cardview;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gatisnau.gati.view.FragmentActivity;
import com.gatisnau.gati.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public final class CardFragment extends Fragment {

    public static CardFragment newInstance() {

        Bundle args = new Bundle();

        CardFragment fragment = new CardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.card_fragment, container, false);

        Context context = getContext();
        if (context == null) return view;

        RecyclerCardAdapter adapter = new RecyclerCardAdapter(context);
        RecyclerView recyclerCardList = view.findViewById(R.id.recycler_card);
        recyclerCardList.setLayoutManager(new LinearLayoutManager(context));
        recyclerCardList.setAdapter(adapter);

        if (!(getActivity() instanceof FragmentActivity)) return view;
        ((FragmentActivity) getActivity()).attachRecyclerAdapter(adapter);

        return view;
    }
}
