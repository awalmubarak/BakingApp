package com.anditer.bakingapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anditer.bakingapp.R;
import com.anditer.bakingapp.StepDetailActivity;
import com.anditer.bakingapp.adapter.RecipeStepsAdapter;
import com.anditer.bakingapp.model.Ingredient;
import com.anditer.bakingapp.model.Recipe;
import com.anditer.bakingapp.model.Step;

import java.util.ArrayList;


public class RecipeStepsFragment extends Fragment implements RecipeStepsAdapter.OnRecipeStepClickListener{

    LinearLayoutManager layoutManager;
    RecipeStepsAdapter stepsAdapter;
    Recipe myRecipe;
    ArrayList<Step> steps = new ArrayList<>();
    ArrayList<Ingredient> ingredients;
    public static final String EXTRA_POSITION = "Position";
    public RecipeStepsFragment() {
        // Required empty public constructor
    }

    public void setStep(ArrayList<Step> step) {
        this.steps = step;
    }

    public void setIngredient(ArrayList<Ingredient> ingredient) {
        this.ingredients = ingredient;
    }

    public static RecipeStepsFragment newInstance() {
        return new RecipeStepsFragment();

    }

    public void setMyRecipe(Recipe recipe){
        myRecipe = recipe;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null && savedInstanceState.containsKey(Intent.EXTRA_TEXT)){
            myRecipe = savedInstanceState.getParcelable(Intent.EXTRA_TEXT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.recipe_steps_recycler, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.mRecipeStepsRecycler);
        layoutManager = new LinearLayoutManager(getContext());
        stepsAdapter = new RecipeStepsAdapter(myRecipe.getSteps(), getContext(),this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(stepsAdapter);
        return view;

    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onRecipeStepClick(int pos) {
        if (getContext().getResources().getBoolean(R.bool.isTablet)){
            FragmentManager fragmentManager = getFragmentManager();
            StepDetailFragment stepDetailFragment = new StepDetailFragment();
            stepDetailFragment.setPosition(pos);
            stepDetailFragment.setMyRecipe(myRecipe);
            fragmentManager.beginTransaction().replace(R.id.mRecipeStepDetailFrame,stepDetailFragment).commit();
        }else {
            Intent intent = new Intent(getContext(), StepDetailActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, myRecipe);
            intent.putExtra(EXTRA_POSITION, pos);
            startActivity(intent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Intent.EXTRA_TEXT, myRecipe);
    }
}
