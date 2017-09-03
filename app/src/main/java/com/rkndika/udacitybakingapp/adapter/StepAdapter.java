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
import com.rkndika.udacitybakingapp.model.Step;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepAdapterViewHolder> {
    private final static String FILETYPE_MP4 = "mp4";

    private List<Step> steps;
    private Context context;

    private OnStepClickListener mCallback;

    public interface OnStepClickListener {
        void onStepSelected(Step stepClicked);
    }

    class StepAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mStepTextView;
        final ImageView mStepImageView;

        StepAdapterViewHolder(View view) {
            super(view);
            mStepTextView = (TextView) view.findViewById(R.id.tv_step_item);
            mStepImageView = (ImageView) view.findViewById(R.id.iv_step_item);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Step stepClicked = steps.get(adapterPosition);
            mCallback.onStepSelected(stepClicked);
        }
    }

    @Override
    public StepAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        mCallback = (OnStepClickListener) context;
        int layoutIdForListItem = R.layout.item_step;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new StepAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StepAdapterViewHolder stepAdapterViewHolder, int position) {
        Step step = steps.get(position);
        stepAdapterViewHolder.mStepTextView.setText(step.getShortDescription());

        if(step.getThumbnailURL().isEmpty()){
            stepAdapterViewHolder.mStepImageView.setImageResource(R.drawable.no_thumbnail);
        }
        else {
            String thumbnailType = step.getThumbnailURL()
                    .substring(step.getThumbnailURL().lastIndexOf(".") + 1, step.getThumbnailURL()
                            .length()).toLowerCase();
            if(thumbnailType.equals(FILETYPE_MP4)){
                new ThumbnailFromVideo()
                        .execute(new Container(stepAdapterViewHolder, step.getThumbnailURL()));
            }
            else {
                Picasso.with(context)
                        .load(step.getThumbnailURL())
                        .placeholder(R.drawable.rec_grey)
                        .error(R.drawable.no_thumbnail)
                        .into(stepAdapterViewHolder.mStepImageView);
            }

        }
    }

    class Container{
        StepAdapterViewHolder viewHolder;
        String videoUrl;
        Bitmap thumbnail;
        Container(StepAdapterViewHolder viewHolder, String videoUrl){
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
            result.viewHolder.mStepImageView.setImageBitmap(result.thumbnail);
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    @Override
    public int getItemCount() {
        if (null == steps) return 0;
        return steps.size();
    }

    public void setStepsData(List<Step> stepsData) {
        steps = stepsData;
        notifyDataSetChanged();
    }

    public List<Step> getStepsData() {
        return steps;
    }
}
