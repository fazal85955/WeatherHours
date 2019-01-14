package com.apps.sfrcreativity.weatherhours.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.apps.sfrcreativity.weatherhours.R;

import java.util.ArrayList;
import java.util.List;

import static com.apps.sfrcreativity.weatherhours.adapter.ReportRecyclerViewAdapter.RVModel.DAY_TYPE;
import static com.apps.sfrcreativity.weatherhours.adapter.ReportRecyclerViewAdapter.RVModel.TIME_TYPE;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.MyViewHolder> {

    private ArrayList<String> dataSet;
    private CityListInteractionListener cityListInteractionListener;

    public interface CityListInteractionListener {
        void onClickListener(String name, int pos);
        void onDeleteClickListener(String name, int pos);
    }

    public CityAdapter(ArrayList<String> dataSet, CityListInteractionListener cityListInteractionListener) {
        this.dataSet = dataSet;
        this.cityListInteractionListener = cityListInteractionListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        ImageButton deleteButton;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewName =  itemView.findViewById(R.id.textViewName);
            this.deleteButton = itemView.findViewById(R.id.delete_action);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cityListInteractionListener.onClickListener(dataSet.get(getAdapterPosition()),getAdapterPosition());
                }
            });
            this.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cityListInteractionListener.onDeleteClickListener(dataSet.get(getAdapterPosition()),getAdapterPosition());
                }
            });
        }
    }

    public void removeItem(int pos) {
        dataSet.remove(pos);
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_city, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int listPosition) {
        TextView textViewName = holder.textViewName;
        textViewName.setText(dataSet.get(listPosition));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}