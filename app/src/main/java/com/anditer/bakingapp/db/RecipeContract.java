package com.anditer.bakingapp.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Our Recipe Contract
 */

public class RecipeContract {

    public  static final String AUTHORITY = "com.anditer.bakingapp";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://"+ AUTHORITY);
    public  static final String PATH_RECIPES = "recipes";
    public  static final String PATH_ADD_RECIPES = "add";
    public  static final String PATH_DELETE_RECIPE = "delete";
    private RecipeContract(){

    }

    public static class RecipeEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RECIPES).build();

        public static final Uri ADD_RECIPE_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_ADD_RECIPES).build();

        public static final Uri DELETE_RECIPE_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_DELETE_RECIPE).build();

        public static final String TABLE_NAME = "recipes";
        public static final String RECIPE_NAME = "name";
        public static final String RECIPE_ID = "id";
        public static final String RECIPE_SERVINGS = "servings";
        public static final String RECIPE_IMAGE = "image";
        public static final String RECIPE_STEPS_JSON = "steps";
        public static final String RECIPE_INGREDIENTS_JSON = "ingredients";
        public static final String COLUMN_TIME_STAMP = "timestamp";

    }
}
