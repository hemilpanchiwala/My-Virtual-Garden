package com.example.android.mygarden;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.RemoteViews;

import com.example.android.mygarden.provider.PlantContract;
import com.example.android.mygarden.ui.MainActivity;
import com.example.android.mygarden.ui.PlantDetailActivity;
import com.example.android.mygarden.ui.PlantWateringService;

/**
 * Implementation of App Widget functionality.
 */
public class PlantProviderWidget extends AppWidgetProvider {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int imgRes, int appWidgetId, long plantId, boolean canWater) {

        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        RemoteViews rv;
        if (width < 300) {
            rv = getSinglePlantRemoteView(context, imgRes, plantId, canWater);
        } else {
            rv = getGardenGridRemoteView(context);
        }
        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        PlantWateringService.startActionUpdatePlantWidget(context);
    }

    public static void updatePlantWidgets(Context context, AppWidgetManager appWidgetManager,
                                          int imgRes, int[] appWidgetIds, long plantId, boolean canWater) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, imgRes, appWidgetId, plantId, canWater);
        }
    }

    private static RemoteViews getSinglePlantRemoteView(Context context, int imgRes, long plantId, boolean canWater){

        Intent intent;

        if (plantId == PlantContract.INVALID_PLANT_ID) {
            intent = new Intent(context, MainActivity.class);
        }else {
            intent = new Intent(context, PlantDetailActivity.class);
            intent.putExtra(PlantDetailActivity.EXTRA_PLANT_ID, plantId);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.plant_provider_widget);

        views.setImageViewResource(R.id.widget_plant_image, imgRes);
        views.setTextViewText(R.id.widget_plant_name, String.valueOf(plantId));

        if (canWater) views.setViewVisibility(R.id.widget_water_drop, View.VISIBLE);
        else views.setViewVisibility(R.id.widget_water_drop, View.INVISIBLE);

        views.setOnClickPendingIntent(R.id.widget_plant_image, pendingIntent);

        Intent waterIntent = new Intent(context, PlantWateringService.class);
        waterIntent.setAction(PlantWateringService.ACTION_WATER_PLANT);
        waterIntent.putExtra(PlantWateringService.EXTRA_PLANT_ID, plantId);

//        PlantWateringService.startActionWaterPlant(context, plantId);
        PendingIntent pendingWaterIntent = PendingIntent.getService(context, 0, waterIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_water_drop, pendingWaterIntent);

        return views;
    }

    private static RemoteViews getGardenGridRemoteView(Context context){

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_grid_view);

        Intent intent = new Intent(context, GridWidgetService.class);
        views.setRemoteAdapter(R.id.widget_grid_view, intent);

        Intent appIntent = new Intent(context, PlantDetailActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_grid_view, appPendingIntent);

        views.setEmptyView(R.id.widget_grid_view, R.id.empty_view);

        return views;

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        PlantWateringService.startActionUpdatePlantWidget(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // Perform any action when one or more AppWidget instances have been deleted
    }

    @Override
    public void onEnabled(Context context) {
        // Perform any action when an AppWidget for this provider is instantiated
    }

    @Override
    public void onDisabled(Context context) {
        // Perform any action when the last AppWidget instance for this provider is deleted
    }
}

