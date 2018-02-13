package com.anditer.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.anditer.bakingapp.fragments.RecipeStepsFragment;
import com.anditer.bakingapp.model.Recipe;

public class RecipeStepsActivity extends AppCompatActivity{
    Recipe myRecipe = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_steps);
        Intent intent = getIntent();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (intent.hasExtra(MainActivity.RECIPE_EXTRA)) {
            myRecipe = intent.getParcelableExtra(MainActivity.RECIPE_EXTRA);
            FragmentManager fragmentManager = getSupportFragmentManager();
            setTitle(myRecipe.getName());
            RecipeStepsFragment recipeStepsFragment = (RecipeStepsFragment) fragmentManager.findFragmentByTag("steps");
            if (recipeStepsFragment == null) {
                recipeStepsFragment = new RecipeStepsFragment();
            }
            recipeStepsFragment.setMyRecipe(myRecipe);
            fragmentManager.beginTransaction().replace(R.id.mRecipeStepsFrame, recipeStepsFragment, "steps").commit();
        }

    }

}
