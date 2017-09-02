package com.rkndika.udacitybakingapp;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.rkndika.udacitybakingapp.adapter.IngredientAdapter;
import com.rkndika.udacitybakingapp.adapter.StepAdapter;
import com.rkndika.udacitybakingapp.model.Recipe;
import com.rkndika.udacitybakingapp.model.Step;

import java.util.ArrayList;

public class DetailRecipeFragment extends Fragment {
    private final static String ISINGREDIENT_STATE = "isIngredientState";
    private final static String RECYCLERVIEW_STATE = "rvState";
    private final static String RECYCLERVIEW_DATA_STATE = "rvDataState";

    private RecyclerView rvStepsContent;
    private StepAdapter mStepAdapter;

    private CardView cvIngredientContent;

    private Recipe recipe;
    private Boolean isIngredient;

    private TextView tvMenuIngredient, tvMenuSteps;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_detail_recipe, container, false);
        Bundle bundle = this.getArguments();
        recipe = bundle.getParcelable(MainActivity.PUT_EXTRA_RECIPE);

        // initialize view
        initView(rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if(savedInstanceState == null){
            // initialize
            isIngredient = true;
            showStateView();
        }
        else {
            showPreviousView(savedInstanceState);
        }
        super.onActivityCreated(savedInstanceState);
    }

    private void initView(View rootView) {
        cvIngredientContent = (CardView) rootView.findViewById(R.id.cv_ingredient_content);
        rvStepsContent = (RecyclerView) rootView.findViewById(R.id.rv_steps_content);

        tvMenuIngredient = (TextView) rootView.findViewById(R.id.tv_menu_ingredient);
        tvMenuIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isIngredient = true;
                showStateView();
            }
        });

        tvMenuSteps = (TextView) rootView.findViewById(R.id.tv_menu_steps);
        tvMenuSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isIngredient = false;
                showStateView();
            }
        });


        if(recipe != null){
            //init ingredient view
            ListView ingredientList = (ListView)rootView.findViewById(R.id.lv_ingredient);
            IngredientAdapter ingredientAdapter = new IngredientAdapter(getContext(), recipe.getIngredients());
            ingredientList.setAdapter(ingredientAdapter);

            //init steps view
            LinearLayoutManager layoutManager= new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false);
            mStepAdapter = new StepAdapter();
            rvStepsContent.setLayoutManager(layoutManager);
            rvStepsContent.setNestedScrollingEnabled(false);
            rvStepsContent.setHasFixedSize(true);
            rvStepsContent.setAdapter(mStepAdapter);
            mStepAdapter.setStepsData(recipe.getSteps());
        }

    }

    private void showPreviousView(Bundle savedInstanceState){
        // get data from previous data
        ArrayList<Step> stepList = savedInstanceState.getParcelableArrayList(RECYCLERVIEW_DATA_STATE);

        // if no data get from state
        if(stepList != null){
            mStepAdapter.setStepsData(stepList);

            //set recyclerview position from previous position
            Parcelable listState = savedInstanceState.getParcelable(RECYCLERVIEW_STATE);
            rvStepsContent.getLayoutManager().onRestoreInstanceState(listState);
        }

        isIngredient = savedInstanceState.getBoolean(ISINGREDIENT_STATE);
        showStateView();
    }

    private void showStateView(){
        if(isIngredient){
            tvMenuIngredient.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            tvMenuSteps.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWetAsphalt));
            cvIngredientContent.setVisibility(View.VISIBLE);
            rvStepsContent.setVisibility(View.GONE);
        }
        else {
            tvMenuIngredient.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWetAsphalt));
            tvMenuSteps.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            cvIngredientContent.setVisibility(View.GONE);
            rvStepsContent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // save state isIngredient
        savedInstanceState.putBoolean(ISINGREDIENT_STATE, isIngredient);

        // save state list recyclerview
        Parcelable listState = rvStepsContent.getLayoutManager().onSaveInstanceState();
        savedInstanceState.putParcelable(RECYCLERVIEW_STATE, listState);

        // save state adapter data recyclerview
        savedInstanceState.putParcelableArrayList(RECYCLERVIEW_DATA_STATE,
                new ArrayList<>(mStepAdapter.getStepsData()));

        super.onSaveInstanceState(savedInstanceState);
    }
}
