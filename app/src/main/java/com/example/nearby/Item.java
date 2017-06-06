package com.example.nearby;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Professor on 4/13/2017.
 */
public class Item implements Parcelable{

    private String photo;
    private String name;
    private String address;
    private int price;
    private String isOpen;
    private double rating;
    private double latitude;
    private double longitude;
    private String id;
    private String type;

    public Item(String photo, String name, String address, int price, String isOpen, double rating, double latitude,
                double longitude, String id, String type) {
        this.photo = photo;
        this.name = name;
        this.address = address;
        this.price = price;
        this.isOpen = isOpen;
        this.rating = rating;
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
        this.type=type;
    }
    public String getType(){return type;}

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getId() {
        return id;
    }

    public String getPhoto() {
        return photo;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getPrice() {
        return price;
    }

    public String isOpen() {
        return isOpen;
    }

    public double getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return "photo : "+photo + "\n name : "+name+"\n address : "+address+"\n price : "+price+"\n open : "+isOpen
                + "\n rating : "+rating + "\n latitude : "+latitude + "\n longitude : "+longitude +
                 "\n id : "+id + "\n type : " + type;
    }

    private Item(Parcel in){
        photo = in.readString();
        name = in.readString();
        address = in.readString();
        price = in.readInt();
        isOpen = in.readString();
        rating = in.readDouble();
        latitude = in.readDouble();
        longitude = in.readDouble();
        id = in.readString();
        type = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(photo);
        parcel.writeString(name);
        parcel.writeString(address);
        parcel.writeInt(price);
        parcel.writeString(isOpen);
        parcel.writeDouble(rating);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(id);
        parcel.writeString(type);
    }
    public final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel parcel) {
            return new Item(parcel);
        }
        @Override
        public Item[] newArray(int i) {
            return new Item[i];
        }
    };
}
