package com.anditer.bakingapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anditer.bakingapp.R;
import com.anditer.bakingapp.model.Ingredient;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Recipe Ingredient Adapter
 */

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.MyIngredientViewHolder> {
    Context context;
    ArrayList<Ingredient> ingredients;

    public IngredientAdapter(Context context, ArrayList<Ingredient> ingredients) {
        this.context = context;
        this.ingredients = ingredients;
    }

    @Override
    public MyIngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.single_ingredient_item, parent, false);
        return new MyIngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyIngredientViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.mRecipeIngredientName.setText(ingredient.getName());
        holder.mIngredientMeasure.setText(ingredient.getMeasure());
        holder.mIngredientQuantity.setText(String.valueOf(ingredient.getQuantity()));
    }

    @Override
    public int getItemCount() {
        if (ingredients!=null && ingredients.size()>0){
            return ingredients.size();
        }
        return 0;
    }

    public static class MyIngredientViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.mRecipeIngredientName) TextView mRecipeIngredientName;
        @BindView(R.id.mIngredientQuantity) TextView mIngredientQuantity;
        @BindView(R.id.mIngredientMeasure) TextView mIngredientMeasure;
        public MyIngredientViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
