package com.anditer.bakingapp.adapter;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anditer.bakingapp.R;
import com.anditer.bakingapp.db.RecipeContract;
import com.anditer.bakingapp.db.RecipeDbHelper;
import com.anditer.bakingapp.model.Recipe;
import com.anditer.bakingapp.widget.RecipeAppWidget;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Our Recipe Adapter
 */

public class RecipeAdaper extends RecyclerView.Adapter<RecipeAdaper.RecipeViewHolder> {
    private Context context;
    private ArrayList<Recipe> recipeArrayList;
    private OnItemClickListener onItemClickListener;
    private View view;

    public RecipeAdaper(Context context, ArrayList<Recipe> recipeArrayList,
                        OnItemClickListener onItemClickListener, View v) {
        this.context = context;
        this.recipeArrayList = recipeArrayList;
        this.onItemClickListener = onItemClickListener;
        this.view = v;
    }

    public interface OnItemClickListener{
        void onItemClicked(int position);
    }

    public void setRecipeArrayList(ArrayList<Recipe> recipeArrayList) {
        this.recipeArrayList = recipeArrayList;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.single_recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecipeViewHolder holder, int position) {
        final Recipe recipe = recipeArrayList.get(position);
        if (checkIfFavorite(String.valueOf(recipe.getId()))){
            holder.mFavButton.setImageResource(R.drawable.fav_star);
        }
        holder.mRecipeName.setText(recipe.getName());
        holder.mFavButton.setOnClickListener(new View.OnClickListener() {
            boolean isFavorite = checkIfFavorite(String.valueOf(recipe.getId()));
            @Override
            public void onClick(View view) {
                if (isFavorite){
                    if (unFavoriteRecipe(String.valueOf(recipe.getId()))){
                        Snackbar.make(view, R.string.unfav_success,Snackbar.LENGTH_LONG).show();
                        updateWidgets();
                        holder.mFavButton.setImageResource(R.drawable.unfav_star);
                        isFavorite = false;
                    }else {
                        Snackbar.make(view, R.string.unfav_unsuccessful,Snackbar.LENGTH_LONG).show();
                    }
                }else{
                    if (favoriteRecipe(recipe)){
                        Snackbar.make(view, R.string.fav_success,Snackbar.LENGTH_LONG).show();
                        updateWidgets();
                        holder.mFavButton.setImageResource(R.drawable.fav_star);
                        isFavorite =true;
                    }else {
                        Snackbar.make(view, R.string.fav_unsuccess,Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });
    }


    private void updateWidgets(){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, RecipeAppWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.mWidgetListView);
    }

    private boolean favoriteRecipe(Recipe recipe) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(RecipeContract.RecipeEntry.RECIPE_ID, recipe.getId());
        contentValues.put(RecipeContract.RecipeEntry.RECIPE_NAME, recipe.getName());
        contentValues.put(RecipeContract.RecipeEntry.RECIPE_IMAGE, recipe.getImage());
        contentValues.put(RecipeContract.RecipeEntry.RECIPE_INGREDIENTS_JSON, recipe.getIngredientsJsonString());
        contentValues.put(RecipeContract.RecipeEntry.RECIPE_SERVINGS, recipe.getServings());
        contentValues.put(RecipeContract.RecipeEntry.RECIPE_STEPS_JSON, recipe.getStepsJsonString());
        return context.getContentResolver().insert(RecipeContract.RecipeEntry.ADD_RECIPE_URI,contentValues)!=null;
    }

    private boolean unFavoriteRecipe(String s) {
        int numRows = context.getContentResolver().delete(RecipeContract.RecipeEntry.DELETE_RECIPE_URI, RecipeContract.RecipeEntry.RECIPE_ID + "=?",new String[]{s});
        if (numRows>0){
            return true;
        }
        return false;
    }

    @Override
    public int getItemCount() {
        if (recipeArrayList!=null && recipeArrayList.size()>0){
            return recipeArrayList.size();
        }
        return 0;
    }

    public boolean checkIfFavorite(String id){
        RecipeDbHelper dbHelper = new RecipeDbHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + RecipeContract.RecipeEntry.TABLE_NAME +
                " WHERE " + RecipeContract.RecipeEntry.RECIPE_ID + " = " + id;
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.getCount()<=0){
            cursor.close();database.close();
            return false;
        }
        cursor.close();database.close();
        return true;
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.mRecipeName) TextView mRecipeName;
        @BindView(R.id.mFavButton) ImageView mFavButton;

        private RecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

            setIsRecyclable(false);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            onItemClickListener.onItemClicked(pos);
        }
    }
}
