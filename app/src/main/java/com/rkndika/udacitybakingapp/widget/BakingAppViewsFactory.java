package com.rkndika.udacitybakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.rkndika.udacitybakingapp.R;
import com.rkndika.udacitybakingapp.model.Ingredient;
import com.rkndika.udacitybakingapp.util.WidgetSharedPreference;

import java.util.List;

public class BakingAppViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private WidgetSharedPreference wPref;
    private List<Ingredient> ingredients = null;
    private Context ctxt=null;
    private int appWidgetId;

    public BakingAppViewsFactory(Context ctxt, Intent intent) {
        this.ctxt=ctxt;
        appWidgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        wPref = new WidgetSharedPreference(ctxt);
        if(wPref.getData() != null){
            ingredients = wPref.getData().getIngredients();
        }
    }

    @Override
    public void onCreate() {
        // no-op
    }

    @Override
    public void onDestroy() {
        // no-op
    }

    @Override
    public int getCount() {
        if(ingredients == null) return 0;
        return(ingredients.size());
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row=new RemoteViews(ctxt.getPackageName(),
                R.layout.item_ingredient);

        row.setTextViewText(R.id.ingredient_name, ingredients.get(position).getIngredient());
        row.setTextViewText(R.id.ingredient_measure,
                ingredients.get(position).getQuantity() + " " + ingredients.get(position).getMeasure());

        return(row);
    }

    @Override
    public RemoteViews getLoadingView() {
        return(null);
    }

    @Override
    public int getViewTypeCount() {
        return(1);
    }

    @Override
    public long getItemId(int position) {
        return(position);
    }

    @Override
    public boolean hasStableIds() {
        return(true);
    }

    @Override
    public void onDataSetChanged() {
        if(wPref.getData() != null){
            ingredients = wPref.getData().getIngredients();
        }
        else {
            ingredients = null;
        }
    }
}