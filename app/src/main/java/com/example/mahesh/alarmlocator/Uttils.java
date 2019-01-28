package com.example.mahesh.alarmlocator;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mahesh.alarmlocator.model.Alarmmodel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by kanhaiya on 2/16/2018.
 */

public class Uttils {

    public static void saveAlarmModel(Context context, Alarmmodel alarmModel) {
        SharedPreferences shared = context.getSharedPreferences(Constants.MYPREFS,
                Context.MODE_PRIVATE);
        Gson gson = new Gson();
        ArrayList<Alarmmodel> alarmlist = new ArrayList<>();
        alarmlist = getAddressList(context);
            if (alarmlist!=null&&alarmlist.size() > 0) {
                int index = alarmlist.indexOf(alarmModel);
                if (index != -1) {
                    alarmlist.remove(index);
                    alarmlist.add(alarmModel);
                } else {
                    alarmModel.setId("" + alarmlist.size() + 1);
                    alarmlist.add(alarmModel);
                }
            } else {
                alarmlist=new ArrayList<>();
                alarmModel.setId("" + alarmlist.size() + 1);
                alarmlist.add(alarmModel);
            }
        shared.edit().putString(Constants.AddressModels, gson.toJson(alarmlist)).commit();

    }

    public static ArrayList<Alarmmodel> getAddressList(Context context) {
        ArrayList<Alarmmodel> addressModelArrayList = new ArrayList<>();
        try {
            SharedPreferences shared = context.getSharedPreferences(Constants.MYPREFS, Context.MODE_PRIVATE);
            String json = shared.getString(Constants.AddressModels, null);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Alarmmodel>>() {
            }.getType();
            addressModelArrayList = gson.fromJson(json, type);
            return addressModelArrayList;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return addressModelArrayList;
    }

    public static void deleteAlarm(Context context, Alarmmodel alarmModel) {
        SharedPreferences shared = context.getSharedPreferences(Constants.MYPREFS,
                Context.MODE_PRIVATE);
        Gson gson = new Gson();
        ArrayList<Alarmmodel> alarmlist = new ArrayList<>();
        alarmlist = getAddressList(context);
        if (alarmlist!=null&&alarmlist.size() > 0) {
          alarmlist.remove(alarmModel);
        }
        shared.edit().putString(Constants.AddressModels, gson.toJson(alarmlist)).commit();

    }
}
