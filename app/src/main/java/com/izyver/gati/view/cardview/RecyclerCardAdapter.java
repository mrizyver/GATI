package com.izyver.gati.view.cardview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.izyver.gati.listeners.OnImageClickListener;
import com.izyver.gati.R;
import com.izyver.gati.listeners.OnImageLongClickListener;
import com.izyver.gati.model.entity.CardImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerCardAdapter extends RecyclerView.Adapter<CardHolder> {

    private Context context;
    private ArrayList<CardImage> schedulers;
    private OnImageClickListener imageClickListener;
    private OnImageLongClickListener imageLongClickListener;
    private RecyclerView recyclerView;
    private Runnable postRunnable;

    private boolean isTouched;
    private int position = -1;

    public RecyclerCardAdapter(@NonNull Context context) {
        this.context = context;
        schedulers = new ArrayList<>(Arrays.asList(null,null,null,null,null));
    }


    /* ----------interface---------- */

    public void updateItem(Bitmap bitmap, boolean isOld, int index) {
        if (getItemCount() <= index){
            schedulers.add(index, new CardImage(bitmap, isOld));
        }
        schedulers.set(index, new CardImage(bitmap, isOld));
        notifyItemChanged(index);
    }

    public void setImageClickListener(OnImageClickListener imageClickListener) {
        this.imageClickListener = imageClickListener;
    }

    public void setImageLongClickListener(OnImageLongClickListener imageLongClickListener) {
        this.imageLongClickListener = imageLongClickListener;
    }

    public void toPosition(int position){
        this.position = position;
        if (recyclerView != null){
            setPosition(position);
        }else {
            postRunnable = () -> setPosition(position);
        }
    }

    public void setVisibilitySaturday(boolean isExistSaturday) {
        if (isExistSaturday){
            if (schedulers.size() == 5){
                schedulers.add(new CardImage(null, false));
            }
        }else {
            if ((schedulers.size() == 6)){
                schedulers.remove(5);
            }
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
        cardHolder.bind(schedulers.get(i),  context, imageClickListener, imageLongClickListener);
    }

    @Override
    public int getItemCount() {
        return schedulers == null ? 0 : schedulers.size() == 0 ? 5 : schedulers.size();
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
