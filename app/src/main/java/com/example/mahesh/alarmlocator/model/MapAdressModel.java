package com.example.mahesh.alarmlocator.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SIS312 on 8/25/2017.
 */

public class MapAdressModel implements Parcelable {
    private String latitude;

    public MapAdressModel(String latitude, String longitude, String zip, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        Zip = zip;
        Address = address;
    }
    public MapAdressModel() {

    }
    protected MapAdressModel(Parcel in) {
        latitude = in.readString();
        longitude = in.readString();
        Zip = in.readString();
        Address = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(Zip);
        dest.writeString(Address);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MapAdressModel> CREATOR = new Creator<MapAdressModel>() {
        @Override
        public MapAdressModel createFromParcel(Parcel in) {
            return new MapAdressModel(in);
        }

        @Override
        public MapAdressModel[] newArray(int size) {
            return new MapAdressModel[size];
        }
    };

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getZip() {
        return Zip;
    }

    public void setZip(String zip) {
        Zip = zip;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    private String longitude;
    private String Zip;
    private String Address;
}
