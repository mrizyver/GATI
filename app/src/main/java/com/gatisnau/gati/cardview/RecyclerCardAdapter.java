package com.gatisnau.gati.cardview;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gatisnau.gati.OnImageClickListener;
import com.gatisnau.gati.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerCardAdapter extends RecyclerView.Adapter<CardHolder> {

    private Context context;
    private ArrayList<Bitmap> schedulers;
    private OnImageClickListener imageClickListener;

    public RecyclerCardAdapter(@NonNull Context context) {
        this.context = context;
        schedulers = new ArrayList<>(6);
    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View cardItem = LayoutInflater.from(context).inflate(R.layout.card_item, viewGroup, false);
        return new CardHolder(cardItem);
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder cardHolder, int i) {
        cardHolder.bind(schedulers.get(i), context, imageClickListener);
    }

    @Override
    public int getItemCount() {
        return schedulers == null ? 0 : schedulers.size();
    }

    public void setSchedulers(ArrayList<Bitmap> schedulers) {
        this.schedulers = schedulers;
        notifyDataSetChanged();
    }

    public void setImageClickListener(OnImageClickListener imageClickListener) {
        this.imageClickListener = imageClickListener;
    }
}
