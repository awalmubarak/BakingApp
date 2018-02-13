package com.anditer.bakingapp;

import com.anditer.bakingapp.model.Recipe;

import java.util.ArrayList;

/**
 * Returns dummy data
 */

public class GetData {

    public static ArrayList<Recipe> getRecipeArray() {
        ArrayList<Recipe> arrayList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Recipe recipe = new Recipe(i,"Recipe "+String.valueOf(i),i,"Image");
            arrayList.add(recipe);
        }

        return arrayList;
    }
}
