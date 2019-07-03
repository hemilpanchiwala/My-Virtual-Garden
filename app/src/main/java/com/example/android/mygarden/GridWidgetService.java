package com.example.android.mygarden;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.mygarden.provider.PlantContract;
import com.example.android.mygarden.ui.PlantDetailActivity;
import com.example.android.mygarden.ui.PlantWateringService;
import com.example.android.mygarden.utils.PlantUtils;

import static com.example.android.mygarden.provider.PlantContract.BASE_CONTENT_URI;
import static com.example.android.mygarden.provider.PlantContract.PATH_PLANTS;

public class GridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }
}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context context;
    Cursor cursor;

    public GridRemoteViewsFactory(Context applicationContext) {
        context = applicationContext;

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

        Uri PLANT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build();
        if (cursor != null) cursor.close();
        cursor = context.getContentResolver().query(
                PLANT_URI,
                null,
                null,
                null,
                PlantContract.PlantEntry.COLUMN_CREATION_TIME
        );

    }

    @Override
    public void onDestroy() {
        cursor.close();
    }

    @Override
    public int getCount() {
        if (cursor == null) return 0;
        return cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int i) {

        if (cursor == null || cursor.getCount() == 0) return null;
        cursor.moveToPosition(i);
        int index = cursor.getColumnIndex(PlantContract.PlantEntry._ID);
        int createTimeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME);
        int waterTimeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);
        int plantTypeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE);
        long timeNow = System.currentTimeMillis();
        long plantId = cursor.getLong(index);
        long createdAt = cursor.getLong(createTimeIndex);
        long wateredAt = cursor.getLong(waterTimeIndex);
        int plantType = cursor.getInt(plantTypeIndex);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.plant_provider_widget);

        int imgRes = PlantUtils.getPlantImageRes(context, timeNow-createdAt, timeNow-wateredAt, plantType);
        remoteViews.setImageViewResource(R.id.widget_plant_image, imgRes);
        remoteViews.setTextViewText(R.id.widget_plant_name, String.valueOf(plantId));
        remoteViews.setViewVisibility(R.id.widget_water_drop, View.GONE);

        Bundle extras = new Bundle();
        extras.putLong(PlantDetailActivity.EXTRA_PLANT_ID, plantId);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        remoteViews.setOnClickFillInIntent(R.id.widget_plant_image, fillInIntent);

        return remoteViews;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

}
