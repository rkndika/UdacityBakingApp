package com.rkndika.udacitybakingapp.network;

import com.rkndika.udacitybakingapp.model.Recipe;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface Service {
    // get recipes
    @GET(Config.RECIPES)
    Call<List<Recipe>> getRecipes();
}
