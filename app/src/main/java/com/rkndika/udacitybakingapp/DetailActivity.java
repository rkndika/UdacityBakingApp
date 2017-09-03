package com.rkndika.udacitybakingapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.rkndika.udacitybakingapp.adapter.StepAdapter;
import com.rkndika.udacitybakingapp.model.Recipe;
import com.rkndika.udacitybakingapp.model.Step;
import com.rkndika.udacitybakingapp.util.WidgetSharedPreference;
import com.rkndika.udacitybakingapp.widget.BakingAppWidgetProvider;

public class DetailActivity extends AppCompatActivity implements StepAdapter.OnStepClickListener {
    private static final String SELECTED_POSITION = "exoplayerPosition";
    public final static String PUT_EXTRA_STEP = "putExtraStep";
    private final static String TYPE_PANE_STATE = "typePaneState";
    private final static String STEP_PLAY_STATE = "stepPlayState";
    private Recipe recipe;
    private boolean mTwoPane;
    private Step stepPlay;

    private static final String TAG = "DetailActivity";
    private SimpleExoPlayerView exoPlayerView;
    private SimpleExoPlayer player;
    private TextView mNoMediaMessage;

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private DataSource.Factory mediaDataSourceFactory;
    private Handler mainHandler;

    private WidgetSharedPreference wPref;
    private long position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        wPref = new WidgetSharedPreference(this);
        position = C.TIME_UNSET;

        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.containsKey(MainActivity.PUT_EXTRA_RECIPE)){
            // get recipe detail from clicked list
            recipe = getIntent().getExtras().getParcelable(MainActivity.PUT_EXTRA_RECIPE);
        }

        if(recipe == null){
            return;
        }

        setTitle(recipe.getName());

        if(findViewById(R.id.recipe_step_linear_layout) != null) {
            if(savedInstanceState != null){
                position = savedInstanceState.getLong(SELECTED_POSITION, C.TIME_UNSET);
                mTwoPane = savedInstanceState.getBoolean(TYPE_PANE_STATE);
                stepPlay = savedInstanceState.getParcelable(STEP_PLAY_STATE);
                setRightPanel(stepPlay);
            }
            else{
                mTwoPane = true;
                stepPlay = recipe.getSteps().get(0);
                setRightPanel(stepPlay);
                setRecipeFragment();
            }
        }
        else {
            mTwoPane = false;
            if(savedInstanceState == null){
                setRecipeFragment();
            }
        }
    }

    private void setRightPanel(Step step){
        exoPlayerView = (SimpleExoPlayerView) findViewById(R.id.sep_step_player);
        mNoMediaMessage = (TextView) findViewById(R.id.tv_no_media_display);
        TextView mDesc = (TextView) findViewById(R.id.tv_step_description);
        mDesc.setText(step.getDescription());

        if(step.getVideoURL().isEmpty()){
            showMedia(false);
            return;
        }

        showMedia(true);
        mediaDataSourceFactory = buildDataSourceFactory(true);
        mainHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        LoadControl loadControl = new DefaultLoadControl();

        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        exoPlayerView.setPlayer(player);
        exoPlayerView.setUseController(true);
        exoPlayerView.requestFocus();

        // Video Source
        Uri uri = Uri.parse(step.getVideoURL());

        // Default Media Source
        MediaSource mediaSource = buildMediaSource(uri, "mp4");

        if (position != C.TIME_UNSET) player.seekTo(position);

        player.prepare(mediaSource);

        player.setPlayWhenReady(true);

        player.addListener(new Player.EventListener() {
            @Override public void onTimelineChanged(Timeline timeline, Object manifest) {
                Log.d(TAG, "onTimelineChanged: ");
            }

            @Override public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.d(TAG, "onTracksChanged: " + trackGroups.length);
            }

            @Override public void onLoadingChanged(boolean isLoading) {
                Log.d(TAG, "onLoadingChanged: " + isLoading);
            }

            @Override public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.d(TAG, "onPlayerStateChanged: " + playWhenReady);
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                Log.d(TAG, "onRepeatModeChanged: ");
            }

            @Override public void onPlayerError(ExoPlaybackException error) {
                Log.e(TAG, "onPlayerError: ", error);
            }

            @Override public void onPositionDiscontinuity() {
                Log.d(TAG, "onPositionDiscontinuity: true");
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                Log.d(TAG, "onPlaybackParametersChanged: ");
            }
        });
    }

    private void setRecipeFragment(){
        Bundle bundle = new Bundle();
        bundle.putParcelable(MainActivity.PUT_EXTRA_RECIPE, recipe);
        DetailRecipeFragment detailRecipeFragment = new DetailRecipeFragment();
        detailRecipeFragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.detail_recipe_container, detailRecipeFragment);
        ft.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(TYPE_PANE_STATE, mTwoPane);
        outState.putParcelable(STEP_PLAY_STATE, stepPlay);
        outState.putLong(SELECTED_POSITION, position);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStepSelected(Step stepClicked) {
        if(mTwoPane){
            stepPlay = stepClicked;
            setRightPanel(stepClicked);
        }
        else {
            Intent i = new Intent(this, DetailStepActivity.class);
            i.putExtra(PUT_EXTRA_STEP, stepClicked);
            startActivity(i);
        }
    }

    private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
        int type = TextUtils.isEmpty(overrideExtension) ? Util.inferContentType(uri)
                : Util.inferContentType("." + overrideExtension);
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource(uri, buildDataSourceFactory(false),
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory), mainHandler, null);
            case C.TYPE_DASH:
                return new DashMediaSource(uri, buildDataSourceFactory(false),
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory), mainHandler, null);
            case C.TYPE_HLS:
                return new HlsMediaSource(uri, mediaDataSourceFactory, mainHandler, null);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource(uri, mediaDataSourceFactory, new DefaultExtractorsFactory(),
                        mainHandler, null);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    public DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(this, bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(Util.getUserAgent(this, "ExoPlayerDemo"), bandwidthMeter);
    }

    private void showMedia(Boolean status){
        if(status){
            mNoMediaMessage.setVisibility(View.GONE);
            exoPlayerView.setVisibility(View.VISIBLE);
        }
        else {
            mNoMediaMessage.setVisibility(View.VISIBLE);
            exoPlayerView.setVisibility(View.GONE);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.detail_menu, menu);

        // Check widget status
        Recipe recipePref = wPref.getData();
        if(recipePref != null && recipePref.getId().equals(recipe.getId()) ){
            menu.findItem(R.id.action_to_widget).setTitle(R.string.delete_from_widget);
        }

        /* Return true so that the menu is displayed in the Toolbar */
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            // finish activity when clicked back button on actionbar
            case android.R.id.home :
                finish();
                return true;
            case R.id.action_to_widget :
                addToWidget(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addToWidget(MenuItem item){
        if(item.getTitle().equals(getString(R.string.add_to_widget))){
            // Save data
            wPref.saveData(recipe);

            // Set widget
            int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), BakingAppWidgetProvider.class));
            Intent intent = new Intent(this, BakingAppWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
            sendBroadcast(intent);

            // Change menu title
            item.setTitle(R.string.delete_from_widget);
        }
        else {
            // Delete data
            wPref.resetData();

            // Set widget
            int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), BakingAppWidgetProvider.class));
            Intent intent = new Intent(this, BakingAppWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
            sendBroadcast(intent);

            // Change menu title
            item.setTitle(R.string.add_to_widget);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(stepPlay != null){
            setRightPanel(stepPlay);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            position = player.getCurrentPosition();
            player.stop();
            player.release();
            player = null;
        }
    }
}
