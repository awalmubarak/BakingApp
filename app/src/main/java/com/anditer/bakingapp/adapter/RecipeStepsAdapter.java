package com.anditer.bakingapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anditer.bakingapp.R;
import com.anditer.bakingapp.model.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

/**
 * Adapter for Recycler view Steps
 *
 */

public class RecipeStepsAdapter extends RecyclerView.Adapter<RecipeStepsAdapter.MyRecipeStepsViewHolder>{
    private ArrayList<Step> steps;
    private Context context;
    private OnRecipeStepClickListener onRecipeStepClickListener;

    public RecipeStepsAdapter(ArrayList<Step> steps, Context context, OnRecipeStepClickListener listener) {
        this.steps = steps;
        this.context = context;
        this.onRecipeStepClickListener = listener;
    }

    public interface OnRecipeStepClickListener{
        void onRecipeStepClick(int pos);
    }

    @Override
    public MyRecipeStepsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.single_recipe_step_item, parent, false);
        return new MyRecipeStepsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyRecipeStepsViewHolder holder, int position) {
        Step step = steps.get(position);
        holder.mRecipeName.setText(step.getShortDescription());
        Log.i(TAG, step.getShortDescription());
        if (position==0){
            holder.mRecipeName.setTextColor(Color.WHITE);
            holder.mRecipeName.setTypeface(null,Typeface.BOLD);
            holder.mRecipeStepLinearLayout.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        }
    }

    @Override
    public int getItemCount() {
        if (steps!=null&&steps.size()>0){
            return steps.size();
        }
        return 0;
    }


    protected class MyRecipeStepsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.mRecipeName) TextView mRecipeName;
        @BindView(R.id.mRecipeStepLinearLayout) LinearLayout mRecipeStepLinearLayout;
        private MyRecipeStepsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            setIsRecyclable(false);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            onRecipeStepClickListener.onRecipeStepClick(pos);
        }
    }
}
