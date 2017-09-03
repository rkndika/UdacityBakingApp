package com.rkndika.udacitybakingapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rkndika.udacitybakingapp.R;
import com.rkndika.udacitybakingapp.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeAdapterViewHolder> {

    private List<Recipe> recipes;
    private Context context;

    private final RecipeAdapterOnClickHandler mClickHandler;

    public interface RecipeAdapterOnClickHandler {
        void onItemClick(Recipe recipeClicked);
    }

    public RecipeAdapter(RecipeAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    class RecipeAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView mRecipeImageView;
        final TextView mRecipeTextView;

        RecipeAdapterViewHolder(View view) {
            super(view);
            mRecipeImageView = (ImageView) view.findViewById(R.id.iv_recipe_item);
            mRecipeTextView = (TextView) view.findViewById(R.id.tv_recipe_item);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Recipe recipeClicked = recipes.get(adapterPosition);
            mClickHandler.onItemClick(recipeClicked);
        }
    }

    @Override
    public RecipeAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.item_recipe;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new RecipeAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeAdapterViewHolder recipeAdapterViewHolder, int position) {
        Recipe recipe = recipes.get(position);
        recipeAdapterViewHolder.mRecipeTextView.setText(recipe.getName());

        if(recipe.getImage().isEmpty()){
            recipeAdapterViewHolder.mRecipeImageView.setImageResource(R.drawable.no_thumbnail);
        }
        else {
            Picasso.with(context)
                    .load(recipe.getImage())
                    .placeholder(R.drawable.rec_grey)
                    .error(R.drawable.no_thumbnail)
                    .into(recipeAdapterViewHolder.mRecipeImageView);
        }
    }

    @Override
    public int getItemCount() {
        if (null == recipes) return 0;
        return recipes.size();
    }

    public void setRecipesData(List<Recipe> recipesData) {
        recipes = recipesData;
        notifyDataSetChanged();
    }

    public List<Recipe> getRecipesData() {
        return recipes;
    }
}