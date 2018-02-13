package com.anditer.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.anditer.bakingapp.fragments.StepDetailFragment;
import com.anditer.bakingapp.model.Recipe;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.anditer.bakingapp.MainActivity.POSITION_EXTRA;
import static com.anditer.bakingapp.fragments.RecipeStepsFragment.EXTRA_POSITION;

public class StepDetailActivity extends AppCompatActivity {
    @BindView(R.id.mPreviousButton) Button mPreviousButton;
    @BindView(R.id.mNextButton) Button mNextButton;
    @BindView(R.id.buttonLinearLayout) LinearLayout buttonLinearLayout;
    int positionCount;
    Recipe recipe;
    String FROM_WIDGET = "WIDGET";
    private final String POSITION_KEY = "position";
    private final String RECIPE_KEY = "recipe";
    private final String URI_KEY = "recipe";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_step_detail);
        ButterKnife.bind(this);

        if (getResources().getBoolean(R.bool.isLandscape)){
            buttonLinearLayout.setVisibility(View.GONE);
        }
        Intent intent = getIntent();

        if (intent.hasExtra(FROM_WIDGET)){

            if (intent.getBooleanExtra(FROM_WIDGET,false)){
                recipe = intent.getParcelableExtra(MainActivity.RECIPE_EXTRA);
                int position = intent.getIntExtra(POSITION_EXTRA, -1);
                if (savedInstanceState!=null){
                    if (savedInstanceState.containsKey(POSITION_KEY)
                            && savedInstanceState.containsKey(RECIPE_KEY)){
                        position = savedInstanceState.getInt(POSITION_KEY, -1);
                        recipe = savedInstanceState.getParcelable(RECIPE_KEY);
                    }
                }
                checkPositionAndSetFragment(position);
            }
        }
        if (intent.hasExtra(Intent.EXTRA_TEXT) && intent.hasExtra(EXTRA_POSITION)){
            int position = intent.getIntExtra(EXTRA_POSITION, -1);
            recipe = intent.getParcelableExtra(Intent.EXTRA_TEXT);
            if (savedInstanceState!=null){
                if (savedInstanceState.containsKey(POSITION_KEY)
                        && savedInstanceState.containsKey(RECIPE_KEY)){
                    position = savedInstanceState.getInt(POSITION_KEY, -1);
                    recipe = savedInstanceState.getParcelable(RECIPE_KEY);
                }
            }
            checkPositionAndSetFragment(position);

        }

    }

    private void checkPositionAndSetFragment(int position){
        if (position!=-1 && recipe!=null){
            positionCount = position;
            setTitle(recipe.getName() + " - Step "+ String.valueOf(positionCount+1));
            FragmentManager fragmentManager = getSupportFragmentManager();

            StepDetailFragment stepDetailFragment = (StepDetailFragment) fragmentManager.findFragmentByTag("details");
            if (stepDetailFragment == null) {
                stepDetailFragment = new StepDetailFragment();
            }
            stepDetailFragment.setPosition(position);
            stepDetailFragment.setMyRecipe(recipe);
            fragmentManager.beginTransaction().replace(R.id.mRecipeStepDetailFrame,stepDetailFragment, "details").commit();
            if (positionCount == recipe.getSteps().size() - 1){
                mPreviousButton.setEnabled(true);
                mNextButton.setEnabled(false);
            }
            if (positionCount == 0){
                mPreviousButton.setEnabled(false);
                mNextButton.setEnabled(true);
            }
            setUpButtonListners();

        }

    }

    private void setUpButtonListners(){
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = (Button) view;
                if (button.isEnabled()) {
                    if (recipe != null) {
                        positionCount++;
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        StepDetailFragment fragment = new StepDetailFragment();
                        fragment.setPosition(positionCount);
                        fragment.setMyRecipe(recipe);
                        fragmentManager.beginTransaction().replace(R.id.mRecipeStepDetailFrame, fragment).commit();
                        setTitle(recipe.getName() + " - Step "+ String.valueOf(positionCount+1));
                        if (positionCount==recipe.getSteps().size()-1){
                            button.setEnabled(false);
                        }else if (positionCount==1){
                            mPreviousButton.setEnabled(true);
                        }
                    }
                }
            }
        });

        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = (Button) view;
                if (button.isEnabled()) {
                    if (recipe != null) {
                        positionCount--;
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        StepDetailFragment fragment = new StepDetailFragment();
                        fragment.setPosition(positionCount);
                        fragment.setMyRecipe(recipe);
                        fragmentManager.beginTransaction().replace(R.id.mRecipeStepDetailFrame, fragment).commit();

                        setTitle(recipe.getName() + " - Step " + String.valueOf(positionCount + 1));
                        if (positionCount == 0) {
                            mPreviousButton.setEnabled(false);
                        } else if (positionCount == recipe.getSteps().size() - 2) {
                            mNextButton.setEnabled(true);
                        }
                    }
                }
            }
        });


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RECIPE_KEY, recipe);
        outState.putInt(POSITION_KEY, positionCount);
    }
}
