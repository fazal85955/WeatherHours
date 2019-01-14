package com.apps.sfrcreativity.weatherhours.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.apps.sfrcreativity.weatherhours.R;

import java.util.List;

import static com.apps.sfrcreativity.weatherhours.adapter.ReportRecyclerViewAdapter.RVModel.DAY_TYPE;
import static com.apps.sfrcreativity.weatherhours.adapter.ReportRecyclerViewAdapter.RVModel.TIME_TYPE;

public class ReportRecyclerViewAdapter extends RecyclerView.Adapter {

    private List<RVModel> dataSet;
    private int lastSelectedPosition = -1;
    private AdapterListener adapterListener;
    private Context ctx;

    public interface AdapterListener {
        void setOnClickListener(int position, RVModel rvModel);
    }

    public ReportRecyclerViewAdapter(List<RVModel>data, AdapterListener adapterListener) {
        this.dataSet = data;
        this.adapterListener = adapterListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        this.ctx = parent.getContext();
        if(dataSet != null) {
            switch (dataSet.get(0).getType()) {
                case DAY_TYPE:
                    view = LayoutInflater.from(ctx).inflate(R.layout.card_day_item, parent, false);
                    return new DayViewHolder(view);
                case TIME_TYPE:
                    view = LayoutInflater.from(ctx).inflate(R.layout.card_time_item, parent, false);
                    return new TimeViewHolder(view);
            }
        }
        return null;
    }



    @Override
    public int getItemViewType(int position) {
        return dataSet.get(position).getType();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int listPosition) {
        RVModel object = dataSet.get(listPosition);

        if (object != null) {
            switch (object.getType()) {
                case DAY_TYPE:
                    ((DayViewHolder)holder).dayTmp.setText(String.valueOf(object.getDayTmp()).concat(ctx.getResources().getString(R.string.signDegree)));
                    ((DayViewHolder)holder).date.setText(object.getDate());
                    ((DayViewHolder)holder).radioDay.setText(object.getDay());
                    ((DayViewHolder)holder).imgDes.setImageResource(object.getIcon());
                    //since only one radio button is allowed to be selected,
                    // this condition un-checks previous selections
                    ((DayViewHolder)holder).radioDay.setChecked(lastSelectedPosition==listPosition);
                    //((DayViewHolder) holder).txtType.setText(object.text);
                    break;
                case TIME_TYPE:
                    ((TimeViewHolder)holder).imgDes.setImageResource(object.getIcon());
                    ((TimeViewHolder)holder).dayTmp.setText(String.valueOf(object.getDayTmp()).concat(ctx.getResources().getString(R.string.signDegree)));
                    ((TimeViewHolder)holder).radioDay.setText(object.getDay());
                    //since only one radio button is allowed to be selected,
                    // this condition un-checks previous selections
                    ((TimeViewHolder)holder).radioDay.setChecked(lastSelectedPosition==listPosition);
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public int getLastSelected() {
        return lastSelectedPosition;
    }
    public void setSelected(int i) {
        lastSelectedPosition = i;
    }

    public class DayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imgDes;
        private TextView dayTmp, date;
        private RadioButton radioDay;

        public DayViewHolder(View view) {
            super(view);
            imgDes = view.findViewById(R.id.imgDes);
            dayTmp = view.findViewById(R.id.txtDayTmp);
            date = view.findViewById(R.id.txtDay);
            radioDay = view.findViewById(R.id.radioDay);
            radioDay.setOnClickListener(this);
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if(lastSelectedPosition!=getAdapterPosition()) {
                lastSelectedPosition = getAdapterPosition();
                radioDay.setChecked(lastSelectedPosition==getAdapterPosition());
                adapterListener.setOnClickListener(getAdapterPosition(), dataSet.get(getAdapterPosition()));
                notifyDataSetChanged();
            }
        }
    }

    public class TimeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imgDes;
        private TextView dayTmp;
        private RadioButton radioDay;

        public TimeViewHolder(View view) {
            super(view);
            imgDes = view.findViewById(R.id.imgDes);
            dayTmp = view.findViewById(R.id.txtTimeTmp);
            radioDay = view.findViewById(R.id.radioTime);
            radioDay.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(lastSelectedPosition!=getAdapterPosition()) {
                lastSelectedPosition = getAdapterPosition();
                radioDay.setChecked(lastSelectedPosition==getAdapterPosition());
                adapterListener.setOnClickListener(getAdapterPosition(), dataSet.get(getAdapterPosition()));
                notifyDataSetChanged();
            }
        }
    }

    public static class RVModel {

        public static final int TIME_TYPE=0;
        public static final int DAY_TYPE=1;

        private int dayTmp;
        private String day;
        private int icon;
        private String date;
        private int type;


        public RVModel(int tmp, int icon, String day) {
            this.icon = icon;
            this.dayTmp = tmp;
            this.day = day;
            this.type = TIME_TYPE;
        }

        public RVModel(int tmp, int icon, String day, String date) {
            this.icon = icon;
            this.dayTmp = tmp;
            this.day = day;
            this.date = date;
            this.type = DAY_TYPE;
        }

        public int getIcon() {
            return icon;
        }

        public int getType() {
            return type;
        }

        public String getDate() {
            return date;
        }

        public int getDayTmp() {
            return dayTmp;
        }

        public String getDay() {
            return day;
        }

    }
}