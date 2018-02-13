package com.anditer.bakingapp.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anditer.bakingapp.R;
import com.anditer.bakingapp.adapter.IngredientAdapter;
import com.anditer.bakingapp.model.Recipe;
import com.anditer.bakingapp.model.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import static com.anditer.bakingapp.fragments.RecipeStepsFragment.EXTRA_POSITION;


public class StepDetailFragment extends Fragment implements ExoPlayer.EventListener {
    Recipe myRecipe;
    int position;
    long playerPosition;
    LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    IngredientAdapter adapter;
    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;
    BandwidthMeter bandwidthMeter;
    private Handler mainHandler;
    private ImageView mImageView;
    MediaSource mediaSource;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private String EXTRA_PLAYER_POSITION = "EXTRA_POSITION";

    public StepDetailFragment() {
        // Required empty public constructor
    }

    public static StepDetailFragment newInstance() {
        return new StepDetailFragment();

    }

    public void setMyRecipe(Recipe recipe) {
        this.myRecipe = recipe;
    }


    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bandwidthMeter = new DefaultBandwidthMeter();
        mainHandler = new Handler();
        if (savedInstanceState!=null && savedInstanceState.containsKey(Intent.EXTRA_TEXT)){
            myRecipe = savedInstanceState.getParcelable(Intent.EXTRA_TEXT);
        }
        if (savedInstanceState!=null){
            if (savedInstanceState.containsKey(Intent.EXTRA_TEXT))
                myRecipe = savedInstanceState.getParcelable(Intent.EXTRA_TEXT);
            if (savedInstanceState.containsKey(EXTRA_PLAYER_POSITION))
                playerPosition = savedInstanceState.getLong(EXTRA_PLAYER_POSITION);
            if (savedInstanceState.containsKey(EXTRA_POSITION))
                position = savedInstanceState.getInt(EXTRA_POSITION);
        }
        View view = null;
            if (position==0){

                 view = inflater.inflate(R.layout.ingredient_detail_fragment, container, false);
                recyclerView = view.findViewById(R.id.mRecipeIngredientRecycler);
                layoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(layoutManager);
                adapter = new IngredientAdapter(getContext(), myRecipe.getIngredients());
                recyclerView.setAdapter(adapter);


            }else{


                 view = inflater.inflate(R.layout.recipe_step_detail_fragment, container,false);

                TextView textView = view.findViewById(R.id.mStepDetailText);
                mImageView = view.findViewById(R.id.mStepDetailImage);
                mPlayerView = view.findViewById(R.id.mStepDetailVideoPlayer);
                Step step = myRecipe.getSteps().get(position);

                if (step.getVideoURL()!=null&& !step.getVideoURL().equals("")){
                    initMediaSession();

                    initializePlayer(Uri.parse(step.getVideoURL()));
                }
                else if (step.getThumbnailURL()!=null && !step.getThumbnailURL().equals("")){
                    mPlayerView.setVisibility(View.GONE);
                    mImageView.setVisibility(View.VISIBLE);
                    Picasso.with(getContext()).load(step.getThumbnailURL())
                            .placeholder(R.drawable.recipe_step)
                            .into(mImageView);
                }
                    textView.setText(step.getDescription());



            }



        return view;

    }


    private void initMediaSession() {

        mMediaSession = new MediaSessionCompat(getContext(), getContext().getClass().getSimpleName());

        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_FAST_FORWARD |
                                PlaybackStateCompat.ACTION_REWIND);

        mMediaSession.setMediaButtonReceiver(null);
        mMediaSession.setPlaybackState(mStateBuilder.build());

        mMediaSession.setCallback(new MySessionCallback());

        mMediaSession.setActive(true);

    }


    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
            DefaultTrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), getString(R.string.app_name));
            mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), mainHandler, null);

            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }else {
            mExoPlayer.seekTo(playerPosition);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);

        }

    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mExoPlayer!=null){
            playerPosition = mExoPlayer.getCurrentPosition();
            outState.putLong(EXTRA_PLAYER_POSITION, playerPosition);
        }
        outState.putParcelable(Intent.EXTRA_TEXT, myRecipe);
        outState.putInt(EXTRA_POSITION,position);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if((playbackState == ExoPlayer.STATE_READY)){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }



    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        releasePlayer();

    }


    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void releasePlayer() {
        if (mExoPlayer!=null){
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
        if (mMediaSession!=null){
            mMediaSession.setActive(false);
        }
    }


}
