package com.example.iotparkingsystem.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iotparkingsystem.Objects.Spot;
import com.example.iotparkingsystem.R;

import java.util.ArrayList;

public class SpotAdapter extends RecyclerView.Adapter<SpotAdapter.MyViewHolder> {

    private ArrayList<String> mStartData;
    private ArrayList<String> mEndData;

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView startTextView;
        TextView endTextView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            startTextView = itemView.findViewById(R.id.startTimeResTextView);
            endTextView = itemView.findViewById(R.id.endTimeResTextView);
        }
    }

    public SpotAdapter (ArrayList<String> startData,ArrayList<String> endData){
        mStartData = startData;
        mEndData = endData;
    }

    @NonNull
    @Override
    public SpotAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        android.view.View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reserve_list,parent,false);

        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SpotAdapter.MyViewHolder holder, int position) {
        holder.startTextView.setText(mStartData.get(position));
        holder.endTextView.setText(mEndData.get(position));
    }

    @Override
    public int getItemCount() {
        return mStartData.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

}
