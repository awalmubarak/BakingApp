package com.anditer.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Single Recipe Item
 */

public class Recipe implements Parcelable{
    private int id;
    private String name;
    private ArrayList<Ingredient> ingredients = new ArrayList<>();
    private ArrayList<Step> steps = new ArrayList<>();
    private int servings;
    private String image;
    private String ingredientsJsonString;
    private String stepsJsonString;

    public Recipe(){

    }

    public Recipe(int id, String name, int servings, String image) {
        this.id = id;
        this.name = name;
        this.servings = servings;
        this.image = image;
    }

    private Recipe(Parcel in) {
        id = in.readInt();
        name = in.readString();
        servings = in.readInt();
        image = in.readString();
        ingredientsJsonString = in.readString();
        stepsJsonString = in.readString();
        in.readTypedList(getIngredients(), Ingredient.CREATOR);
        in.readTypedList(getSteps(), Step.CREATOR);
    }


    public String getIngredientsJsonString() {
        return ingredientsJsonString;
    }

    public void setIngredientsJsonString(String ingredientsJsonString) {
        this.ingredientsJsonString = ingredientsJsonString;
    }

    public String getStepsJsonString() {
        return stepsJsonString;
    }

    public void setStepsJsonString(String stepsJsonString) {
        this.stepsJsonString = stepsJsonString;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients= ingredients;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public int getServings() {
        return servings;
    }

    public String getImage() {
        return image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeInt(servings);
        parcel.writeString(image);
        parcel.writeString(ingredientsJsonString);
        parcel.writeString(stepsJsonString);
        parcel.writeTypedList(ingredients);
        parcel.writeTypedList(steps);
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

}
