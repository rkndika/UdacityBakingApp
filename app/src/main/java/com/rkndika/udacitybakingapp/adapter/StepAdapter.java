package com.rkndika.udacitybakingapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rkndika.udacitybakingapp.R;
import com.rkndika.udacitybakingapp.model.Step;

import java.util.List;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepAdapterViewHolder> {

    private List<Step> steps;

    private OnStepClickListener mCallback;

    public interface OnStepClickListener {
        void onStepSelected(Step stepClicked);
    }

    class StepAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mStepTextView;

        StepAdapterViewHolder(View view) {
            super(view);
            mStepTextView = (TextView) view.findViewById(R.id.tv_step_item);
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
        Context context = viewGroup.getContext();
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
