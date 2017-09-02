package com.rkndika.udacitybakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

import com.rkndika.udacitybakingapp.DetailActivity;
import com.rkndika.udacitybakingapp.MainActivity;
import com.rkndika.udacitybakingapp.R;
import com.rkndika.udacitybakingapp.model.Recipe;
import com.rkndika.udacitybakingapp.util.WidgetSharedPreference;
import com.rkndika.udacitybakingapp.widget.WidgetService;

public class BakingAppWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Intent svcIntent=new Intent(context, WidgetService.class);

        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

        RemoteViews widget=new RemoteViews(context.getPackageName(),
                R.layout.baking_app_widget);

        widget.setRemoteAdapter(R.id.widget_list, svcIntent);

        // Get saved data
        WidgetSharedPreference wPref = new WidgetSharedPreference(context);
        Recipe recipePref = wPref.getData();

        if(recipePref != null){
            widget.setTextViewText(R.id.widget_title, recipePref.getName());

            Intent intent = new Intent(context, DetailActivity.class);
            Bundle extras=new Bundle();
            extras.putParcelable(MainActivity.PUT_EXTRA_RECIPE, recipePref);
            intent.putExtras(extras);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            widget.setOnClickPendingIntent(R.id.widget_title, pendingIntent);
            widget.setViewVisibility(R.id.widget_empty, View.GONE);
            widget.setViewVisibility(R.id.widget_content, View.VISIBLE);
        }
        else {
            widget.setTextViewText(R.id.widget_title, context.getString(R.string.ingredient_title));
            widget.setViewVisibility(R.id.widget_content, View.GONE);
            widget.setViewVisibility(R.id.widget_empty, View.VISIBLE);
        }

        appWidgetManager.updateAppWidget(appWidgetId, widget);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Bundle extras = intent.getExtras();
        if(extras != null && extras.containsKey(AppWidgetManager.EXTRA_APPWIDGET_IDS)){
            int ids[] = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            onUpdate(context,appWidgetManager,ids);
        }


    }
}

