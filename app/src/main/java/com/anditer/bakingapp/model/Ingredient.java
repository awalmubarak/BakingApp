package com.anditer.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Recipe ingredient
 */

public class Ingredient implements Parcelable{
    private String name;
    private String measure;
    private int quantity;

    public Ingredient(String name, String measure, int quantity) {
        this.name = name;
        this.measure = measure;
        this.quantity = quantity;
    }


    private Ingredient(Parcel in) {
        name = in.readString();
        measure = in.readString();
        quantity = in.readInt();
    }


    public String getName() {
        return name;
    }

    public String getMeasure() {
        return measure;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(measure);
        parcel.writeInt(quantity);
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
}
