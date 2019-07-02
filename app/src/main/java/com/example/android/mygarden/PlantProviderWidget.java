package com.example.android.mygarden;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatDelegate;
import android.widget.RemoteViews;

import com.example.android.mygarden.ui.MainActivity;
import com.example.android.mygarden.ui.PlantWateringService;

/**
 * Implementation of App Widget functionality.
 */
public class PlantProviderWidget extends AppWidgetProvider {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.plant_provider_widget);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        views.setOnClickPendingIntent(R.id.widget_plant_image, pendingIntent);

        Intent waterIntent = new Intent(context, PlantWateringService.class);
        waterIntent.setAction(PlantWateringService.ACTION_WATER_PLANTS);
        PlantWateringService.startActionWaterPlant(context);
        PendingIntent pendingWaterIntent = PendingIntent.getActivity(context, 0, waterIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.widget_water_drop, pendingWaterIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
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

