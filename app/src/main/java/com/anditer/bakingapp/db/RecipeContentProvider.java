package com.anditer.bakingapp.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.anditer.bakingapp.R;

public class RecipeContentProvider extends ContentProvider {
    RecipeDbHelper dbHelper;
    private static final int RECIPES = 100;
    private static final int ADD_RECIPES = 101;
    private static final int DELETE_SINGLE_RECIPE = 102;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public RecipeContentProvider() {
    }

    private static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(RecipeContract.AUTHORITY,RecipeContract.PATH_RECIPES, RECIPES);
        uriMatcher.addURI(RecipeContract.AUTHORITY,RecipeContract.PATH_ADD_RECIPES, ADD_RECIPES);
        uriMatcher.addURI(RecipeContract.AUTHORITY,RecipeContract.PATH_DELETE_RECIPE, DELETE_SINGLE_RECIPE);
        return uriMatcher;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase database = dbHelper.getWritableDatabase();

        int numRowsDeleted;
        if (null == selection) selection = "1";
        switch (sUriMatcher.match(uri)){
            case DELETE_SINGLE_RECIPE:
                numRowsDeleted = database.delete(
                        RecipeContract.RecipeEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        //create uri to be returned if save successful
        Uri returnUri;
        //check if the uri match our accepted uri
        switch (sUriMatcher.match(uri)){

            case ADD_RECIPES:
                //if it does, we go ahead and add the recipe to our db
                long id = database.insert(RecipeContract.RecipeEntry.TABLE_NAME, null, values);
                if (id>0){
                    returnUri = ContentUris.withAppendedId(RecipeContract.RecipeEntry.CONTENT_URI,id);
                }else {
                    throw new SQLException(getContext().getString(R.string.saving_recipe_failed));
                }
                break;
                default:
                    throw new UnsupportedOperationException(getContext().getString(R.string.uri_not_supported)+ uri.toString());
        }

        getContext().getContentResolver().notifyChange(uri,null);
        //we close the database
        database.close();
        //finally we return the uri
        return returnUri;
    }

    @Override
    public boolean onCreate() {
        //instantiate our database helper and return true
        dbHelper = new RecipeDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor;
        //check if the uri match our accepted uri
        switch (sUriMatcher.match(uri)){
            case RECIPES:
                //if it does, we go ahead and retrieve all recipes
                String query = "SELECT * FROM " + RecipeContract.RecipeEntry.TABLE_NAME + " ORDER BY " +
                        RecipeContract.RecipeEntry._ID + " DESC";
                cursor = database.rawQuery(query,null);
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.uri_not_supported)+ uri.toString());
        }
        getContext().getContentResolver().notifyChange(uri,null);
        //finally we return the uri
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException(getContext().getString(R.string.not_implemented));
    }
}
