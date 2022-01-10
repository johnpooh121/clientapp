package com.example.clientapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {

    private ArrayList<RankingItem> RankingList;

    @NonNull
    @Override
    public RankingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingAdapter.ViewHolder holder, int position) {
        holder.onBind(RankingList.get(position));
    }

    public  void setRankingList(ArrayList<RankingItem> list){
        this.RankingList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return RankingList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView ranking;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.image);
            name = (TextView) itemView.findViewById(R.id.name);
            ranking = (TextView) itemView.findViewById(R.id.ranking);
        }

        void onBind(RankingItem item){
            image.setImageResource(item.getImage());
            name.setText(item.getName());
            ranking.setText(item.getRanking());
        }
    }
}