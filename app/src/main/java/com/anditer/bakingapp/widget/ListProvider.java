package com.anditer.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.anditer.bakingapp.MainActivity;
import com.anditer.bakingapp.R;
import com.anditer.bakingapp.db.RecipeContract;
import com.anditer.bakingapp.model.Recipe;
import com.anditer.bakingapp.model.Step;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static com.anditer.bakingapp.MainActivity.POSITION_EXTRA;
import static com.anditer.bakingapp.MainActivity.convertJsonToIngredientArray;
import static com.anditer.bakingapp.MainActivity.convertJsonToStepArray;

/**
 * This acts the adapter for our widget
 */

public class ListProvider implements RemoteViewsService.RemoteViewsFactory {

    private Recipe recipes;
    private ArrayList<Step> steps;
    private Context context = null;
    private static String TAG = "ListProvider";
    String FROM_WIDGET = "WIDGET";


    public ListProvider(Context context) {
        this.context = context;
        steps = new ArrayList<>();
        recipes = new Recipe();
        getLastFavoriteRecipe();
    }

    private void getLastFavoriteRecipe(){
        Cursor cursor = context.getContentResolver().query(RecipeContract.RecipeEntry.CONTENT_URI, null,null,null,null);
        recipes = getRecipeFromCursor(cursor);
        Log.i(TAG, " populate");
    }

    //this is a helper class to extract recipes for Cursor
    private Recipe getRecipeFromCursor(Cursor cursor) {
        Log.i(TAG, cursor.toString());
        Recipe recipe = new Recipe();
        if (cursor.getCount()>0){
            cursor.moveToFirst();
            int id  = Integer.parseInt(cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.RECIPE_ID)));
            String name = cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.RECIPE_NAME));
            String ingredientJson = cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.RECIPE_INGREDIENTS_JSON));
            String stepsJson = cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.RECIPE_STEPS_JSON));
            int servings = Integer.parseInt(cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.RECIPE_SERVINGS)));
            String image = cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.RECIPE_IMAGE));

            recipe = new Recipe(id,name,servings,image);
            if (ingredientJson!=null && ingredientJson.length()>0){
                try {
                    JSONArray ingredientJsonArray = new JSONArray(ingredientJson);
                    recipe.setIngredients(convertJsonToIngredientArray(ingredientJsonArray));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (stepsJson!=null && stepsJson.length()>0){
                try {
                    JSONArray stepsJsonArray = new JSONArray(stepsJson);
                    steps.clear();
                    steps.addAll(convertJsonToStepArray(stepsJsonArray));
                    steps.add(0, new Step(context.getResources().getString(R.string.ingredients_for_steps)));
                    recipe.setSteps(steps);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        cursor.close();
        return recipe;
    }



    @Override
    public void onCreate() {
        getLastFavoriteRecipe();
    }

    @Override
    public void onDataSetChanged() {
        getLastFavoriteRecipe();

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (recipes.getSteps()!=null && recipes.getSteps().size()>0){
            return recipes.getSteps().size();
        }
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int i) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.single_widget_recipe_item);
        Step step = recipes.getSteps().get(i);
        remoteView.setTextViewText(R.id.mRecipeWigdetName, step.getShortDescription());

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(MainActivity.RECIPE_EXTRA, recipes);
        bundle.putInt(POSITION_EXTRA, i);
        bundle.putBoolean(FROM_WIDGET, true);
        intent.putExtras(bundle);
        remoteView.setOnClickFillInIntent(R.id.mRecipeWigdetName, intent);
        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
