package com.example.mahesh.alarmlocator.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mahesh.alarmlocator.AlarmListActivity;
import com.example.mahesh.alarmlocator.R;
import com.example.mahesh.alarmlocator.model.Alarmmodel;

import java.util.ArrayList;

/**
 * Created by Mahesh on 03/02/2018.
 */

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.Item> {
    Context context;
    ArrayList<Alarmmodel> alarmlist = new ArrayList<>();
    private onItemClick onItemClick;

    public AlarmListAdapter(Activity activity, ArrayList<Alarmmodel> alarmlist,onItemClick onItemClick) {
        context = activity;
        this.alarmlist = alarmlist;
        this.onItemClick=onItemClick;
    }


    @Override
    public Item onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_alarm_item, parent, false);
        return new Item(view);
    }

    @Override
    public void onBindViewHolder(Item holder, int position) {
        final Alarmmodel alarmmodel = alarmlist.get(position);
        if (alarmmodel != null) {
            holder.address.setText(alarmmodel.getAddress());
            if(alarmmodel.isDisable()){
                holder.disable.setText("Enable");
                holder.update.setVisibility(View.GONE);
            }
            else {
                holder.disable.setText("Disable");
                holder.update.setVisibility(View.VISIBLE);
            }
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.onDelete(alarmmodel);
            }
        });
        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.onEdit(alarmmodel);
            }
        });
        holder.disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.onDisable(alarmmodel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarmlist.size();
    }

    public class Item extends RecyclerView.ViewHolder {
        TextView delete, update, disable, address;

        public Item(View itemView) {
            super(itemView);
            delete = itemView.findViewById(R.id.delete);
            update = itemView.findViewById(R.id.update);
            disable = itemView.findViewById(R.id.disable);
            address = itemView.findViewById(R.id.alarm_address);
        }
    }
    public interface onItemClick{
       void onDisable(Alarmmodel alarmmodel);
       void onDelete(Alarmmodel alarmmodel);
       void onEdit(Alarmmodel alarmmodel);
    }
}