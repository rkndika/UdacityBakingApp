package com.rkndika.udacitybakingapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rkndika.udacitybakingapp.R;
import com.rkndika.udacitybakingapp.model.Recipe;
import com.rkndika.udacitybakingapp.model.Step;

import java.util.HashMap;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeAdapterViewHolder> {

    private List<Recipe> recipes;

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
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.item_recipe;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new RecipeAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeAdapterViewHolder recipeAdapterViewHolder, int position) {
        Recipe recipe = recipes.get(position);
        recipeAdapterViewHolder.mRecipeTextView.setText(recipe.getName());

        Step thumbnailStep = recipe.getSteps().get(recipe.getSteps().size()-1);
        if(thumbnailStep.getVideoURL().isEmpty()){
            recipeAdapterViewHolder.mRecipeImageView.setImageResource(R.drawable.rec_grey);
        }
        else {
            new ThumbnailFromVideo()
                    .execute(new Container(recipeAdapterViewHolder, thumbnailStep.getVideoURL()));
        }
    }

    class Container{
        RecipeAdapterViewHolder viewHolder;
        String videoUrl;
        Bitmap thumbnail;
        Container(RecipeAdapterViewHolder viewHolder, String videoUrl){
            this.viewHolder = viewHolder;
            this.videoUrl = videoUrl;
        }
    }

    private class ThumbnailFromVideo extends AsyncTask<Container, Void, Container> {

        @Override
        protected Container doInBackground(Container... params) {
            Container container = params[0];
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever .setDataSource(container.videoUrl, new HashMap<String, String>());
            container.thumbnail = mediaMetadataRetriever.getFrameAtTime(1000); //unit in microsecond
            return container;
        }

        @Override
        protected void onPostExecute(Container result) {
            result.viewHolder.mRecipeImageView.setImageBitmap(result.thumbnail);
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
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