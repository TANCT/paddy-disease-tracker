package com.example.paddy_disease_tracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    Context context;
    ArrayList<MainModel> mainModel;
    private RecyclerViewClickListener listener;

    public MainAdapter(Context context, ArrayList<MainModel> mainModel, RecyclerViewClickListener listener) {
        this.context = context;
        this.mainModel = mainModel;
        this.listener=listener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.rowitem,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageview.setImageResource(mainModel.get(position).getDiseaseLogo());
        holder.textView.setText(mainModel.get(position).getDiseaseName());
    }

    @Override
    public int getItemCount() {
        return mainModel.size();
    }

    public interface RecyclerViewClickListener{

        void onClick(View v, int position);

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageview;
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageview=itemView.findViewById(R.id.imageview);
            textView=itemView.findViewById(R.id.txt);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            listener.onClick(view,getAdapterPosition());
        }
    }
}

