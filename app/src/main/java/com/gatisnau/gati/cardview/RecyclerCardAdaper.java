package com.gatisnau.gati.cardview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gatisnau.gati.R;

public class RecyclerCardAdaper extends RecyclerView.Adapter<CardHolder> {

    private Context context;
    private Presenter presenter;

    public RecyclerCardAdaper(@NonNull Context context, Presenter presenter) {
        this.context = context;
        this.presenter = presenter;
    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View cardItem = LayoutInflater.from(context).inflate(R.layout.card_item, viewGroup, false);

        return new CardHolder(cardItem);
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder cardHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
