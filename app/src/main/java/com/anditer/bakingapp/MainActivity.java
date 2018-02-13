package com.anditer.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.anditer.bakingapp.adapter.RecipeAdaper;
import com.anditer.bakingapp.db.RecipeContract;
import com.anditer.bakingapp.model.Ingredient;
import com.anditer.bakingapp.model.Recipe;
import com.anditer.bakingapp.model.Step;
import com.anditer.bakingapp.network.MySingleton;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RecipeAdaper.OnItemClickListener {
    private static final String TAG = "MainActivity";
    public static final String RECIPE_EXTRA = "RECIPE_EXTRA";
    public static final String INGREDIENT_EXTRA = "INGREDIENT_EXTRA";
    public static final String STEP_EXTRA = "STEP_EXTRA";
    public static final String POSITION_EXTRA = "POSITION_EXTRA";
    private static final int GRID_SPAN = 2;
    private static final int GRID_SPACING = 8;
    @BindView(R.id.mRecipeRecycler) RecyclerView mRecipeRecycler;
    @BindView(R.id.mRecipeProgressBar) ProgressBar mRecipeProgressBar;
    RecyclerView.LayoutManager layoutManager;
    RecipeAdaper recipeAdaper;
    public static ArrayList<Recipe> recipes;
    boolean isFavoriteSelected;
    JsonArrayRequest jsonArrayRequest;
    private final String RECIPE_SAVE_STATE = "recipeState";
    private final String IS_FAVORITE_STATE = "isFavorite";
    public static final String RECIPE_API = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        recipes = new ArrayList<>();

        //check if it's a tablet and set layout manager to grid
        if (getResources().getBoolean(R.bool.isTablet)){
            layoutManager = new GridLayoutManager(this, GRID_SPAN);
            mRecipeRecycler.addItemDecoration(new SpacesItemDecoration(GRID_SPACING));
        }else {
            //else it's a smart phone so we use the linear layout
            layoutManager = new LinearLayoutManager(this);
        }
        //setup recyclerView
        mRecipeRecycler.setLayoutManager(layoutManager);

        if (savedInstanceState==null || !savedInstanceState.containsKey(RECIPE_SAVE_STATE)) {
            if (isOnline()) {
                getDataFromApiAndNotifyAdapter();
            } else {
                Snackbar.make(findViewById(android.R.id.content), R.string.not_internet_message, Snackbar.LENGTH_LONG).show();
            }
        }else {
            if (savedInstanceState.containsKey(RECIPE_SAVE_STATE)){
                recipes = savedInstanceState.getParcelableArrayList(RECIPE_SAVE_STATE);
                mRecipeProgressBar.setVisibility(View.GONE);
            }
            if (savedInstanceState.containsKey(IS_FAVORITE_STATE)){
                isFavoriteSelected = savedInstanceState.getBoolean(IS_FAVORITE_STATE);
            }
        }

        recipeAdaper = new RecipeAdaper(MainActivity.this, recipes, MainActivity.this, findViewById(android.R.id.content));
        mRecipeRecycler.setAdapter(recipeAdaper);

    }



    //helper function to help us check if our device is connected to the internet
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }



    //we download the recipes using volley library and notify our adapter
    public void getDataFromApiAndNotifyAdapter(){
        if (isFavoriteSelected){
            Snackbar.make(findViewById(android.R.id.content), R.string.already_showing_all,Snackbar.LENGTH_LONG).show();
            return;
        }
        isFavoriteSelected = true;
        mRecipeProgressBar.setVisibility(View.VISIBLE);
        //Using Volley,We create a Json Array Request which will return our Json Array of Recipes
        jsonArrayRequest = new JsonArrayRequest(RECIPE_API, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //check if response is not empty before extracting data
                if (response!=null && response.length()>0) {
                    Log.i(TAG, response.toString());
                    recipes.clear();
                    for (int i = 0; i < response.length(); i++) {
                        Recipe recipe = new Recipe();
                        try {
                            JSONObject object = response.getJSONObject(i);
                            String name = object.optString("name");
                            int servings = object.optInt("servings");
                            int id = object.optInt("id");
                            String image = object.optString("image");
                            recipe = new Recipe(id, name, servings, image);

                            //get ingredients json array and extract individual ingredients
                            JSONArray ingredientsArray = object.getJSONArray("ingredients");

                            if (ingredientsArray != null && ingredientsArray.length() > 0) {
                                recipe.setIngredientsJsonString(ingredientsArray.toString());
                                //we convert all the json to an array using a helper function
                                recipe.setIngredients(convertJsonToIngredientArray(ingredientsArray));
                            }


                            //get steps json array and extract individual steps
                            JSONArray stepsArray = object.getJSONArray("steps");
                            if (stepsArray != null && stepsArray.length() > 0) {
                                recipe.setStepsJsonString(stepsArray.toString());
                                //we convert all the json to an array using a helper function
                                recipe.setSteps(convertJsonToStepArray(stepsArray));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //after saving all the info in the recipe object, we then add
                        //it to our final recipe array list which will be returned after the loop
                        recipes.add(recipe);
                    }

                    recipeAdaper.notifyDataSetChanged();
                    mRecipeProgressBar.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //if we get an error, we log it to the Logcat
                Log.i(TAG, error.getMessage());
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);


    }



    //when a Recipe is clicked on, we pass it to the next activity
    @Override
    public void onItemClicked(int position) {
        Recipe  myRecipe= recipes.get(position);
        myRecipe.getSteps().add(0,new Step(getString(R.string.ingredients_for_steps)));
        Intent intent = new Intent(this, RecipeStepsActivity.class);
        intent.putExtra(RECIPE_EXTRA, myRecipe);
        startActivity(intent);
    }



    //we create and populate our menu with the R.menu.sort_menu resource
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_menu, menu);
        return true;
    }



    //when a menu item is selected, we make sure the right action is performed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sortFavorites:
                getDataFromDbAndUpdateAdapter();
                return true;
            case R.id.sortAllRecipes:
                getDataFromApiAndNotifyAdapter();
                return true;
            default:
                return false;
        }
    }



    //this function gets data from our sqlite Db using content provider
    public void getDataFromDbAndUpdateAdapter(){
        if (jsonArrayRequest!=null){
            jsonArrayRequest.cancel();
        }
        if(!isFavoriteSelected){
            Snackbar.make(findViewById(android.R.id.content), R.string.already_showing_favorites,Snackbar.LENGTH_LONG).show();
            return;
        }

        ArrayList<Recipe> arrayList;
        Cursor cursor = getContentResolver().query(RecipeContract.RecipeEntry.CONTENT_URI, null,null,null,null);
        arrayList = getArrayListFromCursor(cursor);
        if (arrayList!=null && arrayList.size()>0){
            recipes.clear();
            recipes.addAll(arrayList);
            for(Recipe recipe: recipes){
                Log.i("FROMDB", recipe.getSteps().get(3).getShortDescription());
            }
            isFavoriteSelected = false;
            recipeAdaper.notifyDataSetChanged();
        }else {
            Snackbar.make(findViewById(android.R.id.content), R.string.no_fav_recipe_found,Snackbar.LENGTH_LONG).show();
        }

    }



    //this is a helper class to extract recipes for Cursor
    public ArrayList<Recipe> getArrayListFromCursor(Cursor cursor) {
        Log.i(TAG, cursor.toString());
        ArrayList<Recipe> newRecipes = new ArrayList<>();

        if (cursor.moveToFirst()){
            do {
                int id  = Integer.parseInt(cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.RECIPE_ID)));
                String name = cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.RECIPE_NAME));
                Log.i(TAG, name);
                String ingredientJson = cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.RECIPE_INGREDIENTS_JSON));
                Log.i(TAG, ingredientJson);
                String stepsJson = cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.RECIPE_STEPS_JSON));
                Log.i(TAG, stepsJson);
                int servings = Integer.parseInt(cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.RECIPE_SERVINGS)));
                String image = cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.RECIPE_IMAGE));

                Recipe recipe = new Recipe(id,name,servings,image);
                recipe.setStepsJsonString(ingredientJson);
                if (ingredientJson!=null && ingredientJson.length()>0){
                    JSONArray ingredientJsonArray = null;
                    try {
                        ingredientJsonArray = new JSONArray(ingredientJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG, "INGE");
                    }

                    recipe.setIngredients(convertJsonToIngredientArray(ingredientJsonArray));
                }

                recipe.setStepsJsonString(stepsJson);
                if (stepsJson!=null && stepsJson.length()>0){
                    JSONArray stepsJsonArray = null;
                    try {
                        stepsJsonArray = new JSONArray(stepsJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG, "STEPS");
                    }
                    recipe.setSteps(convertJsonToStepArray(stepsJsonArray));
                }

                newRecipes.add(recipe);
            }while (cursor.moveToNext());
        }

        cursor.close();
        return newRecipes;
    }



    //this is a helper class to convert Recipe ingredients Json data to Array
    public static ArrayList<Ingredient> convertJsonToIngredientArray(JSONArray ingredientsArray){
        Log.i(TAG, ingredientsArray.toString());
        ArrayList<Ingredient> ingredientArrayList = new ArrayList<>();
        for (int k = 0; k < ingredientsArray.length(); k++) {
            JSONObject ingredientObject = null;
            try {
                ingredientObject = ingredientsArray.getJSONObject(k);
                int quantity = ingredientObject.optInt("quantity");
                String measure = ingredientObject.optString("measure");
                String ingredient = ingredientObject.optString("ingredient");
                Ingredient ingred = new Ingredient(ingredient, measure, quantity);
                ingredientArrayList.add(ingred);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return ingredientArrayList;
    }



    //this is a helper class to convert Recipe steps Json data to Array
    public static ArrayList<Step> convertJsonToStepArray(JSONArray stepsArray){
        Log.i(TAG, stepsArray.toString());
        ArrayList<Step> stepArrayList = new ArrayList<>();
        for (int j = 0; j < stepsArray.length(); j++) {
            JSONObject stepObject = null;
            try {
                stepObject = stepsArray.getJSONObject(j);
                int step_id = stepObject.optInt("id");
                String shortDescription = stepObject.optString("shortDescription");
                String videoURL = stepObject.optString("videoURL");
                String description = stepObject.optString("description");
                String thumbnailURL = stepObject.optString("thumbnailURL");
                Step step = new Step(step_id,shortDescription,description,videoURL,thumbnailURL);
                stepArrayList.add(step);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return stepArrayList;
    }



    //on save instance state, we save our recipes
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (recipes!=null){
            outState.putParcelableArrayList(RECIPE_SAVE_STATE, recipes);
            outState.putBoolean(IS_FAVORITE_STATE, isFavoriteSelected);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (jsonArrayRequest!=null){
            jsonArrayRequest.cancel();
            jsonArrayRequest = null;
        }
    }

}
