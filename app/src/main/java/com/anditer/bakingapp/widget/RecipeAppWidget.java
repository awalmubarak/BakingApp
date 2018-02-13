package com.anditer.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.anditer.bakingapp.MainActivity;
import com.anditer.bakingapp.R;
import com.anditer.bakingapp.RecipeStepsActivity;
import com.anditer.bakingapp.StepDetailActivity;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews remoteViews = updateWidgetListView(context);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }



    private static RemoteViews updateWidgetListView(Context context) {

        //which layout to show on widget
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.recipe_app_widget);

        Intent svcIntent = new Intent(context, WidgetService.class);
        remoteViews.setRemoteAdapter(R.id.mWidgetListView, svcIntent);

        Intent tempIntent;
        if (context.getResources().getBoolean(R.bool.isTablet)){
            tempIntent = new Intent(context, RecipeStepsActivity.class);
        }else {
            tempIntent = new Intent(context, StepDetailActivity.class);
        }

        PendingIntent tempPendingIntent = PendingIntent.getActivity(context,0,tempIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.mWidgetListView, tempPendingIntent);
        remoteViews.setEmptyView(R.id.mWidgetListView, R.id.empty_view);
        //intent for empty list view
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.empty_view, pendingIntent);

        return remoteViews;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

