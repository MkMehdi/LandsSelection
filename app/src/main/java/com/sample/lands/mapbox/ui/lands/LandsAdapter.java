package com.sample.lands.mapbox.ui.lands;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sample.lands.R;
import com.sample.lands.mapbox.db.model.Land;

import java.util.List;

public class LandsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Land> mLandList;
    OnClickListener mOnClickListener;

    public LandsAdapter(List<Land> landList,OnClickListener onClickListener){
        this.mLandList=landList;
        this.mOnClickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemeView= LayoutInflater.from(parent.getContext()).inflate(R.layout.costum_land,parent,false);
        RecyclerView.ViewHolder holder=new Holder(itemeView);
        holder.setIsRecyclable(false);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Holder classHolder = (Holder) holder;
        try {
            classHolder.textNumber.setText("Number : " + mLandList.get(position).getId());
            String initLocation = "("+ mLandList.get(position).getPoints().get(0).getLatitude()+
                    ":" + mLandList.get(position).getPoints().get(0).getLongitude() + ")";
            classHolder.textLocation.setText(initLocation);

            classHolder.itemView.setOnClickListener(view -> {
                mOnClickListener.onClick(mLandList.get(position));
            });
        }catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return mLandList.size();
    }


    class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textNumber;
        TextView textLocation;


        public Holder(View itemView) {
            super(itemView);
            textNumber = itemView.findViewById(R.id.textLandNumber);
            textLocation = itemView.findViewById(R.id.textLocation);

        }

        @Override
        public void onClick(View v) {
        }
    }

    interface OnClickListener { void onClick(Land land);}
}