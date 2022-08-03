package com.example.monitor;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {


    private ArrayList<IndividualArray> names = new ArrayList<>();
    private Context mContext;

    public RecycleViewAdapter( Context mContext ,ArrayList<IndividualArray> names) {
        this.names = names;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_cell, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("FROM ON BIND VIEW HOLDER", "on bindCalled : VIEW :)");
        holder.showText.setText(names.get(position).getInitName());
        holder.showDescrition.setText(names.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView showText;
        TextView showDescrition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Typeface customfont = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/TitilliumWeb-Regular.ttf");

            showText = itemView.findViewById(R.id.recycleText);
            showText.setTypeface(customfont);
            showDescrition = itemView.findViewById(R.id.recycleDescription);
            showDescrition.setTypeface(customfont);
        }
    }
}
