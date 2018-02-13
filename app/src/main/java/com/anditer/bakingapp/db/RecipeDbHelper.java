package com.anditer.bakingapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Our Db helper class
 */

public class RecipeDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "RecipesDb";
    private static final int VERSION = 1;
    public RecipeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE_QUERY = "CREATE TABLE "+ RecipeContract.RecipeEntry.TABLE_NAME+
                " (" + RecipeContract.RecipeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                RecipeContract.RecipeEntry.RECIPE_ID + " INTEGER NOT NULL, "+
                RecipeContract.RecipeEntry.RECIPE_NAME + " VARCHAR NOT NULL, " +
                RecipeContract.RecipeEntry.RECIPE_IMAGE + " VARCHAR NOT NULL, "+
                RecipeContract.RecipeEntry.RECIPE_SERVINGS + " INTEGER NOT NULL, " +
                RecipeContract.RecipeEntry.RECIPE_STEPS_JSON + " VARCHAR NOT NULL, " +
                RecipeContract.RecipeEntry.RECIPE_INGREDIENTS_JSON + " VARCHAR NOT NULL, " +
                RecipeContract.RecipeEntry.COLUMN_TIME_STAMP+" TIMESTAMP DEFAULT CURRENT_TIMESTAMP"+ ");";

        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RecipeContract.RecipeEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
