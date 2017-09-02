package com.rkndika.udacitybakingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rkndika.udacitybakingapp.R;
import com.rkndika.udacitybakingapp.model.Ingredient;

import java.util.List;

public class IngredientAdapter extends BaseAdapter {
    private Context context;
    private List<Ingredient> ingredients;
    private LayoutInflater inflter;

    public IngredientAdapter(Context context, List<Ingredient> ingredients) {
        this.context = context;
        this.ingredients = ingredients;
        inflter = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return ingredients.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.item_ingredient, null);
        TextView name = (TextView) view.findViewById(R.id.ingredient_name);
        TextView measure = (TextView) view.findViewById(R.id.ingredient_measure);
        name.setText(ingredients.get(i).getIngredient());
        measure.setText(ingredients.get(i).getQuantity() + " " +ingredients.get(i).getMeasure());
        return view;
    }
}
