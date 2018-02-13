package com.anditer.bakingapp.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * This is the service class for our widget list provider or adpater
 */

public class WidgetService extends RemoteViewsService{
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new ListProvider(this.getApplicationContext());
    }
}
