package com.gatisnau.gati.view.cardview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.gatisnau.gati.listeners.OnImageClickListener;
import com.gatisnau.gati.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerCardAdapter extends RecyclerView.Adapter<CardHolder> {

    private Context context;
    private ArrayList<Bitmap> schedulers;
    private OnImageClickListener imageClickListener;
    private RecyclerView recyclerView;
    private Runnable postRunnable;

    private boolean isTouched;
    private int position = -1;

    public RecyclerCardAdapter(@NonNull Context context) {
        this.context = context;
        schedulers = new ArrayList<>(6);
    }


    /* ----------interface---------- */

    public void setSchedulers(ArrayList<Bitmap> schedulers) {
        this.schedulers = schedulers;
        notifyDataSetChanged();
    }

    public void setImageClickListener(OnImageClickListener imageClickListener) {
        this.imageClickListener = imageClickListener;
    }

    public void toPosition(int position){
        this.position = position;
        if (recyclerView != null){
            setPosition(position);
        }else {
            postRunnable = () -> setPosition(position);
        }
    }


    /* ----------super---------- */

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

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;

        recyclerView.setOnTouchListener((v, event) -> {
            isTouched = true;
            recyclerView.setOnTouchListener(null);
            return false;
        });

        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
        if (isTouched || position == -1) return;
        setPosition(position);
    });

        if (postRunnable != null){
            postRunnable.run();
        }
    }


    /* ----------internal logic---------- */

    private void setPosition(int position){
        if (position < 0){
            recyclerView.scrollToPosition(0);
            return;
        }else if (getItemCount() < position){
            recyclerView.scrollToPosition(getItemCount());
            return;
        }
        recyclerView.scrollToPosition(position);
    }
}
