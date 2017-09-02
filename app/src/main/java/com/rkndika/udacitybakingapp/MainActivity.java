package com.rkndika.udacitybakingapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rkndika.udacitybakingapp.adapter.RecipeAdapter;
import com.rkndika.udacitybakingapp.model.Recipe;
import com.rkndika.udacitybakingapp.network.Client;
import com.rkndika.udacitybakingapp.network.Service;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeAdapterOnClickHandler{

    // final string for intent
    public static final String PUT_EXTRA_RECIPE = "recipe";

    // final string for state key
    private static final String STATE_RECYCLERVIEW_LIST = "recyclerViewListState";
    private static final String STATE_RECYCLERVIEW_DATA = "recyclerViewDataState";

    private RecyclerView mRecyclerView;
    private RecipeAdapter mRecipeAdapter;
    private SwipeRefreshLayout swipeContainer;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize views
        initViews();

        // if activity have savedInstanceState then showPreviousView
        if(savedInstanceState != null){
            showPreviousView(savedInstanceState);
        }
        // if activity haven't saveInstanceState
        else {
            // load data from API
            loadRecipesData();
        }
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_recipes);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns(), GridLayoutManager.VERTICAL, false);

        mRecipeAdapter = new RecipeAdapter(this);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mRecipeAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(R.color.colorPrimaryDark);

        // load new data from API
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRecipesData();
                swipeContainer.setRefreshing(false);
            }
        });
    }

    // get number of grid's coloum dynamically
    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 600;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        //if (nColumns < 2) return 2;
        return nColumns;
    }

    private void showPreviousView(Bundle savedInstanceState){
        // get data from previous data
        List<Recipe> recipesList = savedInstanceState.getParcelableArrayList(STATE_RECYCLERVIEW_DATA);

        // if no data get from state
        if(recipesList == null){
            showErrorMessage();
            return;
        }
        showRecipesDataView();

        // set data from previous data
        mRecipeAdapter.setRecipesData(recipesList);

        //set recyclerview position from previous position
        Parcelable listState = savedInstanceState.getParcelable(STATE_RECYCLERVIEW_LIST);
        mRecyclerView.getLayoutManager().onRestoreInstanceState(listState);

    }

    private void loadRecipesData() {
        showRecipesDataView();

        // show loading
        mLoadingIndicator.setVisibility(View.VISIBLE);

        // check network status
        if(!isNetworkAvailable()){
            // disable loading
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            // show no connection message
            showErrorNoIntenetMessage();
            return;
        }

        try {
            Client client = new Client();
            Service apiService = client.getClient().create(Service.class);

            Call<List<Recipe>> call = apiService.getRecipes();

            // get data asynchronously with Retrofit
            call.enqueue(new Callback<List<Recipe>>() {
                @Override
                public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                    // disable loading
                    mLoadingIndicator.setVisibility(View.INVISIBLE);

                    // if no error when fetching data
                    if(response.isSuccessful()){
                        // show recyclerview
                        showRecipesDataView();
                        // set data to adapter recyclerview
                        mRecipeAdapter.setRecipesData(response.body());
                    }
                    // something error when fetching data
                    else {
                        Log.d("Error", "Response code : " + String.valueOf(response.code()));
                        // show error message
                        showErrorMessage();
                    }
                }

                // Retrofit faulire access API
                @Override
                public void onFailure(Call<List<Recipe>> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    // disable loading
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                    // show error message
                    showErrorMessage();
                }
            });
        }catch (Exception e){
            Log.d("Error", e.getMessage());
            // disable loading
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            // show error message
            showErrorMessage();
        }
    }

    private void showRecipesDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setText(getString(R.string.error_message));
    }

    private void showErrorNoIntenetMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setText(getString(R.string.error_no_internet_message));
    }

    private void refreshRecipesData(){
        // delete all old data
        mRecipeAdapter.setRecipesData(null);

        // load new data from API
        loadRecipesData();
    }

    @Override
    public void onItemClick(Recipe recipeClicked) {
        Intent i = new Intent(MainActivity.this, DetailActivity.class);
        // put data as Parcelable
        i.putExtra(PUT_EXTRA_RECIPE, recipeClicked);
        startActivity(i);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        // save state list recyclerview
        Parcelable listState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        savedInstanceState.putParcelable(STATE_RECYCLERVIEW_LIST, listState);

        // save state adapter data
        savedInstanceState.putParcelableArrayList(STATE_RECYCLERVIEW_DATA,
                new ArrayList<>(mRecipeAdapter.getRecipesData()));

        super.onSaveInstanceState(savedInstanceState);
    }



    private boolean isNetworkAvailable(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
